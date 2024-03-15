package com.easytrip.backend.member.repository;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.Platform;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
  Optional<MemberEntity> findByEmail(String email);

  Optional<MemberEntity> findByEmailAndPlatform(String email, Platform platForm);
}
