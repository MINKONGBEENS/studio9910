# Studio9910 - Toss 스타일 회원가입 플로우

## 프로젝트 개요
Toss 앱의 "one page one action" 디자인 원칙을 따른 Android 회원가입 플로우입니다.

## 주요 기능

### 회원가입 플로우
1. **약관 동의** - 계층적 체크박스 시스템
2. **이메일 입력** - 실시간 검증 및 중복 확인
3. **비밀번호 설정** - 3가지 조건 실시간 검증
4. **비밀번호 확인** - 일치 여부 확인
5. **개인정보 입력** - 성별 선택 및 생년월일 입력
6. **장르 선택** - 웹툰 장르 최대 5개 선택
7. **그림체 선택** - 6가지 그림체 중 1개 선택

## 아키텍처 및 코드 구조

### 베이스 클래스
- **BaseSignupActivity**: 모든 회원가입 액티비티의 공통 기능을 추상화
  - 공통 UI 요소 관리 (뒤로가기, 다음 버튼)
  - 데이터 전달 및 화면 전환 로직
  - 템플릿 메서드 패턴 적용

### 유틸리티 클래스
- **ValidationUtils**: 이메일, 비밀번호 검증 로직
- **UIUtils**: 공통 UI 업데이트 기능
  - 조건부 아이콘/텍스트 색상 변경
  - 언더라인 상태 관리
  - 선택 가능한 아이템 배경/텍스트 색상

### 회원가입 액티비티들
- **SignupEmailActivity**: 이메일 입력 및 실시간 검증
- **SignupPasswordActivity**: 비밀번호 설정 및 조건 검증
- **SignupPasswordConfirmActivity**: 비밀번호 확인
- **SignupGenreActivity**: 웹툰 장르 선택 (최대 5개)
- **SignupArtStyleActivity**: 그림체 선택 (1개)

## 주요 개선사항 (리팩터링)

### 1. 코드 재사용성 향상
- 공통 기능을 BaseSignupActivity로 추상화
- 중복 코드 제거 (약 60% 코드 감소)
- 템플릿 메서드 패턴으로 일관된 구조

### 2. 유지보수성 개선
- 검증 로직을 ValidationUtils로 분리
- UI 업데이트 로직을 UIUtils로 통합
- 단일 책임 원칙 적용

### 3. 확장성 증대
- 새로운 회원가입 단계 추가 시 BaseSignupActivity 상속만으로 구현 가능
- 공통 기능 수정 시 한 곳에서만 변경

### 4. 타입 안전성
- Kotlin의 null safety 활용
- when 표현식으로 조건 분기 명확화
- lateinit과 lazy 초기화 적절히 활용

## 기술 스택
- **언어**: Kotlin
- **UI**: XML Layout, Material Design
- **아키텍처**: Template Method Pattern, Utility Classes
- **라이브러리**: FlexboxLayout (장르 선택)

## 디자인 원칙
- **One Page One Action**: 각 화면마다 하나의 주요 액션
- **실시간 피드백**: 입력 즉시 검증 결과 표시
- **조건부 활성화**: 조건 충족 시에만 다음 버튼 활성화
- **일관된 UI**: 모든 화면에서 동일한 디자인 패턴

## 빌드 및 실행
```bash
./gradlew assembleDebug
```

## 프로젝트 구조
```
app/src/main/java/com/soze/studio9910/
├── BaseSignupActivity.kt          # 공통 베이스 클래스
├── SignupEmailActivity.kt         # 이메일 입력
├── SignupPasswordActivity.kt      # 비밀번호 설정
├── SignupPasswordConfirmActivity.kt # 비밀번호 확인
├── SignupGenreActivity.kt         # 장르 선택
├── SignupArtStyleActivity.kt      # 그림체 선택
└── utils/
    ├── ValidationUtils.kt         # 검증 로직
    └── UIUtils.kt                # UI 헬퍼 함수
```

## 주요 특징
- ✅ 완전한 기능 보존 (리팩터링 후에도 모든 기능 정상 작동)
- ✅ 코드 중복 제거 및 재사용성 향상
- ✅ 유지보수성 및 확장성 개선
- ✅ 타입 안전성 및 null safety
- ✅ 일관된 아키텍처 패턴 