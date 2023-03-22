package com.cos.controller;

import com.cos.auth.PrincipalDetails;
import com.cos.model.User;
import com.cos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //View를 리턴하겠다
public class IndexController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping("/test/login")
  public @ResponseBody String testLogin(
    Authentication authentication,
    @AuthenticationPrincipal PrincipalDetails userDetails //DI(의존성 주입)
  ) {
    System.out.println("/test/login ============");
    if (authentication == null) {
      return "세션 정보가 없습니다.";
    }
    //Authentication
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    System.out.println("authentication: " + principalDetails.getUser());

    //@AuthenticationPrincipal
    System.out.println("userDetails:" + userDetails.getUser());

    //둘이 같은 정보를 가져옴
    return "세션 정보 확인하기";
  }

  @GetMapping("/test/oauth/login")
  public @ResponseBody String testOAuthLogin(
    Authentication authentication,
    @AuthenticationPrincipal OAuth2User oauth
  ) {
    System.out.println("/test/oauth/login ============");
    //Authentication
    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
    System.out.println("authentication: " + oauth2User.getAttributes());
    //@AuthenticationPrincipal
    System.out.println("oauth2User: " + oauth.getAttributes());

    //둘이 같은 정보를 가져옴
    return "세션 정보 확인하기";
  }

  @GetMapping({ "", "/" })
  public String index() {
    //머스테치 기본폴더 src/main/resources/
    //뷰리졸버 설정 : templates (prefix), .mustache (suffix) -> application.yml line.17~20 (생략가능)
    return "index"; // src/main/resources/ templates/index.mustache
  }

  //OAuth 로그인을 해도 PrincipalDetails
  //일반 로그인을 해도 PrincipalDetails
  @GetMapping("/user")
  public @ResponseBody String user(
    @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    System.out.println("principalDetails: " + principalDetails);
    return "user";
  }

  @GetMapping("/admin")
  public @ResponseBody String admin() {
    return "admin";
  }

  @GetMapping("/manager")
  public @ResponseBody String manager() {
    return "manager";
  }

  //SecurityConfig.java 설정 후 에 자동 생성된 login 페이지로 넘어가지 않음
  @GetMapping("/loginForm")
  public String loginForm() {
    System.out.println("loginForm");
    return "loginForm";
  }

  @GetMapping("/joinForm")
  public String joinForm() {
    return "joinForm";
  }

  @PostMapping("/join")
  public String join(User user) {
    System.out.println(user);
    user.setRole("ROLE_USER");
    String rawPassword = user.getPassword();
    String encPassword = bCryptPasswordEncoder.encode(rawPassword);
    user.setPassword(encPassword);
    //user.id user.createDate는 자동 생성
    userRepository.save(user); //회원가입은 잘됨. 비밀번호가 원문으로 저장됨 => 시큐리티로 로그인을 할 수 없음. 이유는 패스워드가 암호화가 안 되어있기 때문에

    return "redirect:/loginForm";
  }
  /*
    
   @Secured("ROLE_ADMIN") //하나만 걸고 싶을때
   @GetMapping("/info")
   public @ResponseBody String info() {
     return "개인정보";
   }
 
   @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //함수 시작 전에 실행, 2개 이상 걸고 싶을 때
   // @PostAuthorize()
   @GetMapping("/data")
   public @ResponseBody String data() {
     return "데이터 정보";
   }
   
   */
}
