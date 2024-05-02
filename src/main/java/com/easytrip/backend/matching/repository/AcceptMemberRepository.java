package com.easytrip.backend.matching.repository;

import com.easytrip.backend.matching.domain.AcceptMemberEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptMemberRepository extends JpaRepository<AcceptMemberEntity, Long> {

  Optional<AcceptMemberEntity> findByAcceptingMemberIdAndLikedMemberId(MemberEntity acceptingMember,
      MemberEntity likedMember);
}
