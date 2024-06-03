package com.easytrip.backend.configuration;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

//package com.easytrip.backend.configuration;
//
//import com.easytrip.backend.member.domain.MemberEntity;
//import com.easytrip.backend.member.repository.MemberRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return memberRepository.findByEmail(username).map(this::createUserDetails)
                .orElseThrow( () -> new UsernameNotFoundException(username + "는 데이터베이스에 없는 데이터입니다."));

    }

    private UserDetails createUserDetails(MemberEntity memberEntity) {
        String role = String.valueOf(memberEntity.getAuth().booleanValue());
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

                return new User(
                        String.valueOf(memberEntity.getMemberId()),
                        memberEntity.getPassword(),
                        Collections.singleton(grantedAuthority)
                );
    }
//
//    private final MemberRepository memberRepository;
//
//    @Autowired
//    public CustomUserDetailsService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<MemberEntity> memberOptional = memberRepository.findByEmail(email);
//        if (!memberOptional.isPresent()) {
//            throw new UsernameNotFoundException("User not found with email: " + email);
//        }
//
//        MemberEntity member = memberOptional.get();
//        // 여기서 사용자의 정보를 UserDetails 객체로 변환하여 반환하면 됩니다.
//        // 예를 들어, org.springframework.security.core.userdetails.User 클래스를 사용할 수 있습니다.
//        // 여기서는 간단히 MemberEntity를 UserDetails로 변환하여 반환하겠습니다.
//        return org.springframework.security.core.userdetails.User.withUsername(member.getEmail())
//                .password(member.getPassword())
//                .build();
//    }
//}
}