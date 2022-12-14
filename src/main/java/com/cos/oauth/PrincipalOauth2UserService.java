package com.cos.oauth;

import com.cos.auth.PrincipalDetails;
import com.cos.model.User;
import com.cos.oauth.provider.FacebookUserInfo;
import com.cos.oauth.provider.GoogleUserInfo;
import com.cos.oauth.provider.NaverUserInfo;
import com.cos.oauth.provider.OAuth2UserInfo;
import com.cos.repository.UserRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserRepository userRepository;

  // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
  // 함수 종료시 @Authentication 어노테이션이 만들어진다.
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest)
    throws OAuth2AuthenticationException {
    System.out.println("userRequest: " + userRequest.getClientRegistration()); //registrationId로 어떤 OAuth로 로그인 했는지 확인
    System.out.println(
      "userRequest: " + userRequest.getAccessToken().getTokenValue()
    );

    OAuth2User oauth2User = super.loadUser(userRequest);
    // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리)-> AccessToken 요청
    // userRequest 정보 -> loadUser함수 호출-> 구글로부터 회원프로필 받아줌
    System.out.println("userRequest: " + oauth2User.getAttributes());
    OAuth2UserInfo oAuth2UserInfo = null;
    if (
      userRequest.getClientRegistration().getRegistrationId().equals("google")
    ) {
      System.out.println("구글 로그인 요청");
      oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
    } else if (
      userRequest.getClientRegistration().getRegistrationId().equals("facebook")
    ) {
      System.out.println("페이스북 로그인 요청");
      oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
    } else if (
      userRequest.getClientRegistration().getRegistrationId().equals("naver")
    ) {
      System.out.println("네이버 로그인 요청");
      oAuth2UserInfo =
        new NaverUserInfo((Map) oauth2User.getAttributes().get("response"));
    } else {
      System.out.println("우리는 구글과 페이스북, 네이버만 지원합니다.");
    }

    if (oAuth2UserInfo != null) {
      String provider = oAuth2UserInfo.getProvider();
      String providerId = oAuth2UserInfo.getProviderId();
      String username = provider + "_" + providerId; // = google_123123812940712908
      String password = bCryptPasswordEncoder.encode("겟인데어");
      String email = oAuth2UserInfo.getEmail();
      String role = "ROLE_USER";

      User userEntity = userRepository.findByUsername(username);
      if (userEntity == null) {
        System.out.println("OAuth2 로그인이 처음입니다");
        userEntity =
          User
            .builder()
            .username(username)
            .password(password)
            .email(email)
            .role(role)
            .provider(provider)
            .providerId(providerId)
            .build();
        userRepository.save(userEntity);
      } else {
        System.out.println("OAuth2 로그인을 이미 한 적이 있습니다.");
      }

      return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    } else {
      return null;
    }
  }
}
