package com.codeqna.repository;

import com.codeqna.entity.Board;

import java.util.List;

public interface BoardRepositoryCustom {
    List<Board> findByHashtagsContaining(String[] keywords, String boardCondition);
}
