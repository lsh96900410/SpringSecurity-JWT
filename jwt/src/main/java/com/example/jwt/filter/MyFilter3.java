package com.example.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request ; 
		HttpServletResponse res = (HttpServletResponse) response ; 
		
		// postman ex) 토큰 : skydog 이걸 만들어줘야함. 
		// 1. id,pw 가 정상적으로 들어와서 로그인이 완료되면 토큰 생성 후 응답
		// 2. 클라이언트 브라우저에 토큰이 저장 -> 요청할떄마다 header.Authorization에 value 값으로 토큰 존재
		// 3. 요청이 들어올때 이 토큰이 내가 발급해준 것이 맞는지 검증만 하면댐 (RSA ,HS256)
		
		
		if(req.getMethod().equals("POST")) {
			System.out.println("Post 요청됨");
			String headerAuth = req.getHeader("Authorization");
			System.out.println(headerAuth);
			
			// 인증 O -> 계속 진행 , 인증 X -> 필터에서 걸러냄(필터는 스프링으로 들어오기전에 실행 )
			if(headerAuth.equals("skydog")) {
				chain.doFilter(req, res);// 프로그램 종료 x , 계속 실행 
			}else {
				PrintWriter out = res.getWriter();
				out.print("인증 안댐 ㅜㅜㅜㅜ ");
			}
			
		}
		
		System.out.println("필터3");
	}

}
