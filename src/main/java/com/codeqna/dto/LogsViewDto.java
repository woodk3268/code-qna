package com.codeqna.dto;

import com.codeqna.entity.Board;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Data
public class LogsViewDto {
    private Long bno;
    private String title;
    private String nickname;
    private LocalDateTime regdate;
    private Long hitcount;
    private Long heart;
    private String board_condition;
    private LocalDateTime deleteTime;
    private LocalDateTime recoveryTime;

    public LogsViewDto(Board board, LocalDateTime deleteTime, LocalDateTime recoveryTime) {
        this.bno = board.getBno();
        this.title = board.getTitle();
        this.nickname = board.getUser().getNickname();
        this.regdate = board.getRegdate();
        this.hitcount = board.getHitcount();
        this.board_condition = board.getBoard_condition();
        this.heart = board.getHeart();
        this.deleteTime = deleteTime;
        this.recoveryTime = recoveryTime;
    }

}
