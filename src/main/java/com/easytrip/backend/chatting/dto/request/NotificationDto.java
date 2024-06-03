package com.easytrip.backend.chatting.dto.request;

import com.easytrip.backend.member.domain.MemberEntity;
import com.easytrip.backend.member.dto.MemberDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Data
public class NotificationDto {


        private Long memberId;
        private String title;
        private String content;
        private LocalDateTime time;

        // 생성자, getter, setter 등 필요한 메서드들을 추가합니다.


        // 예시 생성자
        public NotificationDto(Long memberId, String title, String content, LocalDateTime time) {
            this.memberId = memberId;
            this.title = title;
            this.content = content;
            this.time = time;
        }

        // getter, setter 등의 메서드들을 추가합니다.
    }

