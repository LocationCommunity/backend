package com.easytrip.backend.board.dto;



import com.easytrip.backend.type.UseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequestDto {



    private String title;

    private String content;

     private UseType useType;


}
