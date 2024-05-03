package com.easytrip.backend.matching.repository;

import com.easytrip.backend.matching.domain.MemberInterestEntity;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.Interest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<MemberInterestEntity, Long> {

  List<MemberInterestEntity> findAllByMemberId(MemberEntity member);

  void deleteAllByMemberId(MemberEntity member);

  List<MemberInterestEntity> findAllByInterest(Interest interest);
}
