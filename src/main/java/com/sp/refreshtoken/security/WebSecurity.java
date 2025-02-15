package com.sp.refreshtoken.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Autowired
//    private Environment environment;

//
//    public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, Environment environment) {
//        this.userDetailsService = userDetailsService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.environment = environment;
//    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

//        authenticationManagerBuilder.userDetailsService(userDetailsService)
//                .passwordEncoder(bCryptPasswordEncoder);
//
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//
//        AuthenticationFilter authenticationFilter =
//                new AuthenticationFilter(authenticationManager, environment);
//        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));


//        http
////				.authorizeHttpRequests(
////						auth -> auth.requestMatchers("/h2-console/**").permitAll()
////				)
////				.authorizeHttpRequests(
////						auth -> auth.requestMatchers(HttpMethod.POST,"/signup").permitAll()
////				)
//                .authorizeHttpRequests(
//                        auth -> auth.anyRequest().permitAll()
//                )
//                .addFilter(new AuthorizationFilter(authenticationManager, environment))
//                .authenticationManager(authenticationManager)
//                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .csrf(conf -> conf.disable())
//                .headers(conf -> conf.frameOptions(foption -> foption.disable()));

        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll()// Or any other authorization rules
//                )
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/h2-console/**").permitAll()
                )
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .headers(conf -> conf.frameOptions(foption -> foption.disable()));


        return http.build();

    }

}