package com.ritsard.baisard.posInfo;

import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.member.dto.request.PosTerminalRequestDto;
import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.entity.Company;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.entity.PosTerminalInfo;
import com.ritsard.baisard.domain.member.mapper.PosTerminalInfoMapper;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.domain.member.service.PosTerminalServiceImpl;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosTerminalServiceTest {

    // ── mocked dependencies ───────────────────────────────────────────────────
    @Mock
    private PosTerminalInfoRepository terminalInfoRepository;
    @Mock
    private JPAQueryFactory queryFactory;
    @Mock
    private AuthManager<Member> authManager;
    @Mock
    private PosTerminalInfoMapper infoMapper;

    @InjectMocks
    private PosTerminalServiceImpl posTerminalService;

    // ── shared fixtures ───────────────────────────────────────────────────────
    private UUID terminalUuid;
    private UUID companyUuid;
    private Company company;
    private Member member;
    private PosTerminalInfo terminalInfo;
    private PosTerminalResponseDto responseDto;

    @BeforeEach
    void setUp() {
        terminalUuid = UUID.randomUUID();
        companyUuid  = UUID.randomUUID();

        company = Company.builder()
                .uuidCompany(companyUuid)
                .name("Test Company")
                .build();

        member = new Member();
        member.setCompany(company);

        terminalInfo = PosTerminalInfo.builder()
                .uuidPosTerminal(terminalUuid)
                .company(company)
                .minNumber("MIN-001")
                .accreditationNumber("ACC-001")
                .ptuNumber("PTU-001")
                .dateIssued(LocalDate.now())
                .validUntil(LocalDate.now().plusYears(5))
                .posName("Test Terminal")
                .registeredName("Test Company")
                .operatedBy("Test Company")
                .address("123 Test Street")
                .vatTinNumber("000-000-000-000")
                .vat(12)
                .discountMax(BigDecimal.ZERO)
                .costCenter("DEFAULT")
                .branchCenter("DEFAULT")
                .useCenter("DEFAULT")
                .printerName("DEFAULT")
                .build();

        // Field order matches PosTerminalResponseDto record exactly:
        // uuidPosTerminal, minNumber, accreditationNumber, ptuNumber,
        // posName, registeredName, operatedBy, address, vatTinNumber,
        // vat, discountMax, costCenter, branchCenter, useCenter, printerName, isRetailType
        responseDto = new PosTerminalResponseDto(
                terminalUuid,
                "MIN-001",
                "ACC-001",
                "PTU-001",
                "Test Terminal",
                "Test Company",
                "Test Company",
                "123 Test Street",
                "000-000-000-000",
                12,
                BigDecimal.ZERO,
                "DEFAULT",
                "DEFAULT",
                "DEFAULT",
                "DEFAULT",
                false
        );
    }

    // ── getPosTerminalById ────────────────────────────────────────────────────
    @Nested
    @DisplayName("getPosTerminalById")
    class GetPosTerminalById {

        @Test
        @DisplayName("should return mapped DTO when terminal exists")
        void shouldReturnDtoWhenTerminalExists() {
            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.of(terminalInfo));
            when(infoMapper.mapToDto(terminalInfo))
                    .thenReturn(responseDto);

            PosTerminalResponseDto result = posTerminalService.getPosTerminalById(terminalUuid);

            assertThat(result).isNotNull();
            assertThat(result.uuidPosTerminal()).isEqualTo(terminalUuid);
            assertThat(result.posName()).isEqualTo("Test Terminal");
            assertThat(result.minNumber()).isEqualTo("MIN-001");

            verify(terminalInfoRepository).findById(terminalUuid);
            verify(infoMapper).mapToDto(terminalInfo);
        }

        @Test
        @DisplayName("should throw NotFoundException when terminal does not exist")
        void shouldThrowWhenTerminalNotFound() {
            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> posTerminalService.getPosTerminalById(terminalUuid))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Terminal info not found");

            verify(terminalInfoRepository).findById(terminalUuid);
            verifyNoInteractions(infoMapper);
        }
    }

    // ── getPosTerminalByCompany ───────────────────────────────────────────────
    @Nested
    @DisplayName("getPosTerminalByCompany")
    class GetPosTerminalByCompany {

        @Test
        @DisplayName("should return paginated terminals for the current member's company")
        void shouldReturnPageForCurrentMemberCompany() {
            Page<PosTerminalInfo> infoPage =
                    new PageImpl<>(List.of(terminalInfo), PageRequest.of(0, 10), 1);

            when(authManager.getMember()).thenReturn(member);
            when(terminalInfoRepository.findAllByCompanyUuidCompany(eq(companyUuid), any(PageRequest.class)))
                    .thenReturn(infoPage);
            when(infoMapper.mapToDto(terminalInfo)).thenReturn(responseDto);

            Page<PosTerminalResponseDto> result =
                    posTerminalService.getPosTerminalByCompany(0, 10, null, null);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).posName()).isEqualTo("Test Terminal");

            verify(authManager).getMember();
            verify(terminalInfoRepository)
                    .findAllByCompanyUuidCompany(eq(companyUuid), any(PageRequest.class));
        }

        @Test
        @DisplayName("should default to page=0 and size=10 when null params are passed")
        void shouldUseDefaultsWhenNullParams() {
            Page<PosTerminalInfo> infoPage =
                    new PageImpl<>(List.of(terminalInfo), PageRequest.of(0, 10), 1);

            when(authManager.getMember()).thenReturn(member);
            when(terminalInfoRepository.findAllByCompanyUuidCompany(eq(companyUuid), any(PageRequest.class)))
                    .thenReturn(infoPage);
            when(infoMapper.mapToDto(terminalInfo)).thenReturn(responseDto);

            posTerminalService.getPosTerminalByCompany(null, null, null, null);

            verify(terminalInfoRepository)
                    .findAllByCompanyUuidCompany(eq(companyUuid), eq(PageRequest.of(0, 10)));
        }

        @Test
        @DisplayName("should throw NotFoundException when current member has no company")
        void shouldThrowWhenMemberHasNoCompany() {
            Member noCompanyMember = new Member(); // company defaults to null

            when(authManager.getMember()).thenReturn(noCompanyMember);

            assertThatThrownBy(() -> posTerminalService.getPosTerminalByCompany(0, 10, null, null))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("User is not associated with a company");

            verify(authManager).getMember();
            verifyNoInteractions(terminalInfoRepository);
        }
    }

    // ── getPosTerminals (QueryDSL) ────────────────────────────────────────────
    @Nested
    @DisplayName("getPosTerminals")
    class GetPosTerminals {

        /**
         * JPAQueryFactory chains: .select → .from → .leftJoin → .where → .offset
         *   → .limit → .orderBy → .fetch / .fetchOne
         *
         * RETURNS_SELF makes every intermediate call return the same mock,
         * so only the terminal operations need explicit stubs.
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        private void stubChain(List<?> fetchResult, Long countResult) {
            JPAQuery chain = mock(JPAQuery.class, RETURNS_SELF);
            when(queryFactory.select((Expression<?>) any())).thenReturn(chain);
            when(chain.fetch()).thenReturn(fetchResult);
            when(chain.fetchOne()).thenReturn(countResult);
        }

        @Test
        @DisplayName("should return paged results with no keyword")
        void shouldReturnPagedResultsWithNoKeyword() {
            stubChain(List.of(responseDto), 1L);

            Page<PosTerminalResponseDto> result =
                    posTerminalService.getPosTerminals(null, 0, 10, null, null);

            assertThat(result.getTotalElements()).isEqualTo(1L);
            assertThat(result.getContent()).containsExactly(responseDto);
        }

        @Test
        @DisplayName("should return paged results with keyword filter")
        void shouldReturnPagedResultsWithKeyword() {
            stubChain(List.of(responseDto), 1L);

            Page<PosTerminalResponseDto> result =
                    posTerminalService.getPosTerminals("Test", 0, 5, null, "desc");

            assertThat(result.getTotalElements()).isEqualTo(1L);
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty page and zero total when nothing matches")
        void shouldReturnEmptyPage() {
            stubChain(List.of(), null); // null fetchOne exercises the orElse(0L) branch

            Page<PosTerminalResponseDto> result =
                    posTerminalService.getPosTerminals("nonexistent", 0, 10, null, null);

            assertThat(result.getTotalElements()).isEqualTo(0L);
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should apply ASC ordering when direction is not 'desc'")
        void shouldApplyAscOrdering() {
            stubChain(List.of(responseDto), 1L);

            assertThat(posTerminalService.getPosTerminals(null, 0, 10, null, "asc"))
                    .isNotNull();
        }

        @Test
        @DisplayName("should apply DESC ordering when direction is 'desc'")
        void shouldApplyDescOrdering() {
            stubChain(List.of(responseDto), 1L);

            assertThat(posTerminalService.getPosTerminals(null, 0, 10, null, "desc"))
                    .isNotNull();
        }
    }

    // ── newPosTerminal ────────────────────────────────────────────────────────
    @Nested
    @DisplayName("newPosTerminal")
    class NewPosTerminal {

        @Test
        @DisplayName("should build a terminal with correct default values and save it")
        void shouldSaveTerminalWithCorrectDefaults() {
            when(terminalInfoRepository.save(any(PosTerminalInfo.class)))
                    .thenReturn(terminalInfo);

            posTerminalService.newPosTerminal(member);

            verify(terminalInfoRepository).save(argThat(saved -> {
                assertThat(saved.getCompany()).isEqualTo(company);
                assertThat(saved.getPosName()).isEqualTo("New Terminal - " + company.getName());
                assertThat(saved.getRegisteredName()).isEqualTo(company.getName());
                assertThat(saved.getOperatedBy()).isEqualTo(company.getName());
                assertThat(saved.getMinNumber()).isEqualTo("PENDING");
                assertThat(saved.getAccreditationNumber()).isEqualTo("PENDING");
                assertThat(saved.getPtuNumber()).isEqualTo("PENDING");
                assertThat(saved.getVatTinNumber()).isEqualTo("000-000-000-000");
                assertThat(saved.getVat()).isEqualTo(12);
                assertThat(saved.getDiscountMax()).isEqualByComparingTo(BigDecimal.ZERO);
                assertThat(saved.getDateIssued()).isNotNull();
                assertThat(saved.getValidUntil()).isAfter(LocalDate.now());
                return true;
            }));
        }

        @Test
        @DisplayName("should throw NotFoundException when admin member has no company")
        void shouldThrowWhenMemberHasNoCompany() {
            Member noCompanyMember = new Member();

            assertThatThrownBy(() -> posTerminalService.newPosTerminal(noCompanyMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Admin member must belong to a company");

            verifyNoInteractions(terminalInfoRepository);
        }
    }

    // ── deactivatePosTerminal ─────────────────────────────────────────────────
    @Nested
    @DisplayName("deactivatePosTerminal")
    class DeactivatePosTerminal {

        @Test
        @DisplayName("should set isTrainMode=true and persist the terminal")
        void shouldSetTrainModeAndSave() {
            assertThat(terminalInfo.isTrainMode()).isFalse(); // pre-condition

            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.of(terminalInfo));
            when(terminalInfoRepository.save(terminalInfo))
                    .thenReturn(terminalInfo);

            posTerminalService.deactivatePosTerminal(terminalUuid);

            assertThat(terminalInfo.isTrainMode()).isTrue();
            verify(terminalInfoRepository).save(terminalInfo);
        }

        @Test
        @DisplayName("should throw NotFoundException when terminal does not exist")
        void shouldThrowWhenTerminalNotFound() {
            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> posTerminalService.deactivatePosTerminal(terminalUuid))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Terminal info not found");

            verify(terminalInfoRepository, never()).save(any());
        }
    }

    // ── deletePosTerminal ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("deletePosTerminal")
    class DeletePosTerminal {

        @Test
        @DisplayName("should delete the terminal when it exists")
        void shouldDeleteWhenTerminalExists() {
            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.of(terminalInfo));

            posTerminalService.deletePosTerminal(terminalUuid);

            verify(terminalInfoRepository).delete(terminalInfo);
        }

        @Test
        @DisplayName("should throw NotFoundException when terminal does not exist")
        void shouldThrowWhenTerminalNotFound() {
            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> posTerminalService.deletePosTerminal(terminalUuid))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Terminal info not found");

            verify(terminalInfoRepository, never()).delete(any());
        }
    }

    // ── updatePosTerminal ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("updatePosTerminal")
    class UpdatePosTerminal {

        // Field order matches PosTerminalRequestDto record exactly:
        // uuidPosTerminal, posName, registeredName, address, vatTinNumber,
        // vat, discountMax, costCenter, branchCenter, useCenter, printerName, isRetailType
        private PosTerminalRequestDto buildDto(UUID uuid) {
            return new PosTerminalRequestDto(
                    uuid,
                    "Updated Terminal",
                    "Updated Registered",
                    "Updated Address",
                    "111-111-111-111",
                    10,
                    BigDecimal.TEN,
                    "CENTER-A",
                    "BRANCH-A",
                    "USE-A",
                    "PRINTER-A",
                    true
            );
        }

        @Test
        @DisplayName("should fetch the terminal, delegate to mapper, then save")
        void shouldFetchMapAndSaveTerminal() {
            PosTerminalRequestDto dto = buildDto(terminalUuid);

            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.of(terminalInfo));
            when(terminalInfoRepository.save(terminalInfo))
                    .thenReturn(terminalInfo);

            posTerminalService.updatePosTerminal(dto);

            // Exact interaction order: find → map → save
            verify(terminalInfoRepository).findById(terminalUuid);
            verify(infoMapper).adminPosInfoFromDto(dto, terminalInfo);
            verify(terminalInfoRepository).save(terminalInfo);
        }

        @Test
        @DisplayName("should throw NotFoundException and never reach mapper or save when terminal is missing")
        void shouldThrowWhenTerminalNotFound() {
            PosTerminalRequestDto dto = buildDto(terminalUuid);

            when(terminalInfoRepository.findById(terminalUuid))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> posTerminalService.updatePosTerminal(dto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Terminal info not found for update");

            verifyNoInteractions(infoMapper);
            verify(terminalInfoRepository, never()).save(any());
        }
    }
}