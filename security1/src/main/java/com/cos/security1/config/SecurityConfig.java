package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

/**
 *  1. 코드 받기(인증)
 *  2. 엑세스토큰(권한)
 *  3. 사용자 프로필정보를 가져옴
 *  4. 프로필 정보를 토대로 서비스 진행
 */

@Configuration // 시큐리티 설정파일 등록
@EnableWebSecurity // 스프링 시큐리티 필터 -> 스프링 필터체인에 등록 ()
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화 , preAuthorize,postAuthorize 어노테이션 활성화
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable); // CSRF 보호 비활성화
		http.authorizeHttpRequests(authorize -> authorize // HTTP 요청에 대한 인증 및 권한을 설정
				.requestMatchers("/manager/**").hasAnyRole("MANAGER","ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/user/**").authenticated() // 인증만 되면 사용 가능하다 (default 느낌)
				.anyRequest().permitAll());


		http.formLogin(customizer -> //loginPage == 로그인 페이지 및 인증되지 않았다면 이 URL로 Redirect
				customizer.loginPage("/loginForm").loginProcessingUrl("/login") // login 주소 호출 -> Spring Security 가 대신 로그인 진행해줌
						.defaultSuccessUrl("/")); // 로그인 성공 후 Redirect URL

		http.oauth2Login(oauth2Customizer ->
				oauth2Customizer.loginPage("/loginForm")
						.userInfoEndpoint(userInfoEndpointCustomize->
								// OAuth2.0 인증이 완료된 후 사용자 정보 얻기위한 엔드포인트 설정 
								//-> principalOauth2UserService 를 통해 사용자 정보 및 토큰 가져오기 가능
							userInfoEndpointCustomize.userService(principalOauth2UserService)));
		// -> 코드를 받는 것이아닌, 엑세스 토큰과 사용자 프로필 정보를 받음

		//http.authenticationProvider(new CustomAuthenticationProvider(userDetailsService,passwordEncoder))
		// 사용자가 원하는 인증방식에 따라 필요할 수 있음 --> TODO
		return http.build();
	}
	/*
	 * 기존: WebSecurityConfigurerAdapter를 상속하고 configure매소드를 오버라이딩하여 설정하는 방법
	 * 현재 : SecurityFilterChain을 리턴하는 메소드를 빈에 등록하는 방식(컴포넌트 방식으로 컨테이너가 관리)
	 * //https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
	 * 
	 * @Override
	 * protected void configure(HttpSecurity http) throws Exception{
	 * 		http.csrf().disable(); http.authorizeRequests()
	 * 			.antMatchers("/user/**").authenticated() .antMatchers("/manager/**")
	 * 			.access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	 * 			.antMatchers("/admin").access("\"hasRole('ROLE_ADMIN')")
	 * 			.anyRequest().permitAll(); }
	 */
}
