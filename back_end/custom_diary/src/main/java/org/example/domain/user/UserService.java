package org.example.domain.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.example.domain.gallery.service.FolderService;
import org.example.domain.user.dto.*;
import org.example.domain.user.entity.*;
import org.example.domain.user.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final TermRepository termRepo;
    private final UserTermRepository userTermRepo;
    private final GenreRepository genreRepo;
    private final UserGenreRepository userGenreRepo;
    private final ArtStyleRepository artStyleRepo;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseAuth firebaseAuth;
    private final FolderService folderService;

    @Transactional
    public SignupResponseDTO registerUser(SignupRequestDTO req) {
        // Firebase 토큰 검증
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(req.getIdToken());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase 토큰 검증 실패", e);
        }

        // 이미 존재하는 사용자 확인
        if (userRepo.existsByFirebaseUid(decodedToken.getUid())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        // 2) User 저장
        User user = new User(
                decodedToken.getUid(),
                decodedToken.getEmail(),
                req.getGender(),
                passwordEncoder.encode(req.getPassword()),
                req.getBirthDate()
        );
        userRepo.save(user);

        // ===> 2-1) 기본 폴더 초기화 (Bookmark, Weekly Webtoon)
        folderService.initializeDefaultFolders(uid);

        // 3) 약관 동의 저장
        for (TermAgreementDTO t : req.getTerms()) {
            Term term = termRepo.findByCode(t.getCode())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 약관 코드: " + t.getCode()));
            UserTerm ut = new UserTerm();
            UserTermKey key = new UserTermKey(); key.setUserId(uid); key.setTermId(term.getId());
            ut.setId(key);
            ut.setUser(user);
            ut.setTerm(term);
            ut.setAgreed(t.isAgreed());
            ut.setCreatedAt(LocalDateTime.now());
            userTermRepo.save(ut);
        }

        // 4) 장르 선택 저장
        for (Long gid : req.getGenreIds()) {
            Genre g = genreRepo.findById(gid)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장르 ID: " + gid));
            UserGenre ug = new UserGenre();
            UserGenreKey k2 = new UserGenreKey(); k2.setUserId(uid); k2.setGenreId(gid);
            ug.setId(k2);
            ug.setUser(user);
            ug.setGenre(g);
            userGenreRepo.save(ug);
        }

        ArtStyle style = new ArtStyle();
        // 5) 기본 그림체 프롬프트 저장
        try {
            style.setUser(user);
            style.setPrompt(objectMapper.writeValueAsString(req.getArtStylePrompt()));
            artStyleRepo.save(style);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("그림체 프롬프트 직렬화 실패", e);
        }

        // 6) 응답 작성
        SignupResponseDTO res = new SignupResponseDTO();
        res.setFirebaseUid(uid);
        res.setEmail(email);
        res.setGender(user.getGender());
        res.setBirthDate(user.getBirthDate());
        res.setAgreedTermCodes(
                req.getTerms().stream()
                        .filter(TermAgreementDTO::isAgreed)
                        .map(TermAgreementDTO::getCode)
                        .collect(Collectors.toList())
        );
        res.setGenreIds(req.getGenreIds());
        res.setArtStyleId(style.getId());

        return res;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO loginUser(LoginRequestDTO loginDTO) {
        // Firebase 토큰 검증
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(loginDTO.getIdToken());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Firebase 토큰 검증 실패", e);
        }

        String uid = decodedToken.getUid();
        User user = userRepo.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AuthResponseDTO res = new AuthResponseDTO();
        res.setFirebaseUid(uid);
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setPassword(user.getPassword());
        res.setBirthDate(user.getBirthDate());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }
}
