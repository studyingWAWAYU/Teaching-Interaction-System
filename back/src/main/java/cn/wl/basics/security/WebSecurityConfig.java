package cn.wl.basics.security;

import cn.wl.basics.redis.RedisTemplateHelper;
import cn.wl.basics.security.jwt.*;
import cn.wl.basics.utils.SecurityUtil;
import cn.wl.basics.parameter.WlLoginProperties;
import cn.wl.basics.security.validate.ImageValidateFilter;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@ApiOperation(value = "SpringSecurity配置类")
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private WlLoginProperties WlLoginProperties;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailHandler authenticationFailHandler;

    @Autowired
    private WlAccessDeniedHandler wlAccessDeniedHandler;

    @Autowired
    private ImageValidateFilter imageValidateFilter;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(wlAccessDeniedHandler)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                //.addFilterBefore(imageValidateFilter, UsernamePasswordAuthenticationFilter.class) // 图形验证码（如需）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/wl/dictData/getByType/**",
                                "/wl/file/view/**",
                                "/wl/user/regist",
                                "/wl/user/getAll",
                                "/wl/common/**",
                                "/wl/course/getAll",
                                "/wl/course/getOne",
                                "/wl/course/count",
                                "/wl/course/getByPage",
                                "/wl/feedback/getAll",
                                "/wl/feedback/getOne",
                                "/wl/feedback/count",
                                "/wl/feedback/getByPage",
                                "/wl/course/*/topics/getAll",
                                "/wl/course/*/topics/getAll/sorted_by_likes",
                                "/wl/course/*/topics/count",
                                "/wl/course/*/topics/getOne",
                                "/wl/course/*/posts/getAll",
                                "/wl/course/*/posts/getAll/sorted_by_likes",
                                "/wl/course/*/posts/count",
                                "/wl/course/*/posts/getOne",
                                "/*/*.js", "/*/*.css", "/*/*.png", "/*/*.ico",
                                "/swagger-ui.html",
                                "/wl/login",
                                "/wl/common/needLogin"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/wl/common/needLogin")
                        .loginProcessingUrl("/wl/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailHandler)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userDetailsService.loadUserByUsername(username);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtTokenOncePerRequestFilter authenticationJwtTokenFilter() throws Exception {
        return new JwtTokenOncePerRequestFilter(redisTemplate, securityUtil, WlLoginProperties);
    }
}
