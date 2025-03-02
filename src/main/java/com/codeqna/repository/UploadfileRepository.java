package com.codeqna.repository;

import com.codeqna.entity.Uploadfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadfileRepository extends JpaRepository<Uploadfile, Long> {
    // 여기에 필요한 추가적인 메서드 선언이 가능합니다.
    List<Uploadfile> findByBoard_Bno(Long bno);

    @Query(value = "SELECT * FROM uploadfile WHERE bno = :bno AND saved_file_name = :savedFileName", nativeQuery = true)
    Uploadfile findByOriginalFileName(@Param("savedFileName") String saved_file_name, @Param("bno") Long bno);
}
