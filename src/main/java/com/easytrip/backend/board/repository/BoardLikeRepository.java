package com.easytrip.backend.board.repository;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.domain.BoardLikeEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {

    List<BoardLikeEntity> findByBoardId(BoardEntity board);

    Optional<BoardLikeEntity> findByBoardIdAndMemberId(BoardEntity board, MemberEntity member);



}
