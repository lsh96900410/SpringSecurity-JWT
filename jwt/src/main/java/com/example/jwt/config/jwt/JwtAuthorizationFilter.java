package com.example.jwt.config.jwt;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  Spring Security 가 가지고 있는 filter 중 BasicAuthenticationFilter
 *  권한이나 인증이 필요한 경우에만 실행되며, 필요한 경우에는 이 필터를 무조건 거치게 되어있음
 */

// JWT 토큰을 사용하여 요청된 주소에 대한 권한 및 인증 검사
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private UserRepository userRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	// 인증이나 권한이 필요한 주소요청일시 실행
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println(" 인증이나 권한이 필요한 주소 요청이여야 실행 ");
		String jwtHeader = request.getHeader("Authorization");
		// header 값 존재하는지 확인 , 조건 걸리면 바로 return 하여 다른필터 실행
		if (jwtHeader == null || !jwtHeader.startsWith("bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		// JWT 토큰 검증을 통해 정상적인 사용자인지 확인
		String jwtToken = request.getHeader("Authorization").replace("bearer ","");
		String username = JWT.require(Algorithm.HMAC512("skydog")).build().verify(jwtToken).getClaim("username")
						.asString();
		// 서명은 정상처리 완료
		System.out.println("username" + username);

		// JWT 인증 완료 == Spring Security 의 인증 과정 없이 직접 Authentication 객체 생성 후 SecurityContextHolder 에 주입
		/**
		 *  처음 로그인 유저 == JWT ? X -> SpringSecurity 인증 절차 후 JWT 발급되면 로직 실행
		 */

		if (username != null) {
			User findUser = userRepository.findByUsername(username);
			PrincipalDetails principalDetails = new PrincipalDetails(findUser);
			// Authentication 객체 ==> 로그인 과정을 통해 만들어지거나, 강제로 생성가능
			// 이 시점에서 강제로 생성 가능 이유 : JWT 토큰 서명을 통해 정상유저라는것을 확인 한 후이기에
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null,
					principalDetails.getAuthorities());
			// 시큐리티 세션에 접근하여 Authentication 객체 강제로 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);

	}

}
