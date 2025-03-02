package com.codeqna.repository;

import com.codeqna.dto.LogsViewDto;
import com.codeqna.entity.Board;
import com.codeqna.entity.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogsRepository extends JpaRepository<Logs, Long> {
    Logs findByBoard(Board board);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE b.title LIKE %:keyword%")
    List<LogsViewDto> findLogsByTitle(@Param("keyword") String keyword);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE b.user.nickname LIKE %:keyword%")
    List<LogsViewDto> findLogsByNickname(@Param("keyword") String keyword);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE b.regdate >= :start")
    List<LogsViewDto> findLogsByRegdate(@Param("start") LocalDateTime start);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE l.delete_time >= :start")
    List<LogsViewDto> findLogsByDeletetime(@Param("start") LocalDateTime start);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE l.recover_time >= :start")
    List<LogsViewDto> findLogsByRecovertime(@Param("start") LocalDateTime start);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE b.regdate BETWEEN :start AND :end")
    List<LogsViewDto> findLogsByRegdateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE l.delete_time BETWEEN :start AND :end")
    List<LogsViewDto> findLogsByDeletetimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.codeqna.dto.LogsViewDto(b, l.delete_time, l.recover_time) " +
            "FROM Logs l INNER JOIN l.board b WHERE l.recover_time BETWEEN :start AND :end")
    List<LogsViewDto> findLogsByRecovertimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
