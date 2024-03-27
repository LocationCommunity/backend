package com.easytrip.backend.board.repository;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

  Optional<BoardEntity> findALlByMemberId(MemberEntity member);
  List<BoardEntity> findByStatusOrderByCreateDateDesc(BoardStatus status);

  List<BoardEntity> findByStatusOrderByLikeCntDesc(BoardStatus status);

  Optional<BoardEntity> findByBoardId(Long boardId);

  Optional<BoardEntity> findByBoardIdAndMemberId(Long boardId, MemberEntity member);

  List<BoardEntity> findByMemberIdAndStatus(MemberEntity member, BoardStatus status);

  List<BoardEntity> findByTitleContainingAndStatus(String title, BoardStatus status);

  List<BoardEntity> findByContentContainingAndStatus(String content, BoardStatus status);

  List<BoardEntity> findByNicknameAndStatus(String nickname, BoardStatus status);

  Optional<BoardEntity> findByBoardIdAndStatus(Long boardId, BoardStatus status);

  List<BoardEntity> findByTitleContaining(String keyword);

  List<BoardEntity> findByContentContaining(String keyword);

  List<BoardEntity> findByTitleContainingAndContentContainingAndStatus(String keyword, String keyword2, BoardStatus status);

  List<BoardEntity> findByNickname(String keyword);





}
