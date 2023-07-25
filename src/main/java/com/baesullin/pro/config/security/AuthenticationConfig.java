/*package com.baesullin.pro.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity //
public class AuthenticationConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable() // 인증을 UI가 아니라 토큰인증으로..
                .csrf().disable()
                // 모름
                .csrf().ignoringAntMatchers("/h2-console/**")//h2 허용
                .and()
                .cors().and()// 모름
                .headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))//h2 허용
                .and()
                .authorizeRequests() // request를 authorize 하겠다
                .antMatchers("/review","/login", "/h2-console/**" ).permitAll() // 이 경로는 허용하겠다.
                .antMatchers(HttpMethod.POST,"/*").authenticated()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt사용하는 경우 사용
                .and()
                //.addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
 */
