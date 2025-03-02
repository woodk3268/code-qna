package com.codeqna.service;


import com.codeqna.dto.ArticleCommentDto;
import com.codeqna.dto.LogsViewDto;
import com.codeqna.dto.RepliesViewDto;
import com.codeqna.dto.request.ArticleCommentRequest;
import com.codeqna.dto.response.ArticleCommentResponse;
import com.codeqna.entity.Board;
import com.codeqna.entity.Reply;
import com.codeqna.entity.Users;
import com.codeqna.repository.BoardRepository;
import com.codeqna.repository.ReplyRepository;
import com.codeqna.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;


    public Set<ArticleCommentResponse> searchArticleComments(Long articleId) {

        Map<Long, ArticleCommentResponse> map =  replyRepository.findByBoard_Bno(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::getId, Function.identity()));

        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.getParentCommentId());
                    parentComment.getChildComments().add(comment);
                });

        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::getAdopted).reversed()
                                .thenComparing(ArticleCommentResponse::getCreatedAt)
                                .thenComparingLong(ArticleCommentResponse::getId)
                        )
                ));



    }

    public void saveArticleComment( ArticleCommentRequest articleCommentRequest, String email) {
        try {


            Board board = boardRepository.findByBno(articleCommentRequest.getArticleId());
            Users user = userRepository.findByEmail(email).orElseThrow();
            Reply reply = Reply.of(board,user,articleCommentRequest.getContent());
            reply.setReply_condition("N");

            if (articleCommentRequest.getParentCommentId() != null) {
                Reply parentComment = replyRepository.findById(articleCommentRequest.getParentCommentId()).orElseThrow();
                parentComment.addChildComment(reply);
            } else {
                replyRepository.save(reply);
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticleComment(Long articleCommentId, String email) {
        Reply reply  = replyRepository.findById(articleCommentId).orElseThrow();
        reply.deleteReply();
        //  replyRepository.deleteByIdAndUser_Email(articleCommentId, email);

    }

    public void updateArticleComment(Long articleCommentId, ArticleCommentRequest articleCommentRequest, String email) {
        try {
            //article, useraccount 찾아옴
            Reply reply = replyRepository.getReferenceById(articleCommentId);
            Users user = userRepository.findByEmail(email).orElseThrow();
            //게시글 엔티티에서 꺼내온 useraccount 엔티티가, 수정하려는 작성자 useraccount 엔티티와 같으면
            if (reply.getUser().getEmail().equals(user.getEmail())) {
                //dto의 title,content가 null이 아니면 set

                if (articleCommentRequest.getContent() != null) { reply.setContent(articleCommentRequest.getContent()); }

            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 수정하는데 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }
    //댓글관리페이지 설정--------------------------------------------------
    //전체댓글불러오기
    public List<Reply> getAllReplies() {
        return replyRepository.findAll();
    }



    //댓글삭제
    public void deleteReply(Long id) throws EntityNotFoundException {
        Reply reply = replyRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        reply.setReply_condition("Y");
        reply.setDelete_time(LocalDateTime.now());
        reply.setRecover_time(null);
    }


    //댓글 복구
    public void restoreReply(Long id) throws EntityNotFoundException {
        Reply reply = replyRepository.findById(id).orElseThrow();
        reply.setReply_condition("N");
        reply.setRecover_time(LocalDateTime.now());
    }

    // 댓글관리에서 날짜검색 댓글 가져오기
    public List<RepliesViewDto> searchDateReplies(String condition, String start, String end) {
        LocalDateTime startDateTime = convertStringToLocalDateTime(start, false);

        if (end == null || end.isEmpty()) {
            if (condition.equals("regdate")) {
                return replyRepository.findRepliesByRegdate(startDateTime);
            } else if (condition.equals("deletetime")) {
                return replyRepository.findRepliesByDeletetime(startDateTime);
            } else if (condition.equals("recovertime")) {
                return replyRepository.findRepliesByRecovertime(startDateTime);
            }
        } else {
            LocalDateTime endDateTime = convertStringToLocalDateTime(end, true);

            // 기존의 날짜 범위 검색 메서드 호출
            if (condition.equals("regdate")) {
                return replyRepository.findRepliesByRegdateBetween(startDateTime, endDateTime);
            } else if (condition.equals("deletetime")) {
                return replyRepository.findRepliesByDeletetimeBetween(startDateTime, endDateTime);
            } else if (condition.equals("recovertime")) {
                return replyRepository.findRepliesByRecovertimeBetween(startDateTime, endDateTime);
            }
        }

        // 검색 조건이 잘못된 경우 처리
        throw new IllegalArgumentException("Invalid search condition: " + condition);
    }

    // 댓글 검색
    public List<RepliesViewDto> searchStringReplies(String condition, String keyword) {
        if (condition.equals("nickname")) {
            return replyRepository.findRepliesByNickname(keyword);
        } else if (condition.equals("content")) {
            return replyRepository.findRepliesByContent(keyword);
        } else {
            // 검색 조건이 잘못된 경우 처리
            throw new IllegalArgumentException("Invalid search condition: " + condition);
        }
    }

    // 날짜 문자열을 LocalDateTime으로 변환하는 유틸리티 메서드
    private LocalDateTime convertStringToLocalDateTime(String dateStr, boolean isEndOfDay) {
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        return isEndOfDay ? LocalDateTime.of(localDate, LocalTime.MAX) : LocalDateTime.of(localDate, LocalTime.MIN);
    }
    public List<RepliesViewDto> getReplies(){
        List<RepliesViewDto> RepliesViews = new ArrayList<>();
        List<Reply> Replies = replyRepository.findAll();
        for(Reply reply : Replies){
            RepliesViewDto repliesViewDto = new RepliesViewDto(reply, reply.getUser().getNickname());
            RepliesViews.add(repliesViewDto);
        }
        return RepliesViews;
    }

    // 댓글관리 라디오 검색
    public List<RepliesViewDto> searchRadioReplyBoards(String replyCondition){
        return replyRepository.findByReplyconditionContaining(replyCondition);
    }


}
