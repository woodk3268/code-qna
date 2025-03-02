package com.codeqna.repository;

import com.codeqna.entity.Fileconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileconfigRepository extends JpaRepository<Fileconfig, Integer> {
}
