package com.easytrip.backend.board.service.impl;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.domain.BoardLikeEntity;
import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.repository.BoardLikeRepository;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.board.service.BoardService;
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

import java.time.LocalDateTime;
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
    public void writePost(String accessToken, BoardRequestDto boardRequestDto, List<MultipartFile> files, Long placeId) throws Exception {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        // 장소
        PlaceEntity place = placeRepository.findByPlaceId(placeId).orElseThrow(SelectPlaceException::new);


        // naver , kakao
        Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
        String platformString = claimsFromToken.get("platform", String.class);
        Platform platform = Platform.valueOf(platformString);

        MemberEntity memberplatform = memberRepository.findByEmailAndPlatform(email, platform)
                .orElseThrow(NotFoundMemberException::new);

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
                .build();
        boardRepository.save(board);



//        [     이미지    ]


        // 여러개의 파일 저장
        for (MultipartFile file : files) {


            // 저장 경로 설정 ~/boards
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\boards";
            UUID uuid = UUID.randomUUID();

            // 랜덤식별자_원래이름
            String fileName = uuid + "_" + file.getOriginalFilename();

            // 파일 이름에서 확장자 추출
            String fileExtension = StringUtils.getFilenameExtension(fileName);

            // 지원하는 이미지 파일 확장자 목록
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

            // 확장자가 이미지 파일인지 확인
            if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {

                // 빈 껍데기 생성
                File saveFile = new File(projectPath, fileName);

                // transferTo --> Exception 필요
                file.transferTo(saveFile);

            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }

            // 이미지 저장 Board
            ImageEntity image = ImageEntity.builder()
                    .fileName(fileName)
                    .filePath(projectPath + "\\" + fileName)
                    .boardId(board)
                    .useType(UseType.BOARD)
                    .build();

            imageRepository.save(image);


            //    [     장소    ]
            // placeLink 구현해야함

//            PlaceEntity byPlaceId = placeRepository.findByPlaceId(placeId).orElseThrow(NotFoundPostException::new);
//
//            boardRepository.save(byPlaceId);


        }

//        log.info(
//                "post write admin: {}, post content - title: {}, content: {},",
//                email, boardRequestDto.getTitle(), boardRequestDto.getContent());

    }


    // 게시물 수정
    @Transactional
    @Override
    public void updatePost(String accessToken, Long boardId, Long placeId, BoardRequestDto boardRequestDto, List<MultipartFile> files) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberEntity member = new MemberEntity();
        BoardEntity board = new BoardEntity();
        ImageEntity image = new ImageEntity();

        String email = null;

        // naver , kakao
        Claims claimsFromToken = jwtTokenProvider.getClaimsFromToken(accessToken);
        String platformString = claimsFromToken.get("platform", String.class);
        Platform platform = Platform.valueOf(platformString);

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            // admin

            board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);
            Long memberId = board.getMemberId().getMemberId();
            member = memberRepository.findByMemberId(memberId)
                    .orElseThrow(NotFoundMemberException::new);
        } else {
            // member
            email = authentication.getName();
            member = memberRepository.findByEmailAndPlatform(email, platform)
                    .orElseThrow(NotFoundMemberException::new);

            // post exist
             board = boardRepository.findByBoardId(boardId)
                    .orElseThrow(NotFoundPostException::new);
        }

//        have benn deleted post
        if (board.getStatus().equals(BoardStatus.INACTIVE)) {
            throw new DeletePostException();
        }

        // ??boardId
        board = boardRepository.findByBoardIdAndMemberId(boardId, member)
                .orElseThrow(NotMyPostException::new);

          PlaceEntity place = placeRepository.findByPlaceId(placeId).orElseThrow(SelectPlaceException::new);
        BoardEntity boardEntity = board.toBuilder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .modDate(LocalDateTime.now())
                .placeId(place)
                .build();
        boardRepository.save(boardEntity);


        if (!files.isEmpty() || files != null) {
            // 기존의 이미지를 삭제하고 새로운 이미지로 대체
            List<ImageEntity> images = imageRepository.findAllByBoardId(board);
            imageRepository.deleteAll(images);
        }


        for (MultipartFile file : files) {


            // 저장 경로 설정 ~/boards
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\boards";
            UUID uuid = UUID.randomUUID();

            // 랜덤식별자_원래이름
            String fileName = uuid + "_" + file.getOriginalFilename();


            // 파일 이름에서 확장자 추출
            String fileExtension = StringUtils.getFilenameExtension(fileName);


            // 지원하는 이미지 파일 확장자 목록
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");


            // 확장자가 이미지 파일인지 확인
            if (fileExtension != null && allowedExtensions.contains(fileExtension.toLowerCase())) {

                // 빈 껍데기 생성
                File saveFile = new File(projectPath, fileName);

                // transferTo --> Exception 필요
                try {
                    file.transferTo(saveFile);
                } catch (Exception e) {
                    throw new ImageSaveException();
                }


            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }

            // 이미지 저장 Board
            ImageEntity imageEntity = image.toBuilder()
                    .fileName(fileName)
                    .filePath(projectPath + "\\" + fileName)
                    .boardId(board)
                    .useType(UseType.BOARD)
                    .build();

            imageRepository.save(imageEntity);
        }

        //로그처리
