package com.easytrip.backend.matching.service.impl;

import com.easytrip.backend.chatting.dto.request.ChatRoomDto;
import com.easytrip.backend.chatting.entity.ChatRoom;
import com.easytrip.backend.chatting.repository.ChatRoomRepository;
import com.easytrip.backend.chatting.service.ChatRoomService;
import com.easytrip.backend.exception.RoomExistException;
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

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    // 찾아온 회원의 관심사 찾아오기
    List<MemberInterestEntity> interestEntities = interestRepository.findAllByMemberId(member);

    // 현재 회원의 관심사들을 담을 Set 생성
    Set<Interest> interests = interestEntities.stream()
        .map(MemberInterestEntity::getInterest)
        .collect(Collectors.toSet());

    // 현재 회원과 두 개 이상의 관심사가 동일한 다른 회원 찾기
    Set<MemberEntity> matchingMembers = new HashSet<>();
    for (Interest interest : interests) {
      // 현재 관심사에 해당하는 회원의 관심사 목록을 가져옴
      List<MemberInterestEntity> membersInterestEntities = interestRepository.findAllByInterest(interest);

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
          matchingMembers.add(otherMember);
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

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity acceptingMember = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    MemberEntity likedMember = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    Optional<AcceptMemberEntity> byAcceptingMemberIdAndLikedMemberId = acceptMemberRepository.findByAcceptingMemberIdAndLikedMemberId(
        acceptingMember, likedMember);
    if (byAcceptingMemberIdAndLikedMemberId.isPresent()) {
      // 1 : 1 채팅방으로 연결
//      ChatRoomDto.Request request = new ChatRoomDto.Request();
//      request.setMatchedMember1(acceptingMember.getMemberId());
//      request.setMatchedMember2(likedMember.getMemberId());
//      chatRoomService.joinChatRoom(request);
//
//      // DB에 저장되어있던 매칭정보 삭제
//      AcceptMemberEntity acceptMember = byAcceptingMemberIdAndLikedMemberId.get();
//      acceptMemberRepository.delete(acceptMember);

      // 매칭 정보가 이미 존재하는 경우 채팅방을 생성하고 매칭 정보를 삭제하지 않고 반환합니다.
      ChatRoomDto.Request request = new ChatRoomDto.Request();
      request.setMatchedMember1(acceptingMember.getMemberId());
      request.setMatchedMember2(likedMember.getMemberId());
      chatRoomService.joinChatRoom(request);

      // 반환 전에 매칭 정보를 삭제하지 않음


      return;
    }

    // 1 : 1 채팅방이 있는지 확인
//    Optional<ChatRoom> byMember = chatRoomRepository.findByMember(acceptingMember.getMemberId(), likedMember.getMemberId());
//    if(byMember.isPresent()) {
//
//      throw new RoomExistException();
//
//    }

      // 없을 때 매칭정보 저장
      AcceptMemberEntity acceptMember = AcceptMemberEntity.builder()
              .acceptingMemberId(acceptingMember)
              .likedMemberId(likedMember)
              .build();
      acceptMemberRepository.save(acceptMember);

  }
}
