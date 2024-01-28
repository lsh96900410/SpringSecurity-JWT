package com.example.jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.JwtApplication;
import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestApiController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}

	@PostMapping("token")
	public String token() {
		System.out.println("토큰 인증 완료 ! ");
		return "<h1> 토큰 인증된 페이지 !!!! </h1>";
	}

	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("USER");
		userRepository.save(user);

		return "회원가입완료";
	}

	@GetMapping("/api/v1/user")
	public String user(Authentication authentication) {
		PrincipalDetails principalDetails=(PrincipalDetails) authentication.getPrincipal();
		System.out.println("컨트롤러 authentication" + principalDetails.getUsername());
		return "user";
	}

	@GetMapping("/api/v1/manager")
	public String manager() {
		return "manager";
	}

	@GetMapping("/api/v1/admin")
	public String admin() {
		return "admin";
	}

}
