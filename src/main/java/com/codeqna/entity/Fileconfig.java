package com.codeqna.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Fileconfig {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "max_file_num")
    private Integer max_File_Num;

    @Column(name = "max_file_size")
    private Integer max_file_Size;

    @Column(name = "file_ext")
    private String file_ext;
}
