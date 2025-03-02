package com.codeqna.controller;


import com.codeqna.dto.ParentReplyDto;
import com.codeqna.dto.request.ArticleCommentRequest;
import com.codeqna.dto.security.BoardPrincipal;
import com.codeqna.entity.Users;
import com.codeqna.repository.UserRepository;
import com.codeqna.service.ArticleCommentService;
import com.codeqna.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReplyApiController {

    private final ArticleCommentService articleCommentService;
    private final UserRepository userRepository;



    //  댓글 생성
    @PostMapping("/api/comments")
    public ResponseEntity<ArticleCommentRequest> create(
                                                        @RequestBody ArticleCommentRequest articleCommentRequest,
                                                        @AuthenticationPrincipal BoardPrincipal principal) {
        String email  = principal.getName();
        Users user = userRepository.findByEmail(email).orElseThrow();
        articleCommentService.saveArticleComment(articleCommentRequest,email);

        return ResponseEntity.ok()
                .build();
    }



        //댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<ArticleCommentRequest> update(@PathVariable Long id,
                                                        @RequestBody ArticleCommentRequest articleCommentRequest,
                                                        @AuthenticationPrincipal BoardPrincipal principal) {
        String email  = principal.getName();
        Users user = userRepository.findByEmail(email).orElseThrow();

        articleCommentService.updateArticleComment(id,articleCommentRequest,email );
        // 결과 응답
        return ResponseEntity.ok()
                .build();
    }

    // 4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<ArticleCommentRequest> delete(@PathVariable Long id,
                                                        @AuthenticationPrincipal BoardPrincipal principal) {
        String email = principal.getName();
        // 서비스에 위임
        articleCommentService.deleteArticleComment(id,email);


        // 결과 응답
        return ResponseEntity.ok()
                .build();
    }



    //다중 삭제 처리
    @PostMapping("/replies/deleteReplies")
    public ResponseEntity<?> deleteReplies(@RequestBody List<Long> rnos){
        for (Long rno: rnos){
            articleCommentService.deleteReply(rno);
        }
        return ResponseEntity.ok()
                .build();
    }

    //다중 복구 처리
    @PostMapping("/replies/restoreReplies")
    public ResponseEntity<?> restoreReplies(@RequestBody  List<Long> rnos){
        for (Long rno: rnos){
            articleCommentService.restoreReply(rno);
        }
        return ResponseEntity.ok()
                .build();
    }



}
