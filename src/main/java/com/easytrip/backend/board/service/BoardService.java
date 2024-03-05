package com.easytrip.backend.board.service;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.board.repository.BoardRepository;
import com.easytrip.backend.member.domain.MemberEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }


    @Transactional
    public void writePost(BoardEntity board, MemberEntity member) {

        board.setMember(member);
        boardRepository.save(board);
    }

    public Page<BoardEntity> getPostList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public BoardEntity getPost(long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Failed to load post : cannot find post id"));

    }

    @Transactional
    public void deletePost(long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public void updatePost(long id, BoardEntity requestBoard) {
        BoardEntity board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Failed to load post : cannot find post id"));//영속화
        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());
        // 이때 더티체킹 - 자동 업데이트
    }
}
