package com.ritsard.baisard.domain.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category DTO")
public class CategoryDto {

    private String categoryName;
}
