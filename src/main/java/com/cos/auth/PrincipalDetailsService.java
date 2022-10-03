package com.cos.auth;

import com.cos.model.User;
import com.cos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행
@Service
public class PrincipalDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  // 로그인 폼에서 name이 loadUserByUsername(String username)에서 username의 변수명과 같아야 함
  // 시큐리티 session => Authentication => userDetails
  // 따라서 Security Session(내부 Authentication(내부 userDetails))
  // 함수 종료시 @Authentication 어노테이션이 만들어진다.
  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    System.out.println("username :" + username);
    User userEntity = userRepository.findByUsername(username);
    if (userEntity != null) {
      return new PrincipalDetails(userEntity);
    }
    return null;
  }
}
