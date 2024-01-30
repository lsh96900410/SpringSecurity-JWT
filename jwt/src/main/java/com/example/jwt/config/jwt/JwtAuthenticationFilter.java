package com.example.jwt.config.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/*
 * 	UsernamePasswordAuthenticationFilter 는 시큐리티 필터에 기본으로 존재
 * 	/login 요청해서 username 과 password (post) 전송하면 필터 동작  
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	// username,password 가 오면 그 정보를 가지고 로그인 작업을 처리하는 객체
	// todo -> OAuth2 로그인 경우는 ?
	private final AuthenticationManager authenticationManager;
	

	//login 요청을 하면 로그인 시도를 위해 실행되는 함수 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 로그인 시도중  ");
		
		try {
			// JSON 형식의 데이터를 User 타입으로 변환 -> JSON 데이터 구조와 User 클래스는 구조가 일치해야함, 틀릴경우 om 옵션사용하여 매핑
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(),User.class);

			System.out.println(user);
			
			// 2. 로그인 username, password 로 토큰 생성하기.
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

			/**
			 * 	[2] 에서 생성한 토큰 날리기 , 인증 로직
			 * 	: 1. 토큰의 username 으로 DB Find() 작업 수행
			 *
			 * 	: 2. 존재 여부
			 * 		- .1 : 존재한다면 사용자의 암호화된 비밀번호를 가져옴
			 * 		- .2 : 존재하지않다면 UsernameNotFoundException 발생
			 *
			 * 	: 3. 동일한 암호화 방식을 사용하여 암호화한 후, 2.1과 비교
			 *	: 4. 일치한다면 Authentication 객체 생성
			 *
			 *  3. AuthenticationManager 에 authenticationToken 을 제출하여 인증을 시도한다.
			 *  -> PrincipalDetailsService 의 loadUserByUserName() 실행 후 정상이면 Authentication 객체 리턴
			 *  -> 이 시점의 authentication 객체는 DB에 담긴 회원정보에 TOKEN 정보가 존재한다면 인증된 사용자의 정보를 포함
			 *  ==> 로그인 성공 표시
			 */
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			// 3. x 로그인 정상 처리 확인 작업
			PrincipalDetails principalDetails =(PrincipalDetails) authentication.getPrincipal();
			System.out.println("로그인 정상처리 : " + principalDetails.getUser().getUsername());

			/**
			 * 4. 로그인 정상 => 리턴 시점에 authentication 객체가 session 영역에 저장
			 * 	TODO [ CHECK ]
			 * 		리턴의 이유는 권한 관리를 Spring security 가 대신 해주기 떄문에 그저 편하려고?
			 * 		굳이 JWT 토큰을 사용하면서 SESSION 작업을 할 필요가 없지,,?
			 * 		그저 단지 Spring security 의 권한처리 작업 때문에 session 사용
			 */

			/*
			 * 	JWT Token 생성위치는 여기서도 가능
			 * 	but, attemptAuthentication 함수가 정상 처리되면
			 * 	successfulAuthentication 라는 함수가 실행이됨.
			 * 	이 함수가 실행되는 시점에 생성해주는 것이 현명.
			 */
			System.out.println(" attemptAuthentication 마지막 ");
			return authentication;
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
			return null;
		}
	}
	
	// attemptAuthentication 함수에서 정상적으로 인증이 되고, 메쏘드 종료 후 실행
	// todo 불필요한 인자가 너무 많다. 다른 방식은 존재하나? 있다면 방법은?
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		System.out.println("successfulAuthentication 함수 실행 == attemptAuthentication 에서 인증이 완료되었다는 뜻 ");

		PrincipalDetails principalDetails =(PrincipalDetails) authResult.getPrincipal();

		/**
		 *	.withClaim == 비공개 클레임, KEY Value 형식으로 담을 수 있음.
		 *  암호화방식(sign)
		 *  	: HMAC = 서버가 가지고있는 서명 사용
		 *  	: RSA  = 공캐기와 개인키 사용
		 */

		String jwtToken = JWT.create()
				.withSubject("내가 발행한 토큰") // username,password
				.withExpiresAt(new Date(System.currentTimeMillis()+(60000*10))) //토큰 발행 유효 시간 
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("skydog")); // jwt signature 암호화
		// todo secret 은 어떤 형식으로 작성하는 것이 좋을까?


		response.addHeader("Authorization","bearer "+jwtToken); // 한칸 띄워야함 
		//chain.doFilter(request, response);
		//super.successfulAuthentication(request, response, chain, authResult);
	}
}
