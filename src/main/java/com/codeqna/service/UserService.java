package com.codeqna.service;

import com.codeqna.constant.UserRole;
import com.codeqna.dto.UserDto;
import com.codeqna.dto.UserFormDto;
import com.codeqna.dto.security.BoardPrincipal;
import com.codeqna.entity.Board;
import com.codeqna.entity.Users;
import com.codeqna.entity.Visitor;
import com.codeqna.repository.UserRepository;
import com.codeqna.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final VisitorRepository visitorRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public Optional<UserDto> searchUser(String email) {

        return userRepository.findByEmail(email)
                .map(UserDto::from);
        //repository에서 username으로 user 찾아옴. dto로 변환해서 반환
    }

    public Users saveUser(Users users) {
        validateDuplicateUser(users);
        return userRepository.save(users);
    }

    public UserDto saveKakaoUser(String email, String nickname, UserRole user_role) {
        Users user = new Users(null, email, nickname, null, user_role, "Y", null, "N", null,0L);
        return UserDto.from(userRepository.save(user));

    }

    private void validateDuplicateUser(Users users) {
        if (userRepository.existsByEmail(users.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다. 재가입을 원하시면 고객센터로 문의해주세요.");
        }
        if (userRepository.existsByNickname(users.getNickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return
                searchUser(email)
                        .map(BoardPrincipal::from)
                        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - email: " + email));
    }

    //닉네임 중복검사
    public boolean nicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    //닉네임변경
    public boolean updateNickname(String email, String newNickname) {
        Users users = userRepository.findByEmail(email).orElseThrow();
        if (users != null) {
            users.setNickname(newNickname);
            userRepository.save(users);
            return true;
        } else {
            return false;
        }
    }

//    public String getNicknameByEmail(String email) {
//        Users users = userRepository.findByEmail(email).orElseThrow();
//        return (users != null) ? users.getNickname() : null;
//    }

    //회원삭제
    public void deleteUser(String email) throws UsernameNotFoundException {
        Users users = userRepository.findByEmail(email).orElseThrow();
        if (users != null) {
            users.setUser_condition("Y");
            users.setExpiredDate(LocalDateTime.now());
            userRepository.save(users);
        } else {
            throw new UsernameNotFoundException("회원 탈퇴 오류");
        }
    }

    //회원관리페이지 설정--------------------------------------------------
    //전체회원불러오기
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    //회원검색조건
    public List<Users> searchUsers(String condition, String keyword){
        if (condition.equals("nickname")){
            return userRepository.findByNicknameContaining(keyword);
        }else {
            // 검색 조건이 잘못된 경우 처리
            throw new IllegalArgumentException("Invalid search condition: " + condition);
        }
    }

    //회원 복구
    public void restoreUser(String email) throws UsernameNotFoundException{
        Users users = userRepository.findByEmail(email).orElseThrow();
        if (users != null) {
            users.setUser_condition("N");
            users.setExpiredDate(null);
            userRepository.save(users);
        } else {
            throw new UsernameNotFoundException("회원 복구 오류");
        }
    }
    //회원 검색 by 가입일, 탈퇴일
    public List<Users> searchDateDeleteUsers(String condition, String start, String end) {
        LocalDateTime startDateTime = convertStringToLocalDateTime(start, false);

        if (end == null || end.isEmpty()) {
            if (condition.equals("regdate")) {
                return userRepository.findLogsByRegdate(startDateTime);
            } else if (condition.equals("expiredDate")) {
                return userRepository.findLogsByexpiredDate(startDateTime);
            }
        } else {
            LocalDateTime endDateTime = convertStringToLocalDateTime(end, true);

            // 기존의 날짜 범위 검색 메서드 호출
            if (condition.equals("regdate")) {
                return userRepository.findLogsByRegdateBetween(startDateTime, endDateTime);
            } else if (condition.equals("expiredDate")) {
                return userRepository.findLogsByexpiredDateBetween(startDateTime, endDateTime);
            }
        }

        // 검색 조건이 잘못된 경우 처리
        throw new IllegalArgumentException("Invalid search condition: " + condition);
    }

    // 회원 검색
    public List<Users> searchStringDeleteUsers(String condition, String keyword) {
        if (condition.equals("nickname")){
            return userRepository.findByNicknameContaining(keyword);
        }else if (condition.equals("email")){
            return userRepository.findByEmailContaining(keyword);
        }  else {
            // 검색 조건이 잘못된 경우 처리
            throw new IllegalArgumentException("Invalid search condition: " + condition);
        }

    }

    // 날짜 문자열을 LocalDateTime으로 변환하는 유틸리티 메서드
    private LocalDateTime convertStringToLocalDateTime(String dateStr, boolean isEndOfDay) {
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        return isEndOfDay ? LocalDateTime.of(localDate, LocalTime.MAX) : LocalDateTime.of(localDate, LocalTime.MIN);
    }


    // 카카오 라디오 검색
    public List<Users> searchRadioKakao(String kakaoCondition){
        return userRepository.findByKakaoContaining(kakaoCondition);
    }

    public boolean checkPassword(String email, String rawPassword) {

        Users user = userRepository.findByEmail(email).orElseThrow();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
