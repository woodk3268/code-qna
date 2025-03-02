package com.codeqna.dto;


import com.codeqna.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BoardViewDto {
    private Long bno;
    private String title;
    private String content;
    private String nickname;
    private Long heart;
    private Long hit_count;
    private String hashtag;
    private String board_condition;

    public BoardViewDto(Board board) {
        this.bno =board.getBno();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.nickname = board.getUser().getNickname();
        this.heart = board.getHeart();
        this.hit_count = board.getHitcount();
        this.hashtag = board.getHashtag();
        this.board_condition = board.getBoard_condition();
    }

}
