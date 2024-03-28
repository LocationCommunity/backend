
package com.easytrip.backend.board.service;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.domain.BoardLikeEntity;
import com.easytrip.backend.board.dto.*;
import com.easytrip.backend.board.exception.NotfoundImageException;
import com.easytrip.backend.board.repository.BoardLikeRepository;
import com.easytrip.backend.board.repository.BoardRepository;

import com.easytrip.backend.common.image.entity.ImageEntity;
import com.easytrip.backend.common.image.repository.ImageRepository;
import com.easytrip.backend.exception.UnsupportedImageTypeException;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.place.domain.PlaceEntity;
import com.easytrip.backend.place.repository.PlaceRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.SearchOption;
import com.easytrip.backend.type.UseType;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.data.domain.Pageable;
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


    public String writePost(BoardRequestDto boardRequestDto, List<MultipartFile> files, Long placeId) throws Exception {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        // 장소
        PlaceEntity place = placeRepository.findByPlaceId(placeId).orElseThrow(NotFoundPlaceException::new);


        BoardEntity board = BoardEntity.builder()
                .title(boardRequestDto.getTitle())
                .memberId(member)
                .nickname(member.getNickname())
                .content(boardRequestDto.getContent())
                .likeCnt(0)
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

            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }



            // 빈 껍데기 생성
            File saveFile = new File(projectPath, fileName);

            // transferTo --> Exception 필요
            file.transferTo(saveFile);


            // 이미지 저장 Board
            ImageEntity image = ImageEntity.builder()
                    .fileName(fileName)
                    .filePath("/boards/" + fileName)
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


        return "게시글 작성이 완료되었습니다.";
    }

    @Transactional
    @Override
    public String updatePost(Long boardId, BoardRequestDto boardRequestDto, List<MultipartFile> files) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberEntity member = new MemberEntity();
        BoardEntity board = new BoardEntity();
        ImageEntity image = new ImageEntity();
//
        String email = null;
//
        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            // admin
            board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);


        } else {
            // member
            email = authentication.getName();
            member = memberRepository.findByEmail(email).orElseThrow(InvalidTokenException::new);

            // post exist
            BoardEntity boardEntity = boardRepository.findByBoardId(boardId)
                    .orElseThrow(NotFoundPostException::new);
        }

//        have benn deleted post
        if (board.getStatus().equals(BoardStatus.INACTIVE)) {
            throw new DeletePostException();
        }

        // ??boardId
        board = boardRepository.findByBoardIdAndMemberId(boardId, member)
                .orElseThrow(InvalidAuthCodeException::new);


        board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);

        BoardEntity boardEntity = board.toBuilder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .modDate(LocalDateTime.now())
                .build();
        boardRepository.save(boardEntity);

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

            } else {
                // 이미지 파일이 아닌 경우에 대한 처리
                throw new UnsupportedImageTypeException();
            }

            // 빈 껍데기 생성
            File saveFile = new File(projectPath, fileName);

            // transferTo --> Exception 필요
            file.transferTo(saveFile);


            imageRepository.findById(boardId).orElseThrow(NotfoundImageException::new);

            // 이미지 저장 Board
            ImageEntity imageEntity = image.toBuilder()
                    .fileName(fileName)
                    .filePath("/boards/" + fileName)
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

            return "게시물을 수정했습니다.";
        }

        /**
         * 게시글 삭제
         * @param boardId
         * @return string
         */
        @Override
        public String deletePost (Long boardId){

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
                        .orElseThrow(NotFoundPostException::new);
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
            return "게시글이 삭제되었습니다.";

        }

        @Override
        @Transactional
        public List<BoardListDto> getList ( boolean sortByLikes){

            List<BoardEntity> boards;
            // 추천순, 작성일순
            if (sortByLikes) {
                boards = boardRepository.findByStatusOrderByLikeCntDesc(BoardStatus.ACTIVE);
            } else {
                boards = boardRepository.findByStatusOrderByCreateDateDesc(BoardStatus.ACTIVE);
            }

            return BoardListDto.listOf(boards);
        }

        @Override
        public BoardDetailDto getDetail (Long boardId, BoardDetailDto boardDetailDto){

            BoardEntity boardEntity = boardRepository.findByBoardId(boardId)
                    .orElseThrow(NotFoundPostException::new);

            if (boardEntity.getStatus().equals(BoardStatus.INACTIVE)) {

                throw new DeletePostException();
            }


            // dto to Entity
//        BoardEntity board = BoardEntity.builder().build();
//        .title(boardDetailDto.getContent()


            // Entity to dto
            BoardDetailDto boardDetail = BoardDetailDto.builder()
                    .boardId(boardEntity.getBoardId())
                    .title(boardEntity.getTitle())
                    .content(boardEntity.getContent())
                    .nickname(boardEntity.getNickname())
                    .likeCnt(boardEntity.getLikeCnt())
                    .createDate(boardEntity.getCreateDate())
                    .placeId(boardEntity.getPlaceId().getPlaceId())
                    .placeName(boardEntity.getPlaceId().getPlaceName())
                    .address(boardEntity.getPlaceId().getAddress())
                    .x(boardEntity.getPlaceId().getX())
                    .y(boardEntity.getPlaceId().getY())
                    .placeLink("http://localhost:8080/place/info/" + boardEntity.getPlaceId().getPlaceId())
                    .build();

            return boardDetail;
        }


        @Override
        public List<BoardListDto> getMyPost () {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            MemberEntity member = memberRepository.findByEmail(email)
                    .orElseThrow(InvalidTokenException::new);

            List<BoardEntity> boards = boardRepository.findByMemberIdAndStatus(member, BoardStatus.ACTIVE);


            return BoardListDto.listOf(boards);
        }


        @Override
        @Transactional
        public void likes (Long boardId){

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

        @Override
        public List<BoardListDto> search (String keyword, SearchOption searchOption){

            List<BoardEntity> boards = new ArrayList<>();

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
    }





