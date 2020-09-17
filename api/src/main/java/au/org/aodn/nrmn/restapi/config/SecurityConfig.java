//package au.org.aodn.nrmn.restapi.config;
//
////import au.org.aodn.nrmn.restapi.security.CustomUserDetailsService;
////import au.org.aodn.nrmn.restapi.security.JwtAuthenticationEntryPoint;
////import au.org.aodn.nrmn.restapi.security.JwtAuthenticationFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.BeanIds;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
//        securedEnabled = false,
//        jsr250Enabled = false,
//        prePostEnabled = true
//)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Value("${frontend.pages.whitelist}")
//    private String[] frontendPagesWhitelist;
//
//    @Autowired
//    CustomUserDetailsService customUserDetailsService;
//
//    @Autowired
//    private JwtAuthenticationEntryPoint unauthorizedHandler;
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();
//    }
//
//    @Override
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors()
//                .and()
//                .csrf()
//                .disable()
//                .exceptionHandling()
//                .authenticationEntryPoint(unauthorizedHandler)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/",
//                        "/favicon.ico",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js")
//                .permitAll()
//                .antMatchers(
//                        "/v3/api-docs/**",
//                        "/swagger-resources/**",
//                        "/swagger-ui.html")
//                .permitAll()
//                .antMatchers("/api/utils/**")
//                .permitAll()
//                .antMatchers("/api/auth/**")
//                .permitAll()
//                .antMatchers(HttpMethod.GET, "/api/user/me")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.PUT, "/api/user/me")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/user/{\\d+}")
//                .access("hasRole('ROLE_ADMIN')")
//                .antMatchers(HttpMethod.GET, "/api/user/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/organisation/**")
//                .permitAll()
//                .antMatchers(HttpMethod.PUT, "/api/user/**")
//                .access("hasRole('ROLE_ADMIN')")
//                .antMatchers(HttpMethod.POST, "/api/project/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.POST, "/api/installation/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/installation/**")
//                .permitAll()
//                .antMatchers(HttpMethod.POST, "/api/station/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/station/**")
//                .permitAll()
//                .antMatchers(HttpMethod.PUT, "/api/station/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/project/**")
//                .permitAll()
//                .antMatchers(HttpMethod.POST, "/api/receiver/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/receiver/**")
//                .permitAll()
//                .antMatchers(HttpMethod.POST, "/api/species/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/species/**")
//                .permitAll()
//                .antMatchers(HttpMethod.GET, "/api/tag/**")
//                .permitAll()
//                .antMatchers(HttpMethod.POST, "/api/tag")
//                .access("hasRole('ROLE_ADMIN')")
//                .antMatchers(HttpMethod.POST, "/api/tag/registerRelease/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.POST, "/api/tag/release/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.PUT, "/api/tag/release/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.PUT, "/api/tag/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.PUT, "/api/upload/**")
//                .access("hasRole('ROLE_USER')")
//                .antMatchers(HttpMethod.GET, "/api/detection/**")
//                .permitAll()
//                .antMatchers(HttpMethod.GET, "/storybook/**")
//                .permitAll()
//                .antMatchers(HttpMethod.GET, "/static/**")
//                .permitAll()
//                .antMatchers(HttpMethod.GET, frontendPagesWhitelist)
//                .permitAll()
//                .anyRequest()
//                .authenticated();
//
//        // Add our custom JWT security filter
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        // enable X-Frame-Options for storybook access
//        http.headers().frameOptions().disable();
//    }
//}
