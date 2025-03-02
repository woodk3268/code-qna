package com.codeqna.dto.response;


import com.codeqna.dto.ArticleCommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleCommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;
    private Long parentCommentId;

    private Set<ArticleCommentResponse> childComments;
    private String reply_condition;
    private String adopted;
    private Long adoption;

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname,String reply_condition,String adopted,Long adoption) {
        return ArticleCommentResponse.of(id, content, createdAt, email, nickname,null,reply_condition,adopted,adoption);
    }

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname,  Long parentCommentId,String reply_condition,String adopted,Long adoption) {
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                .comparing(ArticleCommentResponse::getCreatedAt)
                .thenComparingLong(ArticleCommentResponse::getId);
        return new ArticleCommentResponse(id, content, createdAt, email, nickname,  parentCommentId, new TreeSet<>(childCommentComparator),reply_condition,adopted,adoption);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {


        return ArticleCommentResponse.of(
                dto.getId(),
                dto.getContent(),
                dto.getRegdate(),
                dto.getUserDto().getEmail(),
                dto.getUserDto().getNickname(),
                dto.getParentCommentId(),
                dto.getReply_condition(),
                dto.getAdopted(),
                dto.getAdoption()
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }



}
