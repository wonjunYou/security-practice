package com.cos.security1.config.oauth;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private static final String ROLE_FOR_USER = "ROLE_USER";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest.getClientRegistration() : " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
        System.out.println("userRequest.getAccessToken().getTokenValue() : " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oauth2User = super.loadUser(userRequest);
        //userRequest : 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료시 code를 리턴(OAuth-Client라이브러리가 받는다) -> AccessToken 요청하여 받는것이다.
        //userRequest를 통해서 회원 프로필을 받아야 한다(loadUser함수 이용)
        System.out.println("userRequest : " + oauth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getClientId(); // "google"
        String providerId = oauth2User.getAttribute("sub");
        String username = provider + "_" + providerId; //google_1123123123123123 유저네임 충돌 방지.
        String password = bCryptPasswordEncoder.encode("test");
        String email = oauth2User.getAttribute("email");
        String role = ROLE_FOR_USER;

        User userEntity = userRepository.findByUsername(username);

        if (!isAlreadySignUp(userEntity)) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        }

        // 해당 객체가 Authentication 객체에 들어간다.
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }

    private boolean isAlreadySignUp(User userEntity) {
        if (userEntity == null) {
            return false;
        }

        return true;
    }
}
