package com.easytrip.backend.board.dto;



import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequestDto {


    @NotEmpty(message = "게시글의 제목을 입력해주세요.")
    private String title;


    @NotNull(message = "게시글의 내용을 입력해주세요.")
    private String content;

    private String fileName;

    private String filePath;


}
