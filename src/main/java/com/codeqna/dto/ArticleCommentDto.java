package com.codeqna.dto;


import com.codeqna.entity.Board;
import com.codeqna.entity.Reply;
import com.codeqna.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleCommentDto {
    private Long id;
    private Long articleId;
    private UserDto userDto;
    private Long parentCommentId;

    private String content;
    private LocalDateTime regdate;
    private String reply_condition;
    private String adopted;
    private Long adoption;



    public static ArticleCommentDto from(Reply entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getBoard().getBno(),
                UserDto.from(entity.getUser()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getRegdate(),
                entity.getReply_condition(),
                entity.getAdopted(),
                entity.getUser().getAdoption()

        );
    }

    public Reply toEntity(Board board, Users user) {
        return Reply.of(
                board,
                user,
                content
        );
    }
}
