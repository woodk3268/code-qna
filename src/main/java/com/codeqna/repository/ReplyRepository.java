package com.codeqna.repository;

import com.codeqna.dto.LogsViewDto;
import com.codeqna.dto.RepliesViewDto;
import com.codeqna.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query(value = "SELECT * FROM reply WHERE bno = ?1 AND reply_condition LIKE ?2", nativeQuery = true)
    List<Reply> findByBnoAndReply_condition(Long bno, String condition);
    List<Reply> findByBoard_Bno(Long articleId);
    void deleteByIdAndUser_Email(Long articleCommentId, String email);

    void deleteByUser_Email(String email);


    Optional<Reply> findById(Long parentCommentId);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname) " +
            "FROM Reply r INNER JOIN r.user u WHERE u.nickname LIKE %:keyword%")
    List<RepliesViewDto> findRepliesByNickname(@Param("keyword") String keyword);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname) " +
            "FROM Reply r INNER JOIN r.user u WHERE r.content LIKE %:keyword%")
    List<RepliesViewDto> findRepliesByContent(@Param("keyword") String keyword);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.regdate >= :start")
    List<RepliesViewDto> findRepliesByRegdate(@Param("start") LocalDateTime start);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.delete_time >= :start")
    List<RepliesViewDto> findRepliesByDeletetime(@Param("start") LocalDateTime start);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.recover_time >= :start")
    List<RepliesViewDto> findRepliesByRecovertime(@Param("start") LocalDateTime start);


    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.regdate BETWEEN :start AND :end")
    List<RepliesViewDto> findRepliesByRegdateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.delete_time BETWEEN :start AND :end")
    List<RepliesViewDto> findRepliesByDeletetimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.recover_time BETWEEN :start AND :end")
    List<RepliesViewDto> findRepliesByRecovertimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT new com.codeqna.dto.RepliesViewDto(r,u.nickname)" +
            "FROM Reply r INNER JOIN r.user u WHERE r.reply_condition = :replyCondition")
    List<RepliesViewDto> findByReplyconditionContaining(@Param("replyCondition")  String replyCondition);

}
