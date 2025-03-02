package com.codeqna.repository;

import com.codeqna.entity.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    Board findByBno(Long bno);

    // 칼럼에 키워드값이 포함되어 있는 보드리스트
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:title% AND b.board_condition = :boardCondition")
    List<Board> findByTitleContaining(@Param("title") String keyword, @Param("boardCondition") String boardCondition);
    @Query("SELECT b FROM Board b WHERE b.content LIKE %:content% AND b.board_condition = :boardCondition")
    List<Board> findByContentContaining(@Param("content") String keyword, @Param("boardCondition") String boardCondition);

    @Query("SELECT b FROM Board b WHERE b.user.nickname LIKE %:nickname% AND b.board_condition = :boardCondition")
    List<Board> findByNicknameContaining(@Param("nickname") String keyword, @Param("boardCondition") String boardCondition);

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:title%")
    List<Board> findByBoardTitleContaining(@Param("title") String keyword);

    @Query("SELECT b FROM Board b WHERE b.user.nickname LIKE %:nickname%")
    List<Board> findByBoardNicknameContaining(@Param("nickname") String keyword);

    @Query("SELECT b FROM Board b WHERE b.regdate >= :start")
    List<Board> findByBoardRegdate(@Param("start") LocalDateTime start);

    @Query("SELECT b FROM Board b WHERE b.regdate BETWEEN :start AND :end")
    List<Board> findByBoardRegdateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Board b WHERE b.board_condition = :deleteCondition")
    List<Board> findByBoardconditionContaining(@Param("deleteCondition") String deleteCondition);

    @Transactional
    @Modifying
    @Query("update Board set hitcount = hitcount + 1 where bno = :bno")
    void incrementHitCount(@Param("bno") Long bno);

    @Query("SELECT b FROM Board b WHERE b.board_condition = 'N' AND b.bno < :bno ORDER BY b.bno DESC")
    List<Board> findPreviousActiveBoard(@Param("bno") Long bno, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.board_condition = 'N' AND b.bno > :bno ORDER BY b.bno ASC")
    List<Board> findNextActiveBoard(@Param("bno") Long bno, Pageable pageable);

    @Query("SELECT count(*) FROM Board b where b.board_condition = :boardCondition")
    long countByBoard_condition(String boardCondition);
}
