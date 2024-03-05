package com.easytrip.backend.board.repository;

import com.easytrip.backend.board.domain.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

//  Optional<BoardEntity> findByTitle
}
