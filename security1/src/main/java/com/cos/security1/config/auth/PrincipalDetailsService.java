package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

// 시큐리티 설정에서 loginProcessingUrl("/login")에 따라서 
// login 요청이 오면 자동으로 UserDetailsService 타입으로 IOC되어있는 loadUserByUsername 메쏘드 실행 [ 그냥 규칙 ]
@Service
public class PrincipalDetailsService implements UserDetailsService{

	// username 명칭 동일해야함. 다를 경우에는 시큐리티설정에서 .usernameParameter("username") 써줘야함
	
	// 1. form 태그에서 버튼 클릭 -> 2. /login url로 요청 -> 
	// 3. ioc는 UserDetailsService 타입으로 등록된 것 찾고 loadUserByUsername 메쏘드 실행 [String username]
	// 해당 메쏘드 종료시 @AutenticationPrincipal 어노테이션이 만들어진다.
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User findUser = userRepository.findByUsername(username);
		if(findUser != null) {
			return new PrincipalDetails(findUser);
		}
		return null;
	}
	// return 시  시큐리티 세션 내부 -> Authentication 객체 내부 -> UserDetails
	
}
