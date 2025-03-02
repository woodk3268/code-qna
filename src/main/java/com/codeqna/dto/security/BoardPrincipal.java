package com.codeqna.dto.security;


import com.codeqna.constant.UserRole;
import com.codeqna.dto.UserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String nickname,
        Map<String, Object> oAuth2Attributes
) implements UserDetails, OAuth2User {

    public static BoardPrincipal of(String username, String password,  String nickname,  UserRole user_role) {
        //일반 회원
        return BoardPrincipal.of(username, password, nickname, user_role,Map.of());
    }

    public static BoardPrincipal of(String username, String password, String nickname,UserRole user_role, Map<String, Object> oAuth2Attributes) {

        //카카오 회원
        Set<UserRole> roleTypes = Set.of(user_role);

        return new BoardPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(UserRole::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                ,

                nickname,

                oAuth2Attributes
        );
    }

    public static BoardPrincipal from(UserDto dto) {
        //받아온 useraccountdto로 boardpoincipal 생성
        return BoardPrincipal.of(
              dto.getEmail(),
                dto.getPassword(),
                dto.getNickname(),
                dto.getUser_role()
        );
    }



    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override public Map<String, Object> getAttributes() { return oAuth2Attributes; }
    @Override public String getName() { return username; }


}
