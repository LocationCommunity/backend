package com.easytrip.backend.common.image.repository;

import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.common.image.domain.ImageEntity;
import com.easytrip.backend.exhibition.entity.ExhibitionEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<com.easytrip.backend.common.image.domain.ImageEntity, Long> {





    List<ImageEntity> findByBoardId(BoardEntity boardId);



    List<ImageEntity> findAllByExId(ExhibitionEntity ex);



    List<ImageEntity> findAllByBoardId(BoardEntity board);


    List<ImageEntity> findByPlaceId(PlaceEntity place);


    List<ImageEntity> findByMemberId(MemberEntity member);

    List<ImageEntity> findByExId(ExhibitionEntity exhibition);
}
