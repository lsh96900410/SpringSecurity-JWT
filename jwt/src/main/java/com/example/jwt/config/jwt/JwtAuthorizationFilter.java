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

// 시큐리티가 가지고있는 filter 중 BasicAuthenticationFilter
// 권한이나 인증이 필요한 특정 주소를 요청했을 떄는 이 필터를 무조건 거치게 되어있음
// 권한,인증 X -> 이 필터는 안거침
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
		System.out.println("인증이나 권한이 필요한 주소 요청이여야 실행 ");
		String jwtHeader = request.getHeader("Authorization");
		System.out.println("JWT TOKEN :" + jwtHeader);
		// header값 존재하는지 확인 , 조건 걸리면 바로 return 하여 다른필터 실행
		if (jwtHeader == null || !jwtHeader.startsWith("bearer ")) {
			chain.doFilter(request, response);
			return;
		}
		//
		// JWT 토큰 검증을 통해 정상적인 사용자인지 확인
		String jwtToken = request.getHeader("Authorization").replace("bearer ","");
		System.out.println(jwtToken);
		JWT.require(Algorithm.HMAC512("skydog")).build().verify(jwtToken).getClaim("username");
		String username = JWT.require(Algorithm.HMAC512("skydog")).build().verify(jwtToken).getClaim("username")
						.asString();
		// 서명은 정상처리 완료
		System.out.println("username" + username);
		if (username != null) {
			User findUser = userRepository.findByUsername(username);
			System.out.println("권한" +findUser.getRoles());
			PrincipalDetails principalDetails = new PrincipalDetails(findUser);
			System.out.println("username@@@@@@"+principalDetails.getUser());
			// Authenticaion 객체 ==> 로그인 과정을 통해 만들어지거나, 강제로 생성가능
			// 이 시점에서 강제로 생성 가능 이유 :: JWT 토큰 서명을 통해 정상유저라는것을 확인 한 후이기에
			Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null,
					principalDetails.getAuthorities());
			// 시큐리티 세션에 접근하여 Authenticaion 객체 강제로 저장
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);

	}

}
