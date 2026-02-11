package com.ritsard.baisard.domain.member.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.entity.*;
import com.ritsard.baisard.domain.member.mapper.PosTerminalInfoMapper;
import com.ritsard.baisard.domain.member.repository.CompanyRepository;
import com.ritsard.baisard.domain.member.repository.PosTerminalInfoRepository;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PosTerminalServiceImpl implements PosTerminalService {
    private final PosTerminalInfoRepository terminalInfoRepository;
    private final JPAQueryFactory queryFactory;
    private final AuthManager<Member> authManager;
    private final PosTerminalInfoMapper infoMapper;

    @Override
    @Transactional(readOnly = true)
    public PosTerminalResponseDto getPosTerminalById(UUID uuidPosTerminal) {
        return infoMapper.mapToDto(terminalInfo(uuidPosTerminal));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PosTerminalResponseDto> getPosTerminalByCompany(Integer page, Integer size, String sortBy, String direction) {
        Member currentMember = authManager.getMember();
        if (currentMember.getCompany() == null) {
            throw new NotFoundException("User is not associated with a company.");
        }

        UUID companyUuid = currentMember.getCompany().getUuidCompany();
        int pageNum = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        // Using JPA Repository here is simpler for a straight relationship lookup
        return terminalInfoRepository.findAllByCompanyUuidCompany(companyUuid, pageRequest)
                .map(infoMapper::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PosTerminalResponseDto> getPosTerminals(String keyword, Integer page, Integer size, String sortBy, String direction) {
        QPosTerminalInfo terminal = QPosTerminalInfo.posTerminalInfo;
        QCompany company = QCompany.company;

        int pageNum = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        BooleanBuilder where = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            where.and(terminal.posName.containsIgnoreCase(keyword)
                    .or(terminal.ptuNumber.containsIgnoreCase(keyword))
                    .or(company.name.containsIgnoreCase(keyword)));
        }

        List<PosTerminalResponseDto> content = queryFactory
                .select(Projections.constructor(PosTerminalResponseDto.class,
                        terminal.uuidPosTerminal,
                        terminal.minNumber,
                        terminal.accreditationNumber,
                        terminal.ptuNumber,
                        terminal.posName,
                        terminal.registeredName,
                        terminal.operatedBy,
                        terminal.address,
                        terminal.vatTinNumber,
                        terminal.vat,
                        terminal.discountMax,
                        terminal.costCenter,
                        terminal.branchCenter,
                        terminal.useCenter,
                        terminal.printerName,
                        terminal.isRetailType
                ))
                .from(terminal)
                .leftJoin(terminal.company, company)
                .where(where)
                .offset((long) pageNum * pageSize)
                .limit(pageSize)
                .orderBy("desc".equalsIgnoreCase(direction) ? terminal.createdAt.desc() : terminal.createdAt.asc())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory.select(terminal.count()).from(terminal).leftJoin(terminal.company, company).where(where).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, PageRequest.of(pageNum, pageSize), total);
    }

    @Override
    public void newPosTerminal(Member adminMember) {
        if (adminMember.getCompany() == null)
            throw new NotFoundException("Admin member must belong to a company to create a terminal");


        // FIX: Ensure all nullable=false fields are populated to avoid SQL errors
        PosTerminalInfo newTerminal = PosTerminalInfo.builder()
                .company(adminMember.getCompany())
                .posName("New Terminal - " + adminMember.getCompany().getName())
                .registeredName(adminMember.getCompany().getName())
                .operatedBy(adminMember.getCompany().getName())
                .minNumber("PENDING")
                .accreditationNumber("PENDING")
                .ptuNumber("PENDING")
                .dateIssued(LocalDate.now()) // Required field
                .validUntil(LocalDate.now().plusYears(5)) // Required field
                .address(adminMember.getCompany().getName())
                .vatTinNumber("000-000-000-000")
                .vat(12)
                .discountMax(BigDecimal.ZERO)
                .costCenter("DEFAULT")
                .branchCenter("DEFAULT")
                .useCenter("DEFAULT")
//                .dbName("pos_db_" + adminMember.getCompany().getCode()) // Required field
                .printerName("DEFAULT")
                .build();

        terminalInfoRepository.save(newTerminal);
    }

    @Override
    public void deactivatePosTerminal(UUID uuidPosTerminal) {
        PosTerminalInfo info = terminalInfo(uuidPosTerminal);
        info.setTrainMode(true);
        terminalInfoRepository.save(info);
    }

    @Override
    public void deletePosTerminal(UUID uuidPosTerminal) {
        terminalInfoRepository.delete(terminalInfo(uuidPosTerminal));
    }

    private PosTerminalInfo terminalInfo(UUID uuidPosTerminal) {
        return terminalInfoRepository.findById(uuidPosTerminal)
                .orElseThrow(() -> new NotFoundException("Terminal info not found"));
    }
}