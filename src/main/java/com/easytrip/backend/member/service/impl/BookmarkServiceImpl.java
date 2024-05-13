package com.easytrip.backend.member.service.impl;

import com.easytrip.backend.exception.impl.InvalidTokenException;
import com.easytrip.backend.exception.impl.NotFoundBookmarkException;
import com.easytrip.backend.exception.impl.NotFoundMemberException;
import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.BookmarkDto;
import com.easytrip.backend.member.jwt.JwtTokenProvider;
import com.easytrip.backend.member.repository.MemberRepository;
import com.easytrip.backend.member.service.BookmarkService;
import com.easytrip.backend.place.domain.BookmarkPlaceEntity;
import com.easytrip.backend.place.repository.BookmarkPlaceRepository;
import com.easytrip.backend.type.Platform;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

  private final MemberRepository memberRepository;
  private final BookmarkPlaceRepository bookmarkPlaceRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public List<BookmarkDto> myBookmark(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    String email = authentication.getName();

    Platform platform = jwtTokenProvider.getPlatform(accessToken);

    MemberEntity member = memberRepository.findByEmailAndPlatform(email, platform)
        .orElseThrow(() -> new NotFoundMemberException());

    List<BookmarkPlaceEntity> byMemberId = bookmarkPlaceRepository.findByMemberId(member);
    List<BookmarkDto> result = BookmarkDto.listOf(byMemberId);

    return result;
  }

  @Override
  public void bookmarkCancel(String accessToken, Long bookmarkId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    BookmarkPlaceEntity bookmarkPlace = bookmarkPlaceRepository.findByBookmarkId(bookmarkId)
        .orElseThrow(() -> new NotFoundBookmarkException());
    bookmarkPlaceRepository.delete(bookmarkPlace);
  }
}
