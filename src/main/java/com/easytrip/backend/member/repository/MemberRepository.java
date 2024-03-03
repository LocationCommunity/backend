package com.easytrip.backend.member.repository;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.PlatForm;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByNickname(String nickname);

  Optional<MemberEntity> findByEmail(String email);
}
