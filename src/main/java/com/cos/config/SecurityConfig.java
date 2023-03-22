package com.cos.config;

import com.cos.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터(SecurityConfig.java)가 스프링 필터체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //securedEnabled => secured 어노테이션 활성화, prePostEnabled => preAuthorize 어노테이션 활성화, poseAuthorize 활성화
public class SecurityConfig {

  @Autowired
  private PrincipalOauth2UserService principalOauth2UserService;

  //해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
  // @Bean
  // public BCryptPasswordEncoder encoderPwd() {
  //   return new BCryptPasswordEncoder();
  // }

  @Bean
  AuthorizationManager<RequestAuthorizationContext> hasAdmin() {
    return AuthorityAuthorizationManager.hasRole("ROLE_ADMIN");
  }

  @Bean
  AuthorizationManager<RequestAuthorizationContext> hasAdminOrManager() {
    return AuthorityAuthorizationManager.hasAnyRole(
      "ROLE_ADMIN",
      "ROLE_MANAGER"
    );
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    throws Exception {
    return httpSecurity
      .csrf(c -> c.disable())
      .authorizeHttpRequests(req ->
        req
          .antMatchers("/user/**")
          .authenticated()
          .antMatchers("/manager/**")
          .access(hasAdminOrManager())
          .antMatchers("/admin/**")
          .access(hasAdmin())
          .anyRequest()
          .permitAll()
      )
      .formLogin(login ->
        login
          .loginPage("/loginForm")
          .loginProcessingUrl("/login") // login　주소가 호출이 되면 시큐라티가 낚아채서 대신 로그인을 진행해줌
          .defaultSuccessUrl("/")
      )
      .oauth2Login(login ->
        login
          .loginPage("/loginForm")
          // 구글 로그인이 완료된 뒤의 후처리가 필요함
          // 1. 코드 받기
          // 2. 액세스 토큰(권한)
          // 3. 사용자 프로필 정보를 가져옴
          // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
          // 4-2. (이메일, 전화번호, 이름, 아이디)을 oauth로 받고
          //      쇼핑몰의 경우, 집주소를
          //      백화점몰의 경우 vip 등급, 일반등급 같은 정보가 추가로 필요함
          //
          // Tip. 코드x, (액세스토큰 + 사용자 프로필 정보 O)
          .userInfoEndpoint()
          .userService(principalOauth2UserService)
      )
      .build();
  }
}