//        if (authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
//            log.info(
//                    "post update admin: {}, postId: {}, post content - title: {}, content: {}",
//                    email, boardId, boardRequestDto.getTitle(), boardRequestDto.getContent());
//        } else {
//            log.info(
//                    "post update user: {}, postId: {}, post content - title: {}, content: {}",
//                    email, boardId, boardRequestDto.getTitle(), boardRequestDto.getContent());
//        }


    }

    // 게시글 삭제 ( INACTIVE )
    @Override
    public void deletePost(String accessToken, Long boardId) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MemberEntity member = new MemberEntity();
        BoardEntity board = new BoardEntity();
//
        String email = null;
//
        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {

            //admin
            email = authentication.getName();
            board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);

        } else {

            //member
            email = authentication.getName();
            member = memberRepository.findByEmail(email).orElseThrow(InvalidTokenException::new);
            board = boardRepository.findByBoardIdAndMemberId(boardId, member)
                    .orElseThrow(NotMyPostException::new);
        }
        // have been deleted post
        if (board.getStatus().equals(BoardStatus.INACTIVE)) {
            throw new DeletePostException();
        }
        board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);
        BoardEntity deletePost = board.toBuilder()
                .status(BoardStatus.INACTIVE)
                .deleteDate(LocalDateTime.now())
                .build();
        boardRepository.save(deletePost);

        // 좋아요 삭제 처리
        List<BoardLikeEntity> deletePostLikes = boardLikeRepository.findByBoardId(board);
        boardLikeRepository.deleteAll(deletePostLikes);

//            // 로그 처리
//            if (authentication.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
//
//                log.info("post delete admin: {}, postId: {}", email, boardId);
//            } else {
//                log.info("post delete user: {}, postId: {}", email, boardId);
//            }


    }

    // 게시판 상세
    @Override
    public List<BoardListDto> getList(Pageable pageable, String sort) {

        // 추천순, 작성일순
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy =  "boardId";

//
        // 만약 sort가 id나 likes로 지정된 경우에는 해당 값에 따라 정렬 방식을 변경합니다.
        if (sort != null && (sort.equals("like") || sort.equals("view"))) {
            sortBy = sort.equals("like") ? "likeCnt" : "viewCnt";
        }

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));

        List<BoardEntity> boardEntities = boardRepository.findAll(pageable).getContent();



        return BoardListDto.listOf(boardEntities);
    }

    @Override
    public BoardDetailDto getDetail(Long boardId) {

        // 이미지 불러와야됌

        BoardEntity board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);
        List<ImageEntity> images = imageRepository.findAllByBoardId(board);
        PlaceEntity place = placeRepository.findByPlaceId(board.getPlaceId().getPlaceId()).orElseThrow(NotFoundPlaceException::new);
        List<String> imageUrls = new ArrayList<>();

        for (ImageEntity image : images) {

            imageUrls.add(image.getFilePath());

        }

        // 조회수

        Integer viewCnt = board.getViewCnt();

        viewCnt++;

        board.setViewCnt(viewCnt);

        boardRepository.save(board);


        BoardDetailDto boardDetailDto = BoardDetailDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .images(imageUrls)
                .placeName(place.getPlaceName())
                .placeLink("http://localhost:8080/place/info" + place.getPlaceId())
                .x(place.getX())
                .y(place.getY())
                .viewCnt(board.getViewCnt())
                .address(place.getAddress())
                .likeCnt(board.getLikeCnt())
                .build();

return boardDetailDto;

    }

    //나의 게시물
    @Override
    public List<BoardListDto> getMyPost(String accessToken) {

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);


        List<BoardEntity> boards = boardRepository.findByMemberIdAndStatus(member, BoardStatus.ACTIVE);



        return BoardListDto.listOf(boards);
    }

    // 게시물 좋아요
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

        Optional<BoardLikeEntity> findByPostIdAndMemberId = boardLikeRepository.findByBoardIdAndMemberId(board, member);

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
            boards = boardRepository.findByTitleContainingAndContentContainingAndStatus(keyword, keyword, BoardStatus.ACTIVE);
        } else if (searchOption.equals(SearchOption.NICKNAME)) {
            boards = boardRepository.findByNicknameAndStatus(keyword, BoardStatus.ACTIVE);
        } else {
            throw new InvalidSearchOptionException();
        }
        return BoardListDto.listOf(boards);
    }

    // <<어드민 기능>>
    // admin search board
    public List<BoardListDto> searchBoard(String accessToken, String keyword, String searchOption) {

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

        return BoardListDto.listOf(boards);
    }
}