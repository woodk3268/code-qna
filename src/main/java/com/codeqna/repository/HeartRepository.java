package com.codeqna.repository;

import com.codeqna.entity.Board;
import com.codeqna.entity.Heart;
import com.codeqna.entity.Users;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    @Modifying
    @Query(value = "delete from heart where bno = :bno AND email = :email", nativeQuery = true)
    void deleteByEmail(Long bno, String email);

    @Query(value = "SELECT * FROM heart WHERE bno = :bno AND email = :email", nativeQuery = true)
    Heart isHeart(Long bno, String email);

    Boolean existsByUser_EmailAndBoard_Bno(String email, Long bno);
}
