package com.codeqna.repository;

import com.codeqna.entity.Board;
import com.codeqna.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByNickname(String nickname);
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    //쿼리모음
    @Query("SELECT u FROM Users u WHERE u.nickname LIKE %:nickname%")
    List<Users> findByNicknameContaining(@Param("nickname") String keyword);

    @Query("SELECT u FROM Users u WHERE u.email LIKE %:email%")
    List<Users> findByEmailContaining(@Param("email") String keyword);

    @Query("SELECT u FROM Users u WHERE u.kakao = :kakao")
    List<Users> findByKakaoContaining(@Param("kakao") String kakaoCondition);

    //    List<Users> findByKakao(String kakao);
    @Query("SELECT u FROM Users u WHERE u.regdate >= :start")
    List<Users> findLogsByRegdate(@Param("start") LocalDateTime start);

    @Query("SELECT u FROM Users u WHERE u.expiredDate >= :start")
    List<Users> findLogsByexpiredDate(@Param("start") LocalDateTime start);

    @Query("SELECT u FROM Users u WHERE u.regdate BETWEEN :start AND :end")
    List<Users> findLogsByRegdateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT u FROM Users u WHERE u.expiredDate BETWEEN :start AND :end")
    List<Users> findLogsByexpiredDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
