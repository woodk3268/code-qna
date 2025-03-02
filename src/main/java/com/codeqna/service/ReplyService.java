package com.codeqna.service;



import com.codeqna.dto.ParentReplyDto;
import com.codeqna.entity.Board;
import com.codeqna.entity.Reply;
import com.codeqna.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository repository;
    private final BoardService boardService;

    //특정 게시물에 등록된 댓글 가져오기
    //bno를 받아서 그 게시물의 댓글을 가져오고, 삭제되지 않은 댓글을 가져옴
    public List<Reply> findByBno(Long bno) {
        return repository.findByBnoAndReply_condition(bno, "N");
    }

    //댓글 등록
    public void addComment(ParentReplyDto parentReplyDto) {
        System.out.println("댓글 등록 성공띠");
        Reply reply = new Reply();
        //reply.setNickname(parentReplyDto.getNickname());
        reply.setContent(parentReplyDto.getContent());
        //bno만 가져올 순 없고 board 자체를 넣어야함
        Board board = boardService.findByBno(parentReplyDto.getBno());
        reply.setBoard(board);
        reply.setReply_condition("N");
        repository.save(reply);
    }

    public void updateComment(ParentReplyDto parentReplyDto) {
        Reply reply = repository.findById(parentReplyDto.getRno()).orElseThrow();


    }
}
