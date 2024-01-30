package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

/**
 *  [ 그냥 규칙 ]
 *  1. /Login 요청이 들어옴 --> +  시큐리티 설정에서 loginProcessingUrl()에따라서 login 요청
 *  2. 'UsernamePasswordAuthenticationFilter' 가 작동
 *  3. UserDetailsService 의 loadUserByUsername() 메쏘드 호출 [ IOC ]
 *  4. 사용자의 정보를 로드하고 인증 작업을 수행
 */

@Service
public class PrincipalDetailsService implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;

	// username 명칭 동일해야함. 다를 경우에는 시큐리티설정에서 .usernameParameter("username") 써줘야함

	/**
	 *  1. form 태그에서 버튼 클릭 등등  ->
	 *  2. /login URL 로 요청 ->
	 *  3. IOC 는 UserDetailsService 타입으로 등록된 것 찾고 loadUserByUsername 메쏘드 실행 [String username]
	 *  4. 해당 메쏘드 정상 인증 처리 후 종료 시점 @AuthenticationPrincipal 어노테이션 사용 가능 , 예외 터질 수 있음
	 *  5. return [정상] 시점 : Security Session 내부 -> Authentication 객체 내부 -> UserDetails
	 *
	 *  @AuthenticationPrincipal : Spring Security 에서 현재 인증된(principal) 사용자의 정보를 주입하기 위해 사용
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User findUser = userRepository.findByUsername(username);
		System.out.println("loadUserByUserName 메쏘드.." +findUser.getRole()+findUser.getUsername());
		if(findUser != null) {
			System.out.println("findUser...." + findUser.getUsername());
			return new PrincipalDetails(findUser);
		}else{
			System.out.println("설마 여기가 찍히나??;;;;");
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
			//return null;
		}
	}

}
