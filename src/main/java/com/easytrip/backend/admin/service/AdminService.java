package com.easytrip.backend.admin.service;

import com.easytrip.backend.admin.dto.MemberDetailDto;
import com.easytrip.backend.member.dto.request.UpdateRequest;
import com.easytrip.backend.place.dto.PlaceDto;
import com.easytrip.backend.place.dto.request.PlaceRequest;
import com.easytrip.backend.type.MemberStatus;
import com.easytrip.backend.type.SearchOption;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AdminService {

  void setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus);

  MemberDetailDto getMemberInfo(String accessToken, Long memberId);

  MemberDetailDto updateMemberInfo(String accessToken, Long memberId, UpdateRequest updateRequest,
      MultipartFile file);

  List<MemberDetailDto> searchMember(String accessToken, String keyword, SearchOption searchOption);

  PlaceDto updatePlace(String accessToken, Long placeId, PlaceRequest placeRequest, List<MultipartFile> file);
}
