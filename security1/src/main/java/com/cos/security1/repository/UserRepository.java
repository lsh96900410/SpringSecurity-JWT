package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//@Repository 어노테이션 X  IOC O -> JpaRepository 상속
public interface UserRepository extends JpaRepository<User, Integer> {
	
	public User findByUsername(String username); 
}
