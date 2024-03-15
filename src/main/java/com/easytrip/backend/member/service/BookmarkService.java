package com.easytrip.backend.member.service;

import com.easytrip.backend.member.dto.BookmarkDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface BookmarkService {

  List<BookmarkDto> myBookmark(String accessToken);

  void bookmarkCancel(String accessToken, Long bookmarkId);
}
