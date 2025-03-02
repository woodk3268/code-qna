package com.codeqna.dto;


import com.codeqna.constant.UserRole;
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
public class UserDto {

        private Long id;
        private String email;
        private String nickname;
        private String password;
        private UserRole user_role;
        private String kakao;
        private LocalDateTime regdate;
        private String user_condition;

    public static UserDto of( String email, String nickname, String userPassword, UserRole user_role,String kakao, LocalDateTime regdate,String user_condition) {
        return new UserDto (null, email,  nickname,userPassword,user_role,kakao,regdate,user_condition);
    }
    public static UserDto of(Long userId,  String email, String nickname, String userPassword, UserRole user_role,String kakao, LocalDateTime regdate,String user_condition) {
        return new UserDto(userId, email,  nickname,userPassword,user_role,kakao,regdate,user_condition);
    }



    public static UserDto from(Users entity) {
        //useraccount entity 받아오면 useraccountdto로 변환해서 반환
        return new UserDto(
                 entity.getId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getPassword(),
                entity.getUser_role(),
                entity.getKakao(),
                entity.getRegdate(),
                entity.getUser_condition()
        );
    }


}


