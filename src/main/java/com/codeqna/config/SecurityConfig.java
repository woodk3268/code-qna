package com.codeqna.config;

import com.codeqna.constant.UserRole;
import com.codeqna.dto.security.BoardPrincipal;
import com.codeqna.entity.Users;
import com.codeqna.repository.UserRepository;
import com.codeqna.service.UserService;
import com.codeqna.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;
import java.util.UUID;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VisitorService visitorService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService)
            throws Exception{

        http.csrf(csrf -> csrf.disable());

        http.formLogin((it) -> it
                .loginPage("/users/login")
                .defaultSuccessUrl("/main")
                .successHandler(customAuthenticationSuccessHandler())
                .usernameParameter("email")
                .failureUrl("/users/login/error")
        );

        http.logout((it) -> it
                .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                .logoutSuccessUrl("/main")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );
        http.oauth2Login(oAuth -> oAuth


                .userInfoEndpoint(endpoint -> endpoint
                        .userService(oAuth2UserService)
                )
                .successHandler(successHandler())
        );

        http.authorizeHttpRequests((req)-> {req
                .requestMatchers(antMatcher("/favicon.ico")).permitAll()
                .requestMatchers(antMatcher("/main")).permitAll()
                .requestMatchers(antMatcher("/users/**")).permitAll()
                .requestMatchers(antMatcher("/img/**")).permitAll()
                .requestMatchers(antMatcher("/vendor/**")).permitAll()
                .requestMatchers(antMatcher("/css/**")).permitAll()
                .requestMatchers(antMatcher("/handle-counter/**")).permitAll()
                .requestMatchers(antMatcher("/js/**")).permitAll()
                .requestMatchers(antMatcher("/scss/**")).permitAll()
                .requestMatchers(antMatcher("/boardAPI/**")).permitAll()
                .requestMatchers(antMatcher("/fileAPI/**")).permitAll()
                .requestMatchers(antMatcher("/viewboard/**")).permitAll()
                .requestMatchers(antMatcher("/newboard/**")).permitAll()
                .requestMatchers(antMatcher("/image_qna/**")).permitAll()
                .requestMatchers(antMatcher("/files/**")).permitAll()
                .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
//                .requestMatchers(antMatcher("/Loginmain")).hasAnyRole("USER","ADMIN")

                .anyRequest().authenticated();
        });

//ip찍기
        http.addFilterBefore(new VisitorFilter(visitorService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            String email = authentication.getName();
            Users users = userRepository.findByEmail(email).orElseThrow();
            if (users.getUser_condition().equals("N")) {
                response.sendRedirect("/main");
            } else {
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();
                response.sendRedirect("/users/login/expired");
            }
         //   response.sendRedirect("/Loginmain");
        });
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(userRepository);
    }



    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserService userService

    ) {

        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);


            Map<String, Object> attributes = oAuth2User.getAttributes();
            if (attributes.get("kakao_account") instanceof Map<?, ?> kakaoAccount) {
                String email = (String) kakaoAccount.get("email");
                String nickname = UUID.randomUUID().toString();

                //username으로 user 찾아서 useraccountdto를 boardprincipal 로 변환
                return userService.searchUser(email)
                        .map(BoardPrincipal::from)
                        .orElseGet(() ->
                                //회원 존재하지 않으면 username, password, email,nickname 넘겨서 useraccountdto 생성
                                //useraccountdto 를 boardprincipal 로 변환
                                BoardPrincipal.from(
                                        userService.saveKakaoUser(
                                                email,
                                                nickname,
                                                UserRole.USER
                                        )
                                )
                        );
            }
            return null;
        };
    }
}






