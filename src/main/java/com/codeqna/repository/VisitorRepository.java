package com.codeqna.repository;

import com.codeqna.entity.Board;
import com.codeqna.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByIpAddr(String ipAddr);

    //ip찍기
    @Query("SELECT v FROM Visitor v WHERE v.vDate = :vDate")
    List<Visitor> findByVDate(@Param("vDate") LocalDate vDate);

    //방문자 그래프
    @Query("SELECT v FROM Visitor v WHERE v.vDate BETWEEN :startDate AND :endDate")
    List<Visitor> findVisitorsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //게시물 그래프
    @Query("SELECT b FROM Board b WHERE b.regdate BETWEEN :startDate AND :endDate")
    List<Board> findBoardsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
