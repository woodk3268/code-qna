package com.codeqna.service;

import com.codeqna.dto.HeartDto;
import com.codeqna.entity.Board;
import com.codeqna.entity.Heart;
import com.codeqna.entity.Users;
import com.codeqna.repository.BoardRepository;
import com.codeqna.repository.HeartRepository;
import com.codeqna.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class HeartService {
    private final BoardRepository boardRepository;
    private final HeartRepository heartRepository;
    private final UserRepository userRepository;

    //-------------------------------------------------------------
    //게시물 좋아요
    @Transactional
    public void increaseHeart(HeartDto heartDto, String email) {
        Board board = boardRepository.findByBno(heartDto.getBno());
        //System.out.println("야임마! : " + heartDto.getNickname());
        board.increaseHeart();
        insertHeartUser(heartDto,email);
    }

    // 좋아요 클릭 시 heart 테이블에 nickname과 bno 저장
    public void insertHeartUser(HeartDto heartDto, String email) {
        Heart heart = new Heart();
        Users user = userRepository.findByEmail(email).orElseThrow();
        heart.setUser(user);

        Board board = boardRepository.findByBno(heartDto.getBno());
        heart.setBoard(board);
        heartRepository.save(heart);
    }
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    //게시물 좋아요 취소
    @Transactional
    public void decreaseHeart(HeartDto heartDto, String email) {

        Board board = boardRepository.findByBno(heartDto.getBno());


        board.decreaseHeart();
        deleteHeartUser(heartDto.getBno(), email);
    }

    public void deleteHeartUser(Long bno, String email) {
        heartRepository.deleteByEmail(bno, email);
    }
    //-------------------------------------------------------------

    //게시물 heart 눌렀는지 여부
    public Heart isHeart(HeartDto heartDto, String email) {


        return heartRepository.isHeart(heartDto.getBno(), email);
    }

}
