package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.SecurityConfig;
import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	/*
	 * Spring Security 는 기본 세션에 일부분으로 Security Session 을 가진다.
	 * Security Session 에는 SecurityContext [ Authentication ] 객체만이 저장될수 있음.
	 * Authentication 객체에는 UserDetails , OAuth2User 타입 가능
	 * 일반 유저가 로그인을 하게되면 UserDetails 타입 , 소셜로그인 OAuth 로그인을 하게되면 OAuth2User 타입
	 * 그렇다면 컨트롤러 메쏘드에서 상황별로 다른 타입을 인젝션?
	 * 그것보다는 두 타입을 구현하는 클래스를 생성, 그 클래스에서 메쏘드 재정의, 컨트롤러는 그 클래스 DI
	 */

	/*
	 * 회원 정보를 얻는 2가지 방법
	 * 1. Authentication 타입 DI 후 접근
	 * 2. @AuthenticationPrincipal UserDetails,OAuth2User 타입 선언 후 접근
	 */

	@GetMapping("/test/login")
	public @ResponseBody String loginTest(Authentication authentication, // 의존성 주입
			@AuthenticationPrincipal PrincipalDetails userDetails) {
		// PrincipalDetails == UserDetails 상속
		// @AuthenticationPrincipal 어노테이션을 사용해서 세션 정보에 접근이 가능하다.
		PrincipalDetails contextHolder = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		System.out.println("/test/login ========================= ");
		PrincipalDetails pd = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("SecurityContextHolder[ Bean ] : " + contextHolder.getUser());
		System.out.println("Authentication [ Controller ]: " + pd.getUser());
		System.out.println("UserDetails	: " + userDetails.getUser());
		return " 세션 정보 확인하기 ";
	}

	@GetMapping("/test/oauth/login")
	public @ResponseBody String loginTest(Authentication authentication // 의존성 주입
			, @AuthenticationPrincipal OAuth2User oAuth2User) {
		System.out.println("/test/login ========================= ");
		OAuth2User ou = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : " + ou.getAttributes()); // 구글 회원 프로필정보
		System.out.println("oauth2User : " + oAuth2User.getAttributes());
		return "oauth 세션 정보 확인하기 ";
	}

	// 기본적으로 스프링 시큐리티 의존성을 추가하면 모든 경로의 요청에 login이 필요하다.
	@GetMapping({ "", "/" })
	public String index() {
		return "index";
	}

	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails pd) {
		System.out.println("principalDetails : " + pd.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin(@AuthenticationPrincipal PrincipalDetails pds) {
		System.out.println("indext Controller ADMIN........ " + pds.getUsername() +pds.getAuthorities());
		System.out.println("index Controller ADMIN.... 찍혀야할텐데....");
		return "admin ";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	// 스프링 시큐리티 기본 url로 변경
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		user.setRole("User");
		// userRepositoy.save(user) --> 비밀번호 암호화가 안되어있기때문에 시큐리티로 로그인 못함..
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // 비밀번호 암호화작업
		userRepository.save(user);
		return "redirect:/loginForm";
	}

	@Secured("ADMIN") // 메쏘드 개별로 적용
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize(value = "ADMIN")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이타정보";
	}
}
