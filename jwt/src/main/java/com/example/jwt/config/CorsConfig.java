package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class CorsConfig {

	// --> 현재 서버는 cors 벗어남 crossOrigin 요청이와도 다 허용 
	// vs @CrossOrigin == 인증이 필요하지않은 요청만 허용, 인증이 필요한 요청은 해결 불가 	 
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 서버에서 응답할 때 json을 자바스크립트 (ajax,anxios,petch)에서 처리할수있게 할지 결정 
		config.addAllowedOrigin("*"); // 요청 들어오는 ip에 응답 허용
		config.addAllowedHeader("*"); // 요청 들어오는 header에 응답 허용
		config.addAllowedMethod("*"); // 요청 들어오는 httpMethod 허용 
		source.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(source);
	};

}
