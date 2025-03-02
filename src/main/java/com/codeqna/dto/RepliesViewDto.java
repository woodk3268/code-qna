package com.codeqna.dto;


import com.codeqna.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RepliesViewDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime regdate;
    private String reply_condition;
    private LocalDateTime delete_time;
    private LocalDateTime recover_time;


    public RepliesViewDto(Reply reply, String nickname){
        this.id = reply.getId();
        this.nickname= nickname;
        this.content = reply.getContent();
        this.regdate = reply.getRegdate();
        this.reply_condition = reply.getReply_condition();
        this.delete_time = reply.getDelete_time();
        this.recover_time = reply.getRecover_time();
    }
}
