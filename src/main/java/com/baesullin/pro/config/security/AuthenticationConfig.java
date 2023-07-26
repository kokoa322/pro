package com.baesullin.pro.config.security;

import com.baesullin.pro.config.Jwt.JwtTokenFilter;
import com.baesullin.pro.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity //
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정

        return httpSecurity
                .httpBasic().disable() // 인증을 UI가 아니라 토큰인증으로..
                .csrf().disable()
                // 모름
                //.csrf().ignoringAntMatchers("/h2-console/**")//h2 허용
                //.and()
                .cors().and()// 모름
                //.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))//h2 허용
                //.and()
                .authorizeRequests() // request를 authorize 하겠다
                .antMatchers("/login", "/join", "/h2-console/**" ).permitAll() // 이 경로는 허용하겠다.
                .antMatchers(HttpMethod.POST,"/*").authenticated()
                //.anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt사용하는 경우 사용
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
