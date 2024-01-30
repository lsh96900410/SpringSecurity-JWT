package com.cos.security1.config.auth;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 1. Spring Security 는 /login 주소로 요청이 오면 UsernamePasswordAuthenticationFilter 가 이를 가로채서 로그인을 진행한다.
// 2. 로그인정보가 인증되면 Spring Security 는 시큐리티 session 을 생성한고, Security ContextHolder 에 저장한다.
// 3. 오브젝트 타입 => Authentication 타입 == key : Security ContextHolder , value : Authentication 타입  
// Authentication 안에 UserDetails 타입의 User 정보가 존재해야함  
// 즉, Security ContextHolder[ Security Session ]  >  Authentication 객체  >  UserDetails 타입 객체

// 따라서, Spring Security 는 사용자 인증이 성공하면 인증된 사용자 정보를 'UserDetails' 객체에 담아 'Authentication' 객체로 생성하고,
// 이를 Security ContextHolder 를 통해 Security Session 에 저장하여 관리합니다.

// 1. UserDetails == 사용자의 인증 및 권한 처리
// 2. OAuth2User == OAuth2 프로토콜을 통해 외부 서비스로부터 인증된 사용자의 정보

// Spring Security 에서 사용자의 인증 및 권한 정보를 담는 역할을 한다.
@Data
public class PrincipalDetails implements UserDetails,OAuth2User{

	private final User user; // composition ==> 생명주기가 동일하다.
	private final Map<String, Object> attributes; // OAuth2 정보

	//일반 로그인
	public PrincipalDetails(User user) {
		System.out.println(" 생성자 .... " + user.getRole());
		this.user=user;
		this.attributes=new HashMap<>();
	}

		// 소셜 로그인
	public PrincipalDetails(User user,Map<String, Object> attributes) {
		this.user=user;
		this.attributes=new HashMap<>(attributes);
	}


	// 해당 유저의 권한을 리턴하는 메쏘드.
	// user 의 권한은 String 타입 , 메쏘드 리턴타입 GrantedAuthority 타입
	// 사용자의 role 정보 --> GrantedAuthority 객체로 변환 [ String getAuthority() 존재 ] --> Collection 담고 리턴
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		System.out.println("@@@@@@@@@@@"+user.getRole() );
		System.out.println(collection.isEmpty());
		System.out.println(Arrays.toString(collection.stream().toArray()).charAt(0));
		return collection;
	}
	// 사용자의 패쓰워드 반환
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	// 사용자의 이름 반환
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정관해서 물어보는 것들 true -> 아니요~
	// 계정이 만료되지 않았는지 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정이 잠겨있지 않은지 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 자격 증명(비밀 번호) 등이 만료되지 않았는지 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	// 계정 활성화 여부
	public boolean isEnabled() {
		return true;
		// ex) 1년동안 로그인 X -> 휴먼
		// if (현재시간 - user.get로그인시간 > 1년 ) return false

	}

	/******************* OAuth2User  **************************/

	// OAuth2 사용자의 추가 속성 반환
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	// 사용자의 이름 반환
	@Override
	public String getName() {
		return null;
	}


}

