# local_inference.py

from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline, AutoTokenizer, AutoModelForSeq2SeqLM
import torch, requests, json, copy, random, os, logging, platform

app = FastAPI()
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# ───────────────────────────────────────────────────────────────────────────────
# 1) NLLB-200-1.3B 모델 로드 및 번역 파이프라인 설정
MODEL_PATH = "facebook/nllb-200-1.3B"
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH)
model     = AutoModelForSeq2SeqLM.from_pretrained(MODEL_PATH)

# 한국어 → 영어 번역 파이프라인
translator_ko_to_en = pipeline(
    "translation",
    model=model,
    tokenizer=tokenizer,
    device=0 if torch.cuda.is_available() else -1,
    src_lang="kor_Hang",
    tgt_lang="eng_Latn",
    num_beams=4,
    clean_up_tokenization_spaces=True
)
# 영어 → 한국어 번역 파이프라인
translator_en_to_ko = pipeline(
    "translation",
    model=model,
    tokenizer=tokenizer,
    device=0 if torch.cuda.is_available() else -1,
    src_lang="eng_Latn",
    tgt_lang="kor_Hang",
    num_beams=4,
    num_return_sequences=1,
    clean_up_tokenization_spaces=True
)
# ───────────────────────────────────────────────────────────────────────────────

class HFRequest(BaseModel):
    inputs: str  # 원문 한국어 다이어리 텍스트

OLLAMA_URL   = "http://127.0.0.1:11434"
OLLAMA_MODEL = "mistrallite:latest"

# JSON 템플릿
DEFAULT_TEMPLATE = {
    "char_info": {
        "gender":"", "importance":"", "age":"", "kind":"",
        "shape":"", "movement":"", "clothing":""
    },
    "background": {"background_info":""},
    "caption":""
}

def to_first_person(text: str) -> str:
    if text.startswith("He "):    return "I " + text[3:].replace(" his ", " my ")
    if text.startswith("She "):   return "I " + text[4:]
    if text.startswith("They "):  return "We " + text[5:]
    if text.startswith("The speaker "): return "I " + text[len("The speaker "):]
    if text.startswith("The person "):  return "I " + text[len("The person "):]
    if text.startswith("A person is "): return "I am " + text[len("A person is "):]
    if text.startswith("Someone is "):  return "I am " + text[len("Someone is "):]
    return "I " + text

def clean_english_for_translation(text: str) -> str:
    reps = {"ing411load":"playing", "USsection":"section", "US":"I"}
    for bad, good in reps.items():
        text = text.replace(bad, good)
    return text

@app.post("/generate")
def generate_text(request: HFRequest):
    logs = []
    final_list = []

    # 1) 한국어 문장 분리
    diary_ko = request.inputs.strip()
    segments = [s.strip() for s in diary_ko.split('.') if s.strip()]
    sentences_ko = [s + '.' for s in segments]
    logs.append(f"분리: {sentences_ko}")

    # 2) 한국어 → 영어 번역
    english_sentences = []
    for idx, ks in enumerate(sentences_ko):
        en = translator_ko_to_en(ks)[0]['translation_text'].strip()
        english_sentences.append(en)
        logs.append(f"[영어 번역 #{idx}] {en}")

    # 3) Ollama 호출 및 파싱
    collected = []
    for idx, en_sent in enumerate(english_sentences):
        prompt = (
            "You are a JSON-only assistant.\n"
            "Below is exactly one English sentence from a diary.\n"
            "Output exactly one JSON object with keys {\"background_info\":\"…\",\"caption\":\"…\"}.\n"
            "- background_info: summarize context in english (≤100 words).\n"
            "- caption: summarize sentence in english (≤60 characters).\n"
            "Return only that JSON object.\n\n"
            f"Sentence: \"{en_sent}\""
        )
        try:
            r = requests.post(
                f"{OLLAMA_URL}/v1/completions",
                json={"model":OLLAMA_MODEL, "prompt":prompt, "max_tokens":256, "temperature":0.0},
                timeout=300
            )
            r.raise_for_status()
            obj = json.loads(r.json()['choices'][0]['text'].strip())
            if isinstance(obj, dict) and 'background_info' in obj:
                collected.append(obj)
                logs.append(f"[파싱 성공 #{idx}] {obj}")
        except Exception as e:
            logs.append(f"[Ollama 예외 #{idx}] {e}")

    # 4) 최대 4개 샘플링
    if len(collected) > 4:
        collected = random.sample(collected, 4)
        logs.append(f"샘플링 후: {collected}")

    # 5) 후처리 및 한글 번역 (배경 + 캡션)
    for idx, entry in enumerate(collected):
        # 영어 원문
        bg_en  = entry['background_info'].strip()
        cap_en = entry['caption'].strip() or bg_en

        # 배경 한글 번역
        ko_bg = translator_en_to_ko(bg_en)[0]['translation_text'].strip()
        logs.append(f"[한글 배경 번역 #{idx}] {ko_bg}")

        # 캡션 1인칭 변환 + 길이 제한
        cap_fp = to_first_person(cap_en)
        if len(cap_fp) > 60:
            cap_fp = cap_fp[:57].rstrip() + '...'
        cap_clean = clean_english_for_translation(cap_fp)

        # 캡션 한글 번역
        ko_caption = translator_en_to_ko(cap_clean)[0]['translation_text'].strip()
        logs.append(f"[한글 캡션 번역 #{idx}] {ko_caption}")

        # JSON 템플릿에 데이터 채우기
        obj2 = copy.deepcopy(DEFAULT_TEMPLATE)
        obj2['background']['background_info'] = ko_bg
        obj2['caption'] = ko_caption
        final_list.append(obj2)

    # 6) 최종 반환 전 완성된 JSON도 로그에 추가
    logs.append(f"[최종 generated_text] {final_list}")

    return {"generated_text": final_list, "logs": logs}
