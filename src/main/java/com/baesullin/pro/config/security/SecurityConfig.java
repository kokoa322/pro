package com.baesullin.pro.config.security;

import com.baesullin.pro.login.oauth.entity.RoleType;
import com.baesullin.pro.login.jwt.repository.UserRefreshTokenRepository;
import com.baesullin.pro.login.oauth.service.CustomOAuth2UserService;
import com.baesullin.pro.common.properties.AppProperties;
import com.baesullin.pro.common.properties.CorsProperties;
import com.baesullin.pro.login.jwt.AuthTokenProvider;
import com.baesullin.pro.login.jwt.exception.RestAuthenticationEntryPoint;
import com.baesullin.pro.login.jwt.filter.TokenAuthenticationFilter;
import com.baesullin.pro.login.jwt.handler.TokenAccessDeniedHandler;
import com.baesullin.pro.login.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.baesullin.pro.login.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.baesullin.pro.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.baesullin.pro.login.oauth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    protected void filterChain(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors() // cors 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session을 사용하지 않을 것이기 때문에 stateless 설정 추가
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .disable() // csrf 설정 해제
                .formLogin().disable() // 소셜로그인만 이용할 것이기 때문에 formLogin 해제
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // 요청이 들어올 시, 인증 헤더를 보내지 않는 경우 401 응답 처리
                .accessDeniedHandler(tokenAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // cors 요청 허용
                .antMatchers("/", "/h2-console/**").permitAll() // 그 외 요청은 모두 허용
                .antMatchers("/review", "/api/bookmark", "/store/register", "/user").hasAnyAuthority(RoleType.USER.getCode(), RoleType.ADMIN.getCode())
                .antMatchers("/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                //.antMatchers("/**").permitAll() // 그 외 요청은 모두 허용
                .anyRequest().authenticated() // 위의 요청 외의 요청은 무조건 권한검사
                .and()
                .oauth2Login() // auth2 로그인 활성화
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler()) // 요청 성공 시 처리
                .failureHandler(oAuth2AuthenticationFailureHandler()); // 요청 실패 시 처리

        // tokenAuthenticationFilter 가 UsernamePasswordAuthenticationFilter 보다 먼저 실행되도록 하는 메소드
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    public void filterChain(WebSecurity web)throws Exception{
        web.ignoring().antMatchers("/h2-console/**");
    }
    /*
     * auth 매니저 설정
     * */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("authenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * security 설정 시, 사용할 인코더 설정
     * */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        System.out.println("passwordEncoder");
        return new BCryptPasswordEncoder();
    }

    /*
     * 토큰 필터 설정
     * */
    @Bean
    public  TokenAuthenticationFilter tokenAuthenticationFilter() {
        System.out.println("tokenAuthenticationFilter");
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        System.out.println("oAuth2AuthorizationRequestBasedOnCookieRepository");
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        System.out.println("oAuth2AuthenticationSuccessHandler");
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
                userRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        System.out.println("oAuth2AuthenticationFailureHandler");
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors 설정
     * */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        System.out.println("corsConfigurationSource");

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());


        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }
}