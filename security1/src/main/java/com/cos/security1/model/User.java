package com.cos.security1.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email; // -> @DiscriminatorValue("B") 상속받는 구조로 설계해보기 !
	private String role; // enum 타입 String 으로 엔티티 매핑
	
	private String provider;
	private String providerId;
	
	@CreationTimestamp
	private Timestamp createDate;
	
	@Builder
	public User( String username, String password, String email, String role, String provider, String providerId
			) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
	}
	
	
}
