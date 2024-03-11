package com.easytrip.backend.place.repository;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.place.domain.BookmarkPlaceEntity;
import com.easytrip.backend.place.domain.PlaceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkPlaceRepository extends JpaRepository<BookmarkPlaceEntity, Long> {

  List<BookmarkPlaceEntity> findByPlaceId(PlaceEntity placeEntity);

  List<BookmarkPlaceEntity> findByMemberId(MemberEntity member);

  Optional<BookmarkPlaceEntity> findByMemberIdAndPlaceId(MemberEntity member, PlaceEntity place);

  Optional<BookmarkPlaceEntity> findByBookmarkId(Long bookmarkId);
}
