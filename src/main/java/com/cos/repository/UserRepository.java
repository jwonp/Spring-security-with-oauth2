package com.cos.repository;

import com.cos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRepository가 들고 있음.
//@Repository라는 어노테이션이 없어도 IoC됨. 이유는 JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {
  //findBy규칙 -> Username문법
  // select * from user where username = ?
  public User findByUsername(String username); //문법에 관한 건 구글에서 Jpa query method 검색 및 참고
  // ex)
  //select * from user where email = ?
  // public User findByEmail();

}
