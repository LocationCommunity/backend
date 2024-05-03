package com.easytrip.backend.matching.domain;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.type.Interest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_interest")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberInterestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long memberInterestId;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;

  @Enumerated(EnumType.STRING)
  private Interest interest;
}
