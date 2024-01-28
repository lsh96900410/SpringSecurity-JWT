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
	// username과password가 오면 그 정보를 가지고 로그인 작업을 처리하는 객체 
	private final AuthenticationManager authenticationManager;
	

	//login 요청을 하면 로그인 시도를 위해 실행되는 함수 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter : 로그인 시도중 !! ");
		
		// 1. username,password 받아서
		try {
//			BufferedReader br = request.getReader();
//			String test = null;
//			while((test=br.readLine())!=null) {
//				System.out.println(test);
//			}
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(),User.class);
			System.out.println(user);
			
			// 2. 로그인 username과 password 로 토큰 생성하기.
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			// 3. 토큰 날리기 ==> password는 스프링이 알아서 db 작업을 통해 처리해줌
			// --> PrincipalDetailsService의 loadUserByUserName 실행 후 정상이면 authenticaion 리턴 
			// 이 시점의 authenticaion == DB에 담긴 회원정보에 클라이언트가 날린 id,pw 가 존재 == 로그인 성공 표시
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			// 3. x 로그인 정상 처리 확인 작업
			PrincipalDetails principalDetails =(PrincipalDetails) authentication.getPrincipal();
			System.out.println("로그인 정상처리 : " + principalDetails.getUser().getUsername());
			// 4. 로그인 정상 => 리턴 시점에 authenticaion 객체가 session 영역에 저장
			// [ CHECK ]
			// 리턴의 이유는 권한 관리를 security가 대신 해주기 떄문에 그저 편하려고 
			// 굳이 JWT 토큰을 사용하면서 SESSION 작업을 할 필요가 없지..? 
			// 그저 단지 security의 권한처리 작업 때문에 session 사용 
			
			/*
			 * JWT Token 생성위치는 여기서도 가능 but, 이 attemptAuthentication 함수가 정상 처리되면
			 * 	successfulAuthentication 라는 함수가 실행이됨. 이 함수가 실행되는 시점에 생성해주는 것이 현명.
			 */
			System.out.println(" attemptAuthentication 마지막 ");
			return authentication;
		} catch (IOException e) {
			System.out.println("IOException 발생 !!!!!!!!!!!!!!!!!!! ");
			e.printStackTrace();
			return null;
		}
	}
	
	// attemptAuthentication 함수에서 정상적으로 인증이 되고, 메쏘드 종료 후 실행
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("attemptAuthentication에서 인증이 완료되었다는 뜻 ");
		PrincipalDetails principalDetails =(PrincipalDetails) authResult.getPrincipal();
		
		String jwtToken = JWT.create()
				.withSubject("내가 발행한 토큰") // username,password
				.withExpiresAt(new Date(System.currentTimeMillis()+(60000*10))) //토큰 발행 유효 시간 
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("skydog")); // jwt signature 암호화 
		//.withClaim == 비공개 클레임 , key value 형식으로 담을 수 있음 
		// 암호화 방식 : HMAC (서버가 가지고있는 서명 사용) vs RSA ( 공개키와 개인키 사용 )
		response.addHeader("Authorization","bearer "+jwtToken); // 한칸 띄워야함 
		//System.out.println("|||||||||||" + jwtToken);
		//chain.doFilter(request, response);
		//super.successfulAuthentication(request, response, chain, authResult);
	}
}
