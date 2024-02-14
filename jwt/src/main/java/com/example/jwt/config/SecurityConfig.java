package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.example.jwt.config.jwt.JwtAuthenticationFilter;
import com.example.jwt.config.jwt.JwtAuthorizationFilter;
import com.example.jwt.filter.MyFilter1;
import com.example.jwt.filter.MyFilter3;
import com.example.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

	private final CorsFilter corsFilter;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//http.apply(new MyCustomDsl());

		//http.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class);
		// 커스텀필터는 시큐리티필터에 등록 불가 --> 특정 필터 이전 이후 실행으로 설정해줘야함 
		http.csrf(CsrfConfigurer::disable); // csrf 보호 비활성 , csrf 토큰 X
		http.sessionManagement(smCustomize ->
				smCustomize.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		http.addFilter(corsFilter);
		http.addFilter(new JwtAuthenticationFilter(authenticationManager));
		http.addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository));

		http.formLogin(formLoginConfigurer-> {
            try {
				formLoginConfigurer.disable().httpBasic(httpBasicConfigurer
						->httpBasicConfigurer.disable());
				// httpBasic 방식 : Header 영역에 id,pw 담음 vs http bearer 방식 :Header 영역에 token 담음
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

		http.authorizeHttpRequests(authorize ->
				authorize.requestMatchers("/api/v1/user/**").authenticated()
						 .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER","ADMIN")
						 .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						 .anyRequest().permitAll());

		return http.build();
	}
	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			System.out.println(" 메쏘드 실행중 !!!!!!!!!!!!!!!!!!");
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
			.addFilter(corsFilter)
			.addFilter(new JwtAuthenticationFilter(authenticationManager))
			.addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository));
		}
	}
	
}
