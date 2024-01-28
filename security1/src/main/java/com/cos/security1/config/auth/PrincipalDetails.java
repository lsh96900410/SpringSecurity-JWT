package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 1. 시큐리티가 /login 주소로 요청이 오면 낚아채서 로그인을 진행한다.
// 2. 로그인정보가 일치하여 완료되면 시큐리티 session을 생성한다. ( Security ContextHolder)
// 3. 오브젝트 타입 => Authentication 타입 == key : Security ContextHolder , value : Authentication 타입  
// Authentication 안에 UserDetails 타입의 User 정보가 존재해야함  

/* 즉, Security Session > Authentication > UserDetails */
@Data
public class PrincipalDetails implements UserDetails,OAuth2User{
	
	private User user; // composition
	private Map<String, Object> attributes;
	
	public PrincipalDetails(User user) {
		//일반 로그인
		this.user=user;
	}
	
	public PrincipalDetails(User user,Map<String, Object> attributes) {
		// 소셜 로그인 
		this.user=user;
		this.attributes=attributes;
	}
	// 해당 유저의 권한을 리턴하는 메쏘드.
	// user의 권한은 String 타입 , 메쏘드 리턴타입 GrantedAuthority 타입 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>(); 
		// why ? ArrayList 
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		// String 타입의 User.getRole --> GrantedAuthority 타입으로 변환 --> Collection 담고 리턴 
		return collection;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	// 계정관해서 물어보는 것들 true -> 아니요~ 
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// ex) 1년동안 로그인 X -> 휴먼 
		// if (현재시간 - user.get로그인시간 > 1년 ) return false 
		
		return true;
	}
/******************* OAuth2User  **************************/
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}
 
	
}
