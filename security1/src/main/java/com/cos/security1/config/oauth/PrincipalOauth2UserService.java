package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로부터 받은 userRequest 데이터에 대한 후처리 함수 
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration == " + userRequest.getClientRegistration());
		// getClientRegistration 로 어떤 OAuth 로 로그인했는지 확인가능
		System.out.println("getAccessToken == " + userRequest.getAccessToken().getTokenValue());
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 구글로그인창 -> 로그인 완료 -> code 를 리턴 -> OAuth-client 라이브러리가 받음 -> AccessToken 요청
		// userRequest 정보 -> loadUser 메쏘드 호출 -> 회원 프로필을 받아줌
		System.out.println("getAttributes == " + oAuth2User.getAttributes());
		OAuth2UserInfo oAuth2UserInfo = null;
		// -> 코드 메소드로 분리하기
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("google");
			oAuth2UserInfo=new GoogleUserInfo(oAuth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("facebook");
			oAuth2UserInfo=new FacebookUserInfo(oAuth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("naver");
			oAuth2UserInfo=new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
		}else {
			System.out.println("sss");
		}
		// DTO 생성하기
		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String userName = provider+"_"+providerId;
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2UserInfo.getEmail();
		String role = "User";
		User user = userRepository.findByUsername(userName);

		// 첫 로그인인지 로그인 한 적이 있는지 체크
		if(user==null) {
			user = User.builder().email(email).password(password).provider(provider).providerId(providerId)
						  .role(role).username(userName).build();
			userRepository.save(user);
			System.out.println(" @@@@@@@@ 처음");
		}else {
			System.out.println("@@@@@ 한적있음 ");
		}
		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
}
