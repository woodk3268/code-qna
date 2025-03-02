package com.codeqna.dto;

import com.codeqna.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Getter
public class AddBoardRequest {

    private String title;
    private String content;
    private String hashtag;
    private Long heart;
    private Long hitcount;
    private String nickname;
    private String board_condition;
    private List<UploadFileDto> files;

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .content(content)
                .hashtag(hashtag)
                .heart(heart)
                .hitcount(hitcount)
                .board_condition(board_condition)
                .build();
    }

}
