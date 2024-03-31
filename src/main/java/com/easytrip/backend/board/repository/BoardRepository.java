package com.easytrip.backend.board.repository;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.BoardStatus;
import com.easytrip.backend.type.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    Optional<BoardEntity> findByBoardId(Long boardId);

    Optional<BoardEntity> findByBoardIdAndMemberId(Long boardId, MemberEntity member);

    List<BoardEntity> findByMemberIdAndStatus(MemberEntity member, BoardStatus status);

    List<BoardEntity> findByTitleContainingAndStatus(String title, BoardStatus status);

    List<BoardEntity> findByContentContainingAndStatus(String content, BoardStatus status);

    List<BoardEntity> findByNicknameAndStatus(String nickname, BoardStatus status);

    List<BoardEntity> findByTitleContainingAndContentContainingAndStatus(String keyword, String keyword2, BoardStatus status);
    List<BoardEntity> findByMemberId(MemberEntity member);

    List<BoardEntity> findByTitleContaining(String keyword);

    List<BoardEntity> findByContentContaining(String keyword);

    List<BoardEntity> findByTitleContainingAndContentContaining(String keyword, String keyword1);

    List<BoardEntity> findByNickname(String keyword);
}
