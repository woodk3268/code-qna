package com.codeqna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rno", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bno", nullable = false)
    private Board board;

//    @Column(name = "nickname")
//    private String nickname;

    @ManyToOne
    @JoinColumn(name = "email",referencedColumnName = "email")
    private Users user;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "regdate")
    private LocalDateTime regdate;

    @Column(updatable = false)
    private Long parentCommentId;

    @Column(name = "reply_condition", columnDefinition = "VARCHAR(5) DEFAULT 'N' ")
    private String reply_condition;


    @OrderBy("regdate ASC")
    @OneToMany(mappedBy = "parentCommentId",cascade = CascadeType.ALL)
    private Set<Reply> childComments = new LinkedHashSet<>();

    @Column(name = "delete_time")
    private LocalDateTime delete_time;

    @Column(name = "recover_time")
    private LocalDateTime recover_time;

    private String adopted;

    private Reply(Board board, Users user, Long parentCommentId, String content) {
        this.board = board;
        this.user = user;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.adopted = "N";

    }

    public static Reply of(Board board, Users user, String content) {
        return new Reply(board, user, null, content);
    }

    public void addChildComment(Reply child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    public void deleteReply(){
        this.reply_condition="Y";
        this.delete_time = LocalDateTime.now();
        this.recover_time = null;
    }




}
