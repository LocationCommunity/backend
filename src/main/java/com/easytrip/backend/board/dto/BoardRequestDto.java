package com.easytrip.backend.board.dto;


import com.easytrip.backend.type.UseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequestDto {

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String visitDate;
}
