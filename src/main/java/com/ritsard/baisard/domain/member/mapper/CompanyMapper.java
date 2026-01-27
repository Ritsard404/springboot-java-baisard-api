package com.ritsard.baisard.domain.member.mapper;

import com.ritsard.baisard.domain.member.dto.request.UpdateCompanyDto;
import com.ritsard.baisard.domain.member.dto.response.CompanyDto;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.global.utils.AESUtil;
import com.ritsard.baisard.global.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyMapper {
    private final ImageUtils imageUtils;
    private final AESUtil aesUtil;

    public Company toCompany(UpdateCompanyDto dto) {
        Company company = Company.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();

        List<String> fileIds = new ArrayList<>();

        if (dto.getEncryptedCompanyImageId() != null) {
            fileIds.add(dto.getEncryptedCompanyImageId());
        }

        if (!fileIds.isEmpty()) {
            try {
                company.setFileIds(fileIds);
            } catch (IOException e) {
                throw new RuntimeException("Error setting company image file IDs", e);
            }

            imageUtils.setImageUrlFromFiles(
                    dto.getEncryptedCompanyImageId(),
                    company.getFileList(),
                    company::setLogoImageUrl
            );
        }

        return company;
    }

    public CompanyDto toCompanyDto(Company company) {
        return CompanyDto.builder()
                .uuid(company.getUuidCompany())
                .name(company.getName())
                .code(company.getCode())
                .email(company.getEmail())
                .phone(company.getPhone())
                .logoImageUrl(
                        imageUtils.toAbsoluteUrl(company.getLogoImageUrl())
                )
                .build();
    }

    public void updateCompanyFromDto(UpdateCompanyDto dto, Company company) {
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());

        if (dto.getEncryptedCompanyImageId() != null) {
            List<String> fileIds = List.of(dto.getEncryptedCompanyImageId());
            try {
                company.setFileIds(fileIds);
                imageUtils.setImageUrlFromFiles(
                        dto.getEncryptedCompanyImageId(),
                        company.getFileList(),
                        company::setLogoImageUrl
                );
            } catch (IOException e) {
                throw new RuntimeException("Error updating company image", e);
            }
        }
    }
}
