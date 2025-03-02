package com.codeqna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParentReplyDto {
    private Long bno;
    private Long rno;
    private Long parentId;
    private String nickname;
    private String content;


}
