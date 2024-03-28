package com.easytrip.backend.admin.service.impl;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.admin.service.AdminManagementService;
import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.impl.ImageSaveException;
import com.easytrip.backend.exception.impl.InvalidSearchOptionException;
import com.easytrip.backend.exception.impl.InvalidStatusException;
import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.SearchOption;
import com.easytrip.backend.type.UseType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;
  private final ImageRepository imageRepository;

  @Override
  public void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    // 이미 설정하려는 상태일 경우, 잘못된 설정일 때
    if (member.getStatus().equals(memberStatus) || memberStatus == null
        || memberStatus.equals(MemberStatus.WITHDRAWN) || memberStatus.equals(
        MemberStatus.WAITING_FOR_APPROVAL)) {
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

    return MemberDetailDto.of(member);
  }

  @Override
  public MemberDetailDto updateMemberInfo(String accessToken, Long memberId,
      UpdateRequest updateRequest, MultipartFile file) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    MemberEntity updateMember = new MemberEntity();

    if (file.isEmpty() || file == null) {
      updateMember = member.toBuilder()
          .nickname(updateRequest.getNickname())
          .introduction(updateRequest.getIntroduction())
          .build();
    } else {
      String uuid = UUID.randomUUID().toString();
      String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\members";
      String fileName = uuid + "_" + file.getOriginalFilename();
      File saveFile = new File(projectPath, fileName);
      try {
        file.transferTo(saveFile);
      } catch (Exception e) {
        throw new ImageSaveException();
      }

      ImageEntity image = ImageEntity.builder()
          .fileName(fileName)
          .filePath(projectPath + "\\" + fileName)
          .useType(UseType.PROFILE)
          .memberId(member)
          .build();
      imageRepository.save(image);

      updateMember = member.toBuilder()
          .nickname(updateRequest.getNickname())
          .imageUrl(image.getFilePath())
          .introduction(updateRequest.getIntroduction())
          .build();
    }

    memberRepository.save(updateMember);

    return MemberDetailDto.of(updateMember);
  }

  @Override
  public List<MemberDetailDto> searchMember(String accessToken, String keyword,
      SearchOption searchOption) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    if (searchOption.equals(SearchOption.NAME)) {
      List<MemberEntity> byName = memberRepository.findByName(keyword);
      if (byName.isEmpty()) {
        throw new NotFoundMemberException();
      }

      return MemberDetailDto.listOf(byName);
    } else if (searchOption.equals(SearchOption.NICKNAME)) {
      MemberEntity member = memberRepository.findByNickname(keyword)
          .orElseThrow(() -> new NotFoundMemberException());

      List<MemberDetailDto> list = new ArrayList<>();
      list.add(MemberDetailDto.of(member));
      return list;
    }

    throw new InvalidSearchOptionException();
  }
}
