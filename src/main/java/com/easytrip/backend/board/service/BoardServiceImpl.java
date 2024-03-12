package com.easytrip.backend.board.service;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.domain.BoardLikeEntity;
import com.easytrip.backend.board.dto.BoardDetailDto;
import com.easytrip.backend.board.dto.BoardListDto;
import com.easytrip.backend.board.dto.BoardRequestDto;
import com.easytrip.backend.board.repository.BoardLikeRepository;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.exception.impl.*;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.SearchOption;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    /**
     * 게시글 작성
     * @param boardRequestDto
     * @return string
     */
    @Override
    @Transactional
    public String writePost(BoardRequestDto boardRequestDto, MultipartFile file) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 저장할 경로 지정
        String projectPath = System.getProperty("user.dir") + "\\src.\\main\\resource\\static\\files";
        UUID uuid = UUID.randomUUID();

        // 랜덤식별자_원래이름
        String fileName = uuid + "_" + file.getOriginalFilename();

        // 빈 껍데기 생성
        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        BoardEntity board = BoardEntity.builder()
                .title(boardRequestDto.getTitle())
                .memberId(member)
                .nickname(member.getNickname())
                .content(boardRequestDto.getContent())
                .fileName(fileName)
                .filePath("/files" + fileName)
                .likeCnt(0)
                .status(BoardStatus.ACTIVE)
                .createDate(LocalDateTime.now())
                .build();
        boardRepository.save(board);


        log.info(
                "post write admin: {}, post content - title: {}, content: {},",
                email, boardRequestDto.getTitle(), boardRequestDto.getContent());


        return "게시글 작성이 완료되었습니다.";
    }

    /**
     * 게시글 수정
     * @param boardId
     * @param boardRequestDto
     * @return string
     */
    @Override
    @Transactional
    public String updatePost(Long boardId, BoardRequestDto boardRequestDto, MultipartFile file) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MemberEntity member = new MemberEntity();
        BoardEntity board = new BoardEntity();

        String email = null;

        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            //admin
            board = boardRepository.findByBoardId(boardId).orElseThrow(NotFoundPostException::new);


        } else {
            //member
            email = authentication.getName();
            member = memberRepository.findByEmail(email).orElseThrow(InvalidTokenException::new);

            //post exist
            BoardEntity boardEntity = boardRepository.findByBoardId(boardId)
                    .orElseThrow(NotFoundPostException::new);
        }

        //have benn deleted post
        if (board.getStatus().equals(BoardStatus.INACTIVE)) {
            throw new DeletePostException();
        }

        // ??boardId
        board = boardRepository.findByBoardIdAndMemberId(boardId, member)
                .orElseThrow(InvalidAuthCodeException::new);


        BoardEntity boardEntity = board.toBuilder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .fileName(board.getFileName())
                .filePath(board.getFilePath())
                .modDate(LocalDateTime.now())
                .build();
        boardRepository.save(boardEntity);

        //로그처리
        if (authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
            log.info(
                    "post update admin: {}, postId: {}, post content - title: {}, content: {}",
                    email, boardId, boardRequestDto.getTitle(), boardRequestDto.getContent());
        } else {
            log.info(
                    "post update user: {}, postId: {}, post content - title: {}, content: {}",
                    email, boardId, boardRequestDto.getTitle(), boardRequestDto.getContent());
        }


        return "게시물을 수정했습니다.";
    }

    /**
     * 게시글 삭제
     * @param boardId
     * @return string
     */
    @Override
    public String deletePost(Long boardId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MemberEntity member = new MemberEntity();
        BoardEntity board = new BoardEntity();

        String email = null;

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

            BoardEntity deletePost = board.toBuilder()
                    .status(BoardStatus.INACTIVE)
                    .deleteDate(LocalDateTime.now())
                    .build();
            boardRepository.save(deletePost);

            // 좋아요 삭제 처리
            List<BoardLikeEntity> deletePostLikes = boardLikeRepository.findByBoardId(board);
            boardLikeRepository.deleteAll(deletePostLikes);



            // 로그 처리
            if (authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"))) {

                log.info("post delete admin: {}, postId: {}", email, boardId);
            } else {
                log.info("post delete user: {}, postId: {}", email, boardId);
            }

            return "게시글이 삭제되었습니다.";

        }

    @Override
    @Transactional
    public List<BoardListDto> getList(Boolean sortByLikes) {

        List<BoardEntity> boards;
        // 추천순, 작성일순
        if ( sortByLikes ) {
            boards = boardRepository.findByStatusOrderByLikeCntDesc(BoardStatus.ACTIVE);
        } else {
            boards = boardRepository.findByStatusOrderByCreateDateDesc(BoardStatus.ACTIVE);
        }

        return BoardListDto.listOf(boards);
    }

    /**
     * 게시글
     * @param boardId
     * @return string
     * issue : 회원 / 비회원 열람 가능 고민.
     */
    @Override
    public BoardDetailDto getDetail(Long boardId) {

        BoardEntity boardEntity = boardRepository.findByBoardId(boardId)
                .orElseThrow(NotFoundPostException::new);

        if (boardEntity.getStatus().equals(BoardStatus.INACTIVE)) {

            throw new DeletePostException();
        }

        BoardDetailDto boardDetailDto = BoardDetailDto.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .nickname(boardEntity.getNickname())
                .likeCnt(boardEntity.getLikeCnt())
                .createDate(boardEntity.getCreateDate())
                .build();


        return boardDetailDto;
    }

    /**
     * 나의 게시글
     * @return
     */
    @Override
    public List<BoardListDto> getMyPost() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        MemberEntity member = memberRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new);

        List<BoardEntity> boards = boardRepository.findByMemberIdAndStatus(member, BoardStatus.ACTIVE);


        return BoardListDto.listOf(boards);
    }

    /**
     * 게시글 좋아요
     * @param boardId
     */
    @Override
    @Transactional
    public void likes(Long boardId) {

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

    /**
     * 검색기능
     * @param keyword
     * @param searchOption
     * @return
     */
    @Override
    public List<BoardListDto> search(String keyword, String searchOption) {

        List<BoardEntity> boards = new ArrayList<>();

        if (searchOption.equals(SearchOption.TITLE.getValue())) {
            boards = boardRepository.findByTitleContainingAndStatus(keyword, BoardStatus.ACTIVE);
        } else if (searchOption.equals(SearchOption.CONTENT.getValue())) {
            boards = boardRepository.findByContentContainingAndStatus(keyword, BoardStatus.ACTIVE);
        } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT.getValue())) {
            boards = boardRepository.findByTitleContainingAndContentContainingAndStatus(keyword, keyword, BoardStatus.ACTIVE);
        } else if (searchOption.equals(SearchOption.NICKNAME.getValue())) {
            boards = boardRepository.findByNicknameAndStatus(keyword, BoardStatus.ACTIVE);
        } else {
            throw new InvalidSearchOptionException();


        }
        return BoardListDto.listOf(boards);
    }
}




