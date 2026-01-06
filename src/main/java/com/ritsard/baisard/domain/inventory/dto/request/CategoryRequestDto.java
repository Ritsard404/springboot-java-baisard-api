package com.ritsard.baisard.domain.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category Request DTO")
public class CategoryRequestDto {

    @NotNull
    private String categoryName;
}
