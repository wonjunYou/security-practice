package com.cos.security1.repository;

import com.cos.security1.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

// CRUD함수를 JpaRepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 JpaRepository를 상속받았기 때문에 IoC가능.
public interface UserRepository extends JpaRepository<User, Integer> {

    // findBy 규칙 -> Username 문법
    // select * from user where username = 1?
    // 물음표(?)에 parameter가 들어오게 된다.
    // 스프링 데이터 JPA 쿼리 메서드.
    User findByUsername(String username);

    // select * from user where email = ?
    // public User findByEmail();
}
