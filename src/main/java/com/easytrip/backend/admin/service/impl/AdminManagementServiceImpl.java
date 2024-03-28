package com.easytrip.backend.admin.service.impl;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminManagementService;
import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.exception.impl.InvalidStatusException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.MemberStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;

  @Override
  public void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    // 이미 설정하려는 상태일 경우, 잘못된 설정일 때
    if (member.getStatus().equals(memberStatus) || memberStatus == null
        || memberStatus.equals(MemberStatus.WITHDRAWN) || memberStatus.equals(MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new InvalidStatusException();
    }

    if (memberStatus.equals(MemberStatus.SUSPENDED)) {
      MemberEntity memberEntity = member.toBuilder()
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);

      // 정지된 회원이 작성한 게시글 삭제
      List<BoardEntity> posts = boardRepository.findByMemberId(member);
      for (BoardEntity post : posts) {
        BoardEntity board = post.toBuilder()
            .status(BoardStatus.INACTIVE)
            .build();
        boardRepository.save(board);
      }
    } else if (memberStatus.equals(MemberStatus.ACTIVE)) {
      MemberEntity memberEntity = member.toBuilder()
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);
    }
  }

  @Override
  public MemberDetailDto getMemberInfo(String accessToken, Long memberId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    MemberDetailDto memberDetailDto = MemberDetailDto.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .password(member.getPassword())
        .name(member.getName())
        .nickname(member.getNickname())
        .auth(member.getAuth())
        .imageUrl(member.getImageUrl())
        .introduction(member.getIntroduction())
        .status(member.getStatus())
        .adminYn(member.getAdminYn())
        .regDate(member.getRegDate())
        .build();

    return memberDetailDto;
  }
}
