
###  Security Session  >  Authenticaion  >  PrincipalDetails [ UserDetails ( 홈페이지 ) , OAuth2User ( 외부 인증 ) ] 

###

# 🔎 JWT :  https://jwt.io/introduction

###
###  생성 시점 
Spring Security 의 UsernamePasswordAuthenticationFilter 에서 생성

attemptAuthentication 함수 ( login 요청 오면 실행 ) 에서 정상적으로 인증이 되면 successfulAuthenticaion 함수가 실행 이 함수에서 jwt 생성 

###  체크 
Spring Security의 BasicAuthenticationFilter ( 권한 및 인증이 필요한 주소 요청시 실행 ) 에서 체크 

통과 :  Authenticaion 객체 생성 후 Security Session에 저장 ( JWT 토큰 서명을 통과 했기에 가능 )


# 🔎 참고 자료 

###
![스프링 시큐리티](https://github.com/lsh96900410/SpringSecurity-JWT/assets/133841235/f38e79e1-79b2-4fc3-be79-5e0683bf614c)


###
![스프링 시큐리티 2](https://github.com/lsh96900410/SpringSecurity-JWT/assets/133841235/a0c3c6d3-172f-4dca-8864-5112df4d98c5)


