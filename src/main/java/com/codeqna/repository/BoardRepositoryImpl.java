package com.codeqna.repository;

import com.codeqna.entity.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Board> findByHashtagsContaining(String[] keywords, String boardCondition) {
        StringBuilder queryBuilder = new StringBuilder("SELECT b FROM Board b WHERE b.board_condition = :boardCondition AND (");
        List<String> conditions = new ArrayList<>();

        for (int i = 0; i < keywords.length; i++) {
            conditions.add("b.hashtag LIKE :keyword" + i);
        }

        queryBuilder.append(String.join(" AND ", conditions)).append(")");
        TypedQuery<Board> query = entityManager.createQuery(queryBuilder.toString(), Board.class);

        query.setParameter("boardCondition", boardCondition);
        for (int i = 0; i < keywords.length; i++) {
            query.setParameter("keyword" + i, "%" + keywords[i] + "%");
        }

        return query.getResultList();
    }
}
