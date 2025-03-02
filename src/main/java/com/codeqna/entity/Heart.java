package com.codeqna.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hno", nullable = false)
    private Long hno;
//
//    @Column(name = "nickname")
//    private String nickname;

    @ManyToOne
    @JoinColumn(name = "email",referencedColumnName = "email")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "bno", nullable = false)
    private Board board;


}
