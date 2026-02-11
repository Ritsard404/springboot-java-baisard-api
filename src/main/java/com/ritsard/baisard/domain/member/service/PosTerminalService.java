package com.ritsard.baisard.domain.member.service;

import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.member.enums.MemberApprovalStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PosTerminalService {
    PosTerminalResponseDto getPosTerminalById(UUID uuidPosTerminal);

    Page<PosTerminalResponseDto> getPosTerminalByCompany(Integer page,
                                                         Integer size,
                                                         String sortBy,
                                                         String direction);


    Page<PosTerminalResponseDto> getPosTerminals(String keyword,
                                                 Integer page,
                                                 Integer size,
                                                 String sortBy,
                                                 String direction);

    void newPosTerminal(Member adminMember);

    void deactivatePosTerminal(UUID uuidPosTerminal);

    void deletePosTerminal(UUID uuidPosTerminal);
}
