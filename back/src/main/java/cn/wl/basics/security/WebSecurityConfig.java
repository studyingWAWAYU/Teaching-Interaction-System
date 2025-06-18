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
    // 所有的请求都会经过securityFilterChain过滤器进行过滤
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 这里配置了免登录接口（白名单），不需要校验token值也能访问这些接口
        http.authorizeHttpRequests().requestMatchers("/wl/dictData/getByType/**",
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
                        "/*/*.js","/*/*.css","/*/*.png","/*/*.ico",
                        "/swagger-ui.html").permitAll()
                // 如果token校验失败就跳转到登录界面
                .and().formLogin()
                .loginPage("/wl/common/needLogin")
                // 配置登录接口
                .loginProcessingUrl("/wl/login").permitAll()
                // 配置登录成功的处理类 和 登录失败的处理类
                .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailHandler)
                // 允许iframe嵌套
                .and().headers().frameOptions().disable()
                // 配置注销
                .and().logout().permitAll()
                // 其他请求需要认证
                .and().authorizeHttpRequests()
                .anyRequest().authenticated()
                // 跨域配置
                .and().cors()
                // 禁用CSRF防护
                .and().csrf().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().exceptionHandling().accessDeniedHandler(wlAccessDeniedHandler)
                .and().authenticationProvider(authenticationProvider())
                // 自定义限流过滤器。防止白名单网站请求量太大导致服务器垮掉
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
                // 图形验证码的过滤器
                //.addFilterBefore(imageValidateFilter, UsernamePasswordAuthenticationFilter.class);
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
