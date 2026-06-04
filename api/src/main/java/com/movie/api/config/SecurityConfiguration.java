package com.movie.api.config;

import com.movie.api.auth.AuthorizationFilter;
import com.movie.api.model.support.ResponseResult;
import com.movie.api.utils.ResponseUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * SpringSecurity配置
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 6 不会自动把 AuthenticationManager 放进 HttpSecurity 的 sharedObject，
     * 必须显式注册 Bean，供 BasicAuthenticationFilter 子类构造使用。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // 开启跨域并禁用 csrf
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> {});

        // 允许跨域使用 iframe
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // 禁用 session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 身份验证失败
        http.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) ->
                ResponseUtil.writeJson(response, new ResponseResult<>(403, "身份认证失败, 请重新登录"))));

        // 保持原行为：仅注册授权过滤器，不额外限制请求鉴权规则
        http.addFilter(new AuthorizationFilter(authenticationManager));

        return http.build();
    }

    /**
     * SpringSecurity有默认的跨域配置 会无法放行RequestHeader带有"Authorization"请求
     * 防止前端请求api报出cors error
     *
     * @return *
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedHeader("DELETE");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
