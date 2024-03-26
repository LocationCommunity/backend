package com.easytrip.backend.common.image.entity
        ;


import com.easytrip.backend.board.domain.BoardEntity;
import com.easytrip.backend.type.UseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "images")
@Entity
public class ImageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;


    private String fileName;


    private String filePath;


    @Enumerated(EnumType.STRING)
    private UseType useType;


}
