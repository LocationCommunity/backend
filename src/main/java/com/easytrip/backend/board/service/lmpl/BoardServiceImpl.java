package com.easytrip.backend.board.service.lmpl;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.domain.BoardLikeEntity;
import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.service.BoardService;
import com.easytrip.backend.exception.NotMyPostException;
import com.easytrip.backend.board.repository.BoardLikeRepository;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.UnsupportedImageTypeException;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.Platform;
import com.easytrip.backend.type.SearchOption;
import com.easytrip.backend.type.UseType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;


@Slf4j
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;
  private final BoardLikeRepository boardLikeRepository;
  private final ImageRepository imageRepository;
  private final PlaceRepository placeRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public void writePost(String accessToken, BoardRequestDto boardRequestDto,
      List<MultipartFile> files, Long placeId) throws Exception {

    validateAccessToken(accessToken);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(SelectPlaceException::new);

    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity memberplatform = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(NotFoundMemberException::new);

    String visitDateString = boardRequestDto.getVisitDate();
    visitDateString = visitDateString.replace("Z", "");
    LocalDateTime visitDateTime = LocalDateTime.parse(visitDateString);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    String formattedVisitDate = visitDateTime.format(formatter);

    BoardEntity board = BoardEntity.builder()
        .title(boardRequestDto.getTitle())
        .memberId(memberplatform)
        .nickname(memberplatform.getNickname())
        .content(boardRequestDto.getContent())
        .likeCnt(0)
        .viewCnt(0)
        .placeId(place)
        .status(BoardStatus.ACTIVE)
        .createDate(LocalDateTime.now())
        .visitDate(formattedVisitDate)
        .build();
    boardRepository.save(board);

    handleFileUpload(files, board);
  }

  @Transactional
  @Override
  public void updatePost(String accessToken, Long boardId, Long placeId,
      BoardRequestDto boardRequestDto, List<MultipartFile> files) {

    validateAccessToken(accessToken);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = getAuthenticatedMember(authentication, platform, boardId);
    BoardEntity board = boardRepository.findByBoardId(boardId)
            .orElseThrow(NotFoundPostException::new);

    if (board.getStatus().equals(BoardStatus.INACTIVE)) {
      throw new DeletePostException();
    }

    board = boardRepository.findByBoardIdAndMemberId(boardId, member)
        .orElseThrow(NotMyPostException::new);

    PlaceEntity place = placeRepository.findByPlaceId(placeId)
        .orElseThrow(SelectPlaceException::new);

    BoardEntity boardEntity = board.toBuilder()
        .title(boardRequestDto.getTitle())
        .content(boardRequestDto.getContent())
        .modDate(LocalDateTime.now())
        .placeId(place)
        .build();
    boardRepository.save(boardEntity);

    handleFileUpload(files, board);
  }

  @Override
  public void deletePost(String accessToken, Long boardId) {

    validateAccessToken(accessToken);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
    String platformString = claimsFromToken.get("platform", String.class);
    Platform platform = Platform.valueOf(platformString);

    MemberEntity member = getAuthenticatedMember(authentication, platform, boardId);
    BoardEntity board = boardRepository.findByBoardId(boardId)
            .orElseThrow(NotFoundPostException::new);

    if (board.getStatus().equals(BoardStatus.INACTIVE)) {
      throw new DeletePostException();
    }

    board = boardRepository.findByBoardIdAndMemberId(boardId, member)
            .orElseThrow(NotMyPostException::new);

    BoardEntity deletePost = board.toBuilder()
        .status(BoardStatus.INACTIVE)
        .deleteDate(LocalDateTime.now())
        .build();
    boardRepository.save(deletePost);

    List<BoardLikeEntity> deletePostLikes = boardLikeRepository.findByBoardId(board);
    boardLikeRepository.deleteAll(deletePostLikes);
  }

  public List<BoardListDto> getList(int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "boardId"));
    Page<BoardEntity> boardPage = boardRepository.findByStatus(BoardStatus.ACTIVE, pageable);

    List<BoardEntity> boardEntities = boardPage.getContent();
    List<List<String>> imageUrls = new ArrayList<>();
    List<String> memberImageUrl = new ArrayList<>();

    for (BoardEntity boardEntity : boardEntities) {
      List<String> url = new ArrayList<>();
      List<ImageEntity> images = imageRepository.findByBoardId(boardEntity);
      for (ImageEntity image : images) {
        url.add(image.getFileName());
      }
      imageUrls.add(url);

      MemberEntity memberEntity = boardEntity.getMemberId();
      if (memberEntity != null) {
        List<ImageEntity> memberImages = imageRepository.findByMemberId(memberEntity);
        if (!memberImages.isEmpty()) {

          memberImageUrl.add(memberImages.get(0).getFileName());
        } else {
          memberImageUrl.add("");
        }
      } else {
        memberImageUrl.add("");
      }
    }

    return BoardListDto.listOf(boardEntities, imageUrls, memberImageUrl);
  }

  @Override
  public BoardDetailDto getDetail(Long boardId, String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    BoardEntity board = boardRepository.findByBoardId(boardId)
        .orElseThrow(NotFoundPostException::new);
    List<ImageEntity> images = imageRepository.findAllByBoardId(board);
    PlaceEntity place = placeRepository.findByPlaceId(board.getPlaceId().getPlaceId())
        .orElseThrow(NotFoundPlaceException::new);
    List<String> imageUrls = new ArrayList<>();

    for (ImageEntity image : images) {
      imageUrls.add(image.getFileName());
    }

    boardRepository.save(board);

    return BoardDetailDto.getDetail(board, place, imageUrls);
  }

  @Override
  public List<BoardListDto> getMyPost(String accessToken) {

    validateAccessToken(accessToken);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(InvalidTokenException::new);

    List<BoardEntity> boards = boardRepository.findByMemberIdAndStatus(member,
        BoardStatus.ACTIVE);
    List<String> memberImageUrl = new ArrayList<>();

    List<List<String>> imageUrls = new ArrayList<>();
    for (BoardEntity board : boards) {
      List<String> url = new ArrayList<>();
      List<ImageEntity> images = imageRepository.findByBoardId(board);
      for (ImageEntity image : images) {
        url.add(image.getFilePath());
      }
      imageUrls.add(url);

      MemberEntity memberEntity = board.getMemberId();
      if (memberEntity != null) {
        List<ImageEntity> memberImages = imageRepository.findByMemberId(memberEntity);
        if (!memberImages.isEmpty()) {

          memberImageUrl.add(memberImages.get(0).getFileName());
        } else {
          memberImageUrl.add("");
        }
      } else {
        memberImageUrl.add("");
      }
    }

    return BoardListDto.listOf(boards, imageUrls, memberImageUrl);
  }

  @Override
  @Transactional
  public void likes(Long boardId, String accessToken) {
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(InvalidTokenException::new);

    BoardEntity board = boardRepository.findByBoardId(boardId)
        .orElseThrow(NotFoundPostException::new);

    if (board.getStatus().equals(BoardStatus.INACTIVE)) {
      throw new DeletePostException();
    }

    Optional<BoardLikeEntity> findByPostIdAndMemberId = boardLikeRepository.findByBoardIdAndMemberId(
        board, member);

    if (findByPostIdAndMemberId.isEmpty()) {
      BoardEntity boardEntity = board.toBuilder()
          .likeCnt(board.getLikeCnt() + 1)
          .build();
      boardRepository.save(boardEntity);

      BoardLikeEntity boardLikeEntity = BoardLikeEntity.builder()
          .boardId(board)
          .memberId(member)
          .build();
      boardLikeRepository.save(boardLikeEntity);

    } else {
      BoardEntity boardEntity = board.toBuilder()
          .likeCnt(board.getLikeCnt() - 1)
          .build();
      boardRepository.save(boardEntity);

      BoardLikeEntity boardLikeEntity = findByPostIdAndMemberId.get();
      boardLikeRepository.delete(boardLikeEntity);
    }
  }

  // 게시물 검색
  @Transactional
  @Override
  public List<BoardListDto> search(String keyword, SearchOption searchOption) {

    List<BoardEntity> boards;

    if (searchOption.equals(SearchOption.TITLE)) {
      boards = boardRepository.findByTitleContainingAndStatus(keyword, BoardStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.CONTENT)) {
      boards = boardRepository.findByContentContainingAndStatus(keyword, BoardStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT)) {
      boards = boardRepository.findByTitleContainingAndContentContainingAndStatus(keyword,
          keyword, BoardStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.NICKNAME)) {
      boards = boardRepository.findByNicknameAndStatus(keyword, BoardStatus.ACTIVE);
    } else {
      throw new InvalidSearchOptionException();
    }

    List<List<String>> imageUrls = new ArrayList<>();
    List<String> memberImageUrl = new ArrayList<>();

    for (BoardEntity boardEntity : boards) {
      List<String> url = new ArrayList<>();
      List<ImageEntity> images = imageRepository.findByBoardId(boardEntity);
      for (ImageEntity image : images) {
        url.add(image.getFileName());
      }
      imageUrls.add(url);

      MemberEntity memberEntity = boardEntity.getMemberId();
      if (memberEntity != null) {
        List<ImageEntity> memberImages = imageRepository.findByMemberId(memberEntity);
        if (!memberImages.isEmpty()) {
          // 여러 이미지 중 첫 번째 이미지를 사용
          memberImageUrl.add(memberImages.get(0).getFileName());
        } else {
          memberImageUrl.add(""); // 이미지가 없을 경우 빈 문자열 추가
        }
      } else {
        memberImageUrl.add(""); // 회원 정보가 없을 경우 빈 문자열 추가
      }
    }

    return BoardListDto.listOf(boards, imageUrls, memberImageUrl);
  }

  // <<어드민 기능>>
  // admin search board
  public List<BoardListDto> searchBoard(String accessToken, String keyword,
      SearchOption searchOption) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    List<BoardEntity> boards;

    if (searchOption.equals(SearchOption.TITLE)) {
      boards = boardRepository.findByTitleContaining(keyword);
    } else if (searchOption.equals(SearchOption.CONTENT)) {
      boards = boardRepository.findByContentContaining(keyword);
    } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT)) {
      boards = boardRepository.findByTitleContainingAndContentContaining(keyword, keyword);
    } else if (searchOption.equals(SearchOption.NICKNAME)) {
      boards = boardRepository.findByNickname(keyword);
    } else {
      throw new InvalidSearchOptionException();
    }

    List<List<String>> imageUrls = new ArrayList<>();
    List<String> memberImageUrl = new ArrayList<>();

    for (BoardEntity boardEntity : boards) {
      List<String> url = new ArrayList<>();
      List<ImageEntity> images = imageRepository.findByBoardId(boardEntity);
      for (ImageEntity image : images) {
        url.add(image.getFileName());
      }
      imageUrls.add(url);

      MemberEntity memberEntity = boardEntity.getMemberId();
      if (memberEntity != null) {
        List<ImageEntity> memberImages = imageRepository.findByMemberId(memberEntity);
        if (!memberImages.isEmpty()) {
          // 여러 이미지 중 첫 번째 이미지를 사용
          memberImageUrl.add(memberImages.get(0).getFileName());
        } else {
          memberImageUrl.add(""); // 이미지가 없을 경우 빈 문자열 추가
        }
      } else {
        memberImageUrl.add(""); // 회원 정보가 없을 경우 빈 문자열 추가
      }
    }

    return BoardListDto.listOf(boards, imageUrls, memberImageUrl);
  }

  private void validateAccessToken(String accessToken) {
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }
  }

  private MemberEntity getAuthenticatedMember(Authentication authentication, Platform platform, Long boardId) {
    if (isAdmin(authentication)) {
      return getMemberByBoardId(boardId);
    } else {
      String email = authentication.getName();
      return memberRepository.findByEmailAndPlatform(email, platform)
              .orElseThrow(NotFoundMemberException::new);
    }
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
  }

  private MemberEntity getMemberByBoardId(Long boardId) {
    BoardEntity board = boardRepository.findByBoardIdWithMember(boardId)
            .orElseThrow(NotFoundPostException::new);

    return board.getMemberId();
  }

  private void handleFileUpload(List<MultipartFile> files, BoardEntity board) {
    if (files == null || files.isEmpty()) {
      return;
    }

    deleteExistingImages(board);

    for (MultipartFile file : files) {
      validateAndSaveFile(file, board);
    }
  }

  private void deleteExistingImages(BoardEntity board) {
    List<ImageEntity> images = imageRepository.findAllByBoardId(board);
    imageRepository.deleteAll(images);
  }

  private void validateAndSaveFile(MultipartFile file, BoardEntity board) {
    String projectPath = System.getProperty("user.home") + "\\Desktop\\images\\";
    String fileName = generateFileName(file);

    if (isValidImageExtension(fileName)) {
      saveFile(file, projectPath, fileName);
      saveImageEntity(fileName, board);
    } else {
      throw new UnsupportedImageTypeException();
    }
  }

  private String generateFileName(MultipartFile file) {
    UUID uuid = UUID.randomUUID();
    return uuid + "_" + file.getOriginalFilename();
  }

  private boolean isValidImageExtension(String fileName) {
    String fileExtension = StringUtils.getFilenameExtension(fileName);
    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
    return fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase());
  }

  private void saveFile(MultipartFile file, String projectPath, String fileName) {
    File saveFile = new File(projectPath, fileName);
    try {
      file.transferTo(saveFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void saveImageEntity(String fileName, BoardEntity board) {
    ImageEntity imageEntity = ImageEntity.builder()
            .fileName(fileName)
            .filePath("/boards/" + fileName)
            .boardId(board)
            .useType(UseType.BOARD)
            .build();
    imageRepository.save(imageEntity);
  }
}