package com.example.jwt.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.jwt.filter.MyFilter1;
import com.example.jwt.filter.MyFilter2;

//@Configuration
public class FilterConfig {
	/*
	 *  기본적으로 시큐리티필터체인이 기본 필터보다 먼저 실행되어짐 
	 *   커스텀 필터 등록 
	 *  1. SecurityConfig 파일에 필터 등록 --> 특정 필터보다 먼저 or 특정 필터 이후
	 *  2. FilterConfig 파일에 필터 등록  
	 */
	
	//@Bean
	public FilterRegistrationBean<MyFilter1> filter1(){
		FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
		bean.addUrlPatterns("/*"); // 어느 url 요청에 필터 적용할지 [모든 요청 ]
		bean.setOrder(0); // 필터 우선순위 설정,  낮을수록 먼저실행
		return bean;
	}
	//@Bean
	public FilterRegistrationBean<MyFilter2> filter2(){
		FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
		bean.addUrlPatterns("/*"); // 어느 url 요청에 필터 적용할지 [모든 요청 ]
		bean.setOrder(1); // 필터 우선순위 설정,  낮을수록 먼저실행
		return bean;
	}
}
