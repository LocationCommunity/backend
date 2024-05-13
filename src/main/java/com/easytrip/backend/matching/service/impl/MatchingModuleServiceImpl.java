package com.easytrip.backend.matching.service.impl;

import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.chatting.service.ChatRoomService;
import com.easytrip.backend.exception.impl.InvalidMatchingException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.matching.domain.AcceptMemberEntity;
import com.easytrip.backend.matching.domain.MemberInterestEntity;
import com.easytrip.backend.matching.dto.MatchingMemberDto;
import com.easytrip.backend.matching.repository.AcceptMemberRepository;
import com.easytrip.backend.matching.repository.InterestRepository;
import com.easytrip.backend.matching.service.MatchingModuleService;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.Interest;
import com.easytrip.backend.type.Platform;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingModuleServiceImpl implements MatchingModuleService {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final InterestRepository interestRepository;
  private final AcceptMemberRepository acceptMemberRepository;
  private final ChatRoomService chatRoomService;
  private final ChatRoomRepository chatRoomRepository;

  @Override
  public List<MatchingMemberDto> getMatchingList(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰으로 회원 찾아오기
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 찾아온 회원의 관심사 찾아오기
    List<MemberInterestEntity> interestEntities = interestRepository.findAllByMemberId(member);

    // 현재 회원의 관심사들을 담을 Set생성
    Set<Interest> interests = interestEntities.stream()
        .map(MemberInterestEntity::getInterest)
        .collect(Collectors.toSet());

    // 현재 회원과 두 개 이상의 관심사가 동일한 다른 회원 찾기
    Set<MemberEntity> matchingMembers = new HashSet<>();
    for (Interest interest : interests) {
      // 현재 관심사에 해당하는 회원의 관심사 목록을 가져옴
      List<MemberInterestEntity> membersInterestEntities = interestRepository.findAllByInterest(
          interest);

      // 현재 관심사에 해당하는 회원들을 추출하고, 현재 회원을 제외함
      List<MemberEntity> membersWithSameInterest = membersInterestEntities.stream()
          .map(MemberInterestEntity::getMemberId)
          .filter(m -> !m.equals(member))
          .collect(Collectors.toList());

      // 나머지 회원들을 공통된 관심사를 가진 회원으로 간주
      for (MemberEntity otherMember : membersWithSameInterest) {
        // 현재 회원과 공통된 관심사 개수 계산
        Set<Interest> otherMemberInterests = interestRepository.findAllByMemberId(otherMember)
            .stream()
            .map(MemberInterestEntity::getInterest)
            .collect(Collectors.toSet());

        // 현재 회원과 공통된 관심사 찾기
        otherMemberInterests.retainAll(interests);

        // 두 개 이상의 관심사가 동일한 경우에만 추가
        if (otherMemberInterests.size() >= 2) {
          // 이미 매칭 수락을 한 상대일 경우, 1 대 1 채팅방이 만들어진 상태일 때 제외
          Optional<AcceptMemberEntity> acceptMember = acceptMemberRepository.findByAcceptingMemberIdAndLikedMemberId(
              member, otherMember);
          Optional<ChatRoom> chatRoom = chatRoomRepository.findByMatchedMembers(member.getMemberId(),
              otherMember.getMemberId());
          if (acceptMember.isEmpty() && chatRoom.isEmpty()) {
            matchingMembers.add(otherMember);
          }
        }
      }
    }

    return MatchingMemberDto.listOf(matchingMembers);
  }

  @Override
  public void acceptMatching(String accessToken, Long memberId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity acceptingMember = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 매칭 상대가 자기 자신일 경우 exception
    if (acceptingMember.getMemberId().equals(memberId)) {
      throw new InvalidMatchingException();
    }

    MemberEntity likedMember = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    Optional<AcceptMemberEntity> byAcceptingMembers = acceptMemberRepository.findByAcceptingMemberIdAndLikedMemberId(
        likedMember, acceptingMember);

    // 동일한 회원가 중복매칭 시 exception
    Optional<AcceptMemberEntity> byAcceptingMemberIdAndLikedMemberId = acceptMemberRepository.findByAcceptingMemberIdAndLikedMemberId(
        acceptingMember, likedMember);
    if (byAcceptingMemberIdAndLikedMemberId.isPresent()) {
      throw new InvalidMatchingException();
    }

    if (byAcceptingMembers.isPresent()) {
      // 1 : 1 채팅방으로 연결

      
      ChatRoomDto.Request request = new ChatRoomDto.Request();
      request.setMatchedMember1(acceptingMember.getMemberId());
      request.setMatchedMember2(likedMember.getMemberId());
      chatRoomService.joinChatRoom(request);


      // DB에 저장되어있던 매칭정보 삭제
      AcceptMemberEntity acceptMember = byAcceptingMembers.get();
      acceptMemberRepository.delete(acceptMember);


      return;
    }

    // 1 : 1 채팅방이 있는지 확인
    Optional<ChatRoom> byMember = chatRoomRepository.findByMatchedMember1AndMatchedMember2OrMatchedMember1AndMatchedMember2(acceptingMember, likedMember, likedMember, acceptingMember);
    
    if(byMember.isPresent()) {
      throw new InvalidMatchingException();
    }

      // 없을 때 매칭정보 저장
      AcceptMemberEntity acceptMember = AcceptMemberEntity.builder()
              .acceptingMemberId(acceptingMember)
              .likedMemberId(likedMember)
              .build();
      acceptMemberRepository.save(acceptMember);
  }
}
