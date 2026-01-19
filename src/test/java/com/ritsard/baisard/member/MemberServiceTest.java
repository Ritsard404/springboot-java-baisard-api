package com.ritsard.baisard.member;

import com.ritsard.baisard.domain.member.dto.response.MemberListDto;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.projections.MemberListProjection;
import com.ritsard.baisard.domain.member.service.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMembers_withKeywordAndFilters() {
        // Arrange
        String keyword = "john";
        MemberApprovalStatus approvalStatus = MemberApprovalStatus.APPROVED;
        UUID companyId = UUID.randomUUID();
        int page = 0;
        int size = 10;
        String sortBy = "identifier";
        String direction = "asc";

        MemberListProjection mockProjection1 = mock(MemberListProjection.class);
        when(mockProjection1.getMemberId()).thenReturn(UUID.randomUUID());
        when(mockProjection1.getIdentifier()).thenReturn("john_doe");
        when(mockProjection1.getApprovalStatus()).thenReturn("APPROVED");
        when(mockProjection1.getCompanyId()).thenReturn(companyId);
        when(mockProjection1.getCompanyCode()).thenReturn("CMP01");
        when(mockProjection1.getCompanyName()).thenReturn("Test Company");
        when(mockProjection1.getPermissions()).thenReturn(List.of("ADMIN"));

        MemberListProjection mockProjection2 = mock(MemberListProjection.class);
        when(mockProjection2.getMemberId()).thenReturn(UUID.randomUUID());
        when(mockProjection2.getIdentifier()).thenReturn("jane_doe");
        when(mockProjection2.getApprovalStatus()).thenReturn("APPROVED");
        when(mockProjection2.getCompanyId()).thenReturn(companyId);
        when(mockProjection2.getCompanyCode()).thenReturn("CMP01");
        when(mockProjection2.getCompanyName()).thenReturn("Test Company");
        when(mockProjection2.getPermissions()).thenReturn(List.of("USER"));

        Page<MemberListProjection> mockPage = new PageImpl<>(Arrays.asList(mockProjection1, mockProjection2));

        when(memberRepository.findMembersWithProjection(keyword, approvalStatus, companyId, PageRequest.of(page, size, Sort.by(sortBy).ascending())))
                .thenReturn(mockPage);

        // Act
        Page<MemberListDto> result = memberService.getMembers(keyword, approvalStatus, companyId, page, size, sortBy, direction);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        MemberListDto firstMember = result.getContent().get(0);
        assertEquals("john_doe", firstMember.getIdentifier());
        assertEquals("APPROVED", firstMember.getApprovalStatus());
        assertEquals("CMP01", firstMember.getCompany().getCode());
        assertEquals(List.of("ADMIN"), firstMember.getPermissions());

        verify(memberRepository, times(1))
                .findMembersWithProjection(keyword, approvalStatus, companyId, PageRequest.of(page, size, Sort.by(sortBy).ascending()));
    }
}
