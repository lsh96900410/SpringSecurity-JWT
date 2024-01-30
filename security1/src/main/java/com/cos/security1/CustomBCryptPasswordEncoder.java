package com.cos.security1;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *  SpringSecurity 프레임워크에서 제공하는 클래스
 *   1. 비밀번호 암호호(해시)에 사용한다. -> 연속 사용시 다른 값 도출
 *   2. 제공 메소드
 *   암호화       : String encode(CharSequence rawPassword)
 *   일치 여부    : boolean matches(CharSequence rawPassword , String encodedPassword)
 *   안정성 체크   : boolean upgradeEncoding(String enodedPassword)
 */

@Component
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder{

}
