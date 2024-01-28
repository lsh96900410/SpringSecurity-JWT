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

//1. 코드 받기(인증)  2. 엑세스토큰(권한)  3. 사용자 프로필정보를 가져옴
//4. 프로필 정보를 토대로 서비스 진행 


@Configuration // 시큐리티 설정파일 등록
@EnableWebSecurity // 스프링 시큐리티 필터 -> 스프링 필터체인에 등록 ()
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
//secured 어노테이션 활성화 , preAuthorize,postAuthorize 어노테이션 활성화  
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalauth2UserService;
	
	// 해당 메써드의 리턴 오브젝트를 IOC로 등록해준다.
	/*
	 * @Bean public BCryptPasswordEncoder endoedPw() { return new
	 * BCryptPasswordEncoder(); }
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable);
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/user/**").authenticated() // 인증만 되면
				// requestMatchers , role 매칭?
				.requestMatchers("/Manager/**").hasAnyRole("Manager", "ADMIN").requestMatchers("/ADMIN/**")
				.hasRole("ADMIN").anyRequest().permitAll());
		http.formLogin().loginPage("/loginForm").loginProcessingUrl("/login") // login 주소 호출 -> 시큐리티가 대신 로그인을 진행해줌
				.defaultSuccessUrl("/");
		http.oauth2Login().loginPage("/loginForm")
		.userInfoEndpoint().userService(principalauth2UserService);	
		// 구글 로그인이 완료된 뒤의 후처리가 필요함
		// -> 코드를 받는 것이 아닌, 엑세트 토큰과 사용자 프로필정보를 받음 
		return http.build();
		

	}
	/*
	 * 기존: WebSecurityConfigurerAdapter를 상속하고 configure매소드를 오버라이딩하여 설정하는 방법 => 현재:
	 * SecurityFilterChain을 리턴하는 메소드를 빈에 등록하는 방식(컴포넌트 방식으로 컨테이너가 관리)
	 * //https://spring.io/blog/2022/02/21/spring-security-without-the-
	 * websecurityconfigureradapter
	 * 
	 * @Override protected void configure(HttpSecurity http) throws Exception{
	 * http.csrf().disable(); http.authorizeRequests()
	 * .antMatchers("/user/**").authenticated() .antMatchers("/manager/**").
	 * access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	 * .antMatchers("/admin").access("\"hasRole('ROLE_ADMIN')")
	 * .anyRequest().permitAll(); }
	 * 
	 */
}
