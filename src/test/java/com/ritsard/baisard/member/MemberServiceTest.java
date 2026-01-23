package com.ritsard.baisard.member;

import com.ritsard.baisard.domain.member.dto.response.*;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import com.ritsard.baisard.domain.member.mapper.MemberMapper;
import com.ritsard.baisard.domain.member.repository.MemberRepository;
import com.ritsard.baisard.domain.member.repository.projections.MemberListProjection;
import com.ritsard.baisard.domain.member.service.MemberServiceImpl;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NoSuchUserException;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import com.ritsard.baisard.utils.helper.AESConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService Tests")
public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthManager<Member> authManager;

    @Mock
    private AESConverter aesConverter;

    private UUID testMemberId;
    private UUID testCompanyId;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testMemberId = UUID.randomUUID();
        testCompanyId = UUID.randomUUID();
        testMember = mock(Member.class);

        // Inject the authManager since it's not annotated with @Autowired in the service
        ReflectionTestUtils.setField(memberService, "authManager", authManager);
    }

    @Nested
    @DisplayName("getMembers Tests")
    class GetMembersTests {

        @Test
        @DisplayName("Should return paginated members with all filters")
        void testGetMembers_withAllFilters() {
            // Arrange
            String keyword = "john";
            MemberApprovalStatus approvalStatus = MemberApprovalStatus.APPROVED;
            int page = 0;
            int size = 10;
            String sortBy = "identifier";
            String direction = "asc";

            MemberListProjection projection1 = createMockProjection("john_doe", "APPROVED");
            MemberListProjection projection2 = createMockProjection("jane_doe", "APPROVED");

            Page<MemberListProjection> mockPage = new PageImpl<>(
                    Arrays.asList(projection1, projection2),
                    PageRequest.of(page, size, Sort.by(sortBy).ascending()),
                    2
            );

            when(memberRepository.findMembersWithProjection(
                    eq(keyword), eq(approvalStatus), eq(testCompanyId), any(Pageable.class))
            ).thenReturn(mockPage);

            // Act
            Page<MemberListDto> result = memberService.getMembers(
                    keyword, approvalStatus, testCompanyId, page, size, sortBy, direction
            );

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(2, result.getTotalElements());
            assertEquals("john_doe", result.getContent().get(0).getIdentifier());
            assertEquals(testCompanyId, result.getContent().get(0).getCompany().getUuid());
            assertEquals("CMP01", result.getContent().get(0).getCompany().getCode());
            assertEquals("Test Company", result.getContent().get(0).getCompany().getName());
            assertNotNull(result.getContent().get(0).getPermissions());
            assertEquals(1, result.getContent().get(0).getPermissions().size());

            verify(memberRepository, times(1))
                    .findMembersWithProjection(eq(keyword), eq(approvalStatus), eq(testCompanyId), any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination when parameters are null")
        void testGetMembers_withNullPaginationParams() {
            // Arrange
            Page<MemberListProjection> mockPage = new PageImpl<>(Collections.emptyList());
            when(memberRepository.findMembersWithProjection(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(mockPage);

            // Act
            Page<MemberListDto> result = memberService.getMembers(
                    null, null, null, null, null, "createdAt", null
            );

            // Assert
            assertNotNull(result);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(memberRepository).findMembersWithProjection(any(), any(), any(), pageableCaptor.capture());

            Pageable capturedPageable = pageableCaptor.getValue();
            assertEquals(0, capturedPageable.getPageNumber());
            assertEquals(10, capturedPageable.getPageSize());
            assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor("createdAt").getDirection());
        }

        @Test
        @DisplayName("Should sort descending when direction is desc")
        void testGetMembers_withDescendingSort() {
            // Arrange
            Page<MemberListProjection> mockPage = new PageImpl<>(Collections.emptyList());
            when(memberRepository.findMembersWithProjection(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(mockPage);

            // Act
            memberService.getMembers(null, null, null, 0, 10, "identifier", "desc");

            // Assert
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(memberRepository).findMembersWithProjection(any(), any(), any(), pageableCaptor.capture());

            Pageable capturedPageable = pageableCaptor.getValue();
            assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("identifier").getDirection());
        }

        @Test
        @DisplayName("Should handle empty result set")
        void testGetMembers_emptyResults() {
            // Arrange
            Page<MemberListProjection> mockPage = new PageImpl<>(Collections.emptyList());
            when(memberRepository.findMembersWithProjection(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(mockPage);

            // Act
            Page<MemberListDto> result = memberService.getMembers(
                    "nonexistent", null, null, 0, 10, "identifier", "asc"
            );

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should handle custom page size")
        void testGetMembers_customPageSize() {
            // Arrange
            int customSize = 25;
            Page<MemberListProjection> mockPage = new PageImpl<>(
                    Collections.emptyList(),
                    PageRequest.of(0, customSize),
                    0
            );
            when(memberRepository.findMembersWithProjection(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(mockPage);

            // Act
            memberService.getMembers(null, null, null, 0, customSize, "identifier", "asc");

            // Assert
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(memberRepository).findMembersWithProjection(any(), any(), any(), pageableCaptor.capture());
            assertEquals(customSize, pageableCaptor.getValue().getPageSize());
        }

        private MemberListProjection createMockProjection(String identifier, String status) {
            MemberListProjection projection = mock(MemberListProjection.class);
            when(projection.getMemberId()).thenReturn(UUID.randomUUID());
            when(projection.getIdentifier()).thenReturn(identifier);
            when(projection.getApprovalStatus()).thenReturn(status);
            when(projection.getCompanyId()).thenReturn(testCompanyId);
            when(projection.getCompanyCode()).thenReturn("CMP01");
            when(projection.getCompanyName()).thenReturn("Test Company");
            when(projection.getPermissions()).thenReturn(List.of("USER"));
            return projection;
        }
    }

    @Nested
    @DisplayName("adminProfile Tests")
    class AdminProfileTests {

        @Test
        @DisplayName("Should return admin profile successfully")
        void testAdminProfile_success() {
            // Arrange
            AdminInfoDto expectedDto = AdminInfoDto.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .phoneNumber("1234567890")
                    .build();

            when(authManager.getMember()).thenReturn(testMember);

            // Mock static method if using MemberMapper (adjust based on your implementation)
            try (MockedStatic<MemberMapper> mapperMock = mockStatic(MemberMapper.class)) {
                mapperMock.when(() -> MemberMapper.toAdminDto(testMember))
                        .thenReturn(expectedDto);

                // Act
                AdminInfoDto result = memberService.adminProfile();

                // Assert
                assertNotNull(result);
                assertEquals(expectedDto.getName(), result.getName());
                assertEquals(expectedDto.getEmail(), result.getEmail());
                assertEquals(expectedDto.getPhoneNumber(), result.getPhoneNumber());
                verify(authManager, times(1)).getMember();
                mapperMock.verify(() -> MemberMapper.toAdminDto(testMember), times(1));
            }
        }

        @Test
        @DisplayName("Should handle null member from authManager")
        void testAdminProfile_nullMember() {
            // Arrange
            when(authManager.getMember()).thenReturn(null);

            // Act & Assert
            assertThrows(NullPointerException.class, () ->
                    memberService.adminProfile()
            );
        }
    }

    @Nested
    @DisplayName("updateAdminProfile Tests")
    class UpdateAdminProfileTests {

        @Test
        @DisplayName("Should update admin profile successfully")
        void testUpdateAdminProfile_success() {
            // Arrange
            AdminInfoDto dto = AdminInfoDto.builder()
                    .name("John Doe")
                    .email("john@example.com")
                    .phoneNumber("1234567890")
                    .build();

            when(authManager.getBaseMemberUuid()).thenReturn(testMemberId);
            when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));

            // Mock static method
            try (MockedStatic<MemberMapper> mapperMock = mockStatic(MemberMapper.class)) {
                mapperMock.when(() -> MemberMapper.updateAdminFromDto(dto, testMember, aesConverter))
                        .then(invocation -> null);

                // Act
                memberService.updateAdminProfile(dto);

                // Assert
                verify(authManager, times(1)).getBaseMemberUuid();
                verify(memberRepository, times(1)).findById(testMemberId);
                mapperMock.verify(() -> MemberMapper.updateAdminFromDto(dto, testMember, aesConverter), times(1));
            }
        }

        @Test
        @DisplayName("Should throw NoSuchUserException when admin not found")
        void testUpdateAdminProfile_adminNotFound() {
            // Arrange
            AdminInfoDto dto = AdminInfoDto.builder()
                    .name("John")
                    .build();
            when(authManager.getBaseMemberUuid()).thenReturn(testMemberId);
            when(memberRepository.findById(testMemberId)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchUserException exception = assertThrows(NoSuchUserException.class, () ->
                    memberService.updateAdminProfile(dto)
            );

            assertEquals("Admin not found", exception.getMessage());
            verify(memberRepository, times(1)).findById(testMemberId);
        }

        @Test
        @DisplayName("Should handle null UUID from authManager")
        void testUpdateAdminProfile_nullUuid() {
            // Arrange
            AdminInfoDto dto = AdminInfoDto.builder().name("John").build();
            when(authManager.getBaseMemberUuid()).thenReturn(null);

            // Act & Assert
            assertThrows(Exception.class, () ->
                    memberService.updateAdminProfile(dto)
            );
        }
    }

    @Nested
    @DisplayName("approveMember Tests")
    class ApproveMemberTests {

        @Test
        @DisplayName("Should approve member successfully")
        void testApproveMember_success() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            Member approver = mock(Member.class);

            when(authManager.getMember()).thenReturn(approver);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            doNothing().when(testMember).approve(approver);

            // Act
            memberService.approveMember(memberId);

            // Assert
            verify(authManager, times(1)).getMember();
            verify(memberRepository, times(1)).findById(memberId);
            verify(testMember, times(1)).approve(approver);
            verify(memberRepository, times(1)).save(testMember);
        }

        @Test
        @DisplayName("Should throw NotFoundException when member not found")
        void testApproveMember_memberNotFound() {// Arrange
            UUID memberId = UUID.randomUUID();
            // REMOVE THIS LINE: when(authManager.getMember()).thenReturn(approver);
            // Because the service throws "Member not found" before checking the approver.

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    memberService.approveMember(memberId)
            );

            assertEquals("Member not found", exception.getMessage());
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle null approver")
        void testApproveMember_nullApprover() {
            UUID memberId = UUID.randomUUID();
            // Use a real Member object instead of a mock
            Member realMember = new Member();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(realMember));
            when(authManager.getMember()).thenReturn(null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () ->
                    memberService.approveMember(memberId)
            );
        }
    }

    @Nested
    @DisplayName("activateMember Tests")
    class ActivateMemberTests {

        @Test
        @DisplayName("Should activate member successfully")
        void testActivateMember_success() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            doNothing().when(testMember).restoreMember();

            // Act
            memberService.activateMember(memberId);

            // Assert
            verify(memberRepository, times(1)).findById(memberId);
            verify(testMember, times(1)).restoreMember();
            verify(memberRepository, times(1)).save(testMember);
        }

        @Test
        @DisplayName("Should throw NotFoundException when member not found")
        void testActivateMember_memberNotFound() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    memberService.activateMember(memberId)
            );

            assertEquals("Member not found", exception.getMessage());
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void testActivateMember_saveFails() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(testMember)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    memberService.activateMember(memberId)
            );
            verify(testMember, times(1)).restoreMember();
        }
    }

    @Nested
    @DisplayName("deActivateMember Tests")
    class DeActivateMemberTests {

        @Test
        @DisplayName("Should deactivate member successfully")
        void testDeActivateMember_success() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(any(Member.class))).thenReturn(testMember);
            doNothing().when(testMember).softDelete();

            // Act
            memberService.deActivateMember(memberId);

            // Assert
            verify(memberRepository, times(1)).findById(memberId);
            verify(testMember, times(1)).softDelete();
            verify(memberRepository, times(1)).save(testMember);
        }

        @Test
        @DisplayName("Should throw NotFoundException when member not found")
        void testDeActivateMember_memberNotFound() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    memberService.deActivateMember(memberId)
            );

            assertEquals("Member not found", exception.getMessage());
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle repository save failure")
        void testDeActivateMember_saveFails() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(testMember)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    memberService.deActivateMember(memberId)
            );
            verify(testMember, times(1)).softDelete();
        }
    }

    @Nested
    @DisplayName("updateMember Private Method Tests (via public methods)")
    class UpdateMemberTests {

        @Test
        @DisplayName("Should handle multiple operations on same member")
        void testMultipleOperations_sameMember() {
            // Arrange
            UUID memberId = UUID.randomUUID();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
            when(memberRepository.save(testMember)).thenReturn(testMember);

            // Act
            memberService.activateMember(memberId);
            memberService.deActivateMember(memberId);

            // Assert
            verify(memberRepository, times(2)).findById(memberId);
            verify(memberRepository, times(2)).save(testMember);
            verify(testMember, times(1)).restoreMember();
            verify(testMember, times(1)).softDelete();
        }
    }
}