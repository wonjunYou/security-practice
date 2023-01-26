package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 구글 로그인이 완료된 후에 후처리가 필요함.
// 1. 코드 받기(인증 : 구글에 로그인이 된 정상적인 사용자)
// 2. 액세스 토큰(코드를 통해 받는다. 권한이 생김)
// 3. 권한을 통해 사용자 프로필 정보를 가져옴.
// 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
// 4-2. (이메일, 전화번호, 이름, ID) 외에 추가적인 정보가 필요한 경우
// -> 추가적인 회원가입 창이 나와서 회원가입을 진행시켜야 한다.
// Tip 구글 로그인 완료시 코드를 받는것이 아니라(엑세스토큰 + 사용자 프로필 정보를 바로 받는다.)
// OAuth2라는 라이브러리가 이러한 역할을 해준다.

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // preAuthorized(여러 권한), secured(1개 권한) 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록.
    /*
    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();

    */

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasAnyRole('MANAGER', 'ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .anyRequest().permitAll()
            .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
            .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);

        return http.build();
    }
}
