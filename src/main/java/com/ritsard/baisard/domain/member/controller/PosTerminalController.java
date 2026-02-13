package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.request.PosTerminalRequestDto;
import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.service.PosTerminalService;
import com.ritsard.baisard.utils.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/pos-terminal")
@RequiredArgsConstructor
@Tag(name = "POS Terminal", description = "APIs for managing POS Terminal Information")
public class PosTerminalController {

    private final PosTerminalService terminalService;

    /* =========================================================
       GLOBAL TERMINAL MANAGEMENT (SUPERADMIN)
       ========================================================= */

    @GetMapping("/all")
    @Operation(summary = "Get all terminals (Global)")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<Page<PosTerminalResponseDto>> getAllTerminals(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<PosTerminalResponseDto> terminals = terminalService.getPosTerminals(
                keyword, page, size, sortBy, direction
        );
        return ApiResponse.ok(terminals, "All terminals fetched successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get terminal by ID")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<PosTerminalResponseDto> getById(@PathVariable UUID uuid) {
        PosTerminalResponseDto terminal = terminalService.getPosTerminalById(uuid);
        return ApiResponse.ok(terminal, "Terminal info fetched successfully");
    }

    @PatchMapping("/{uuid}/deactivate")
    @Operation(summary = "Deactivate terminal (Set to Train Mode)")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<?> deactivate(@PathVariable UUID uuid) {
        terminalService.deactivatePosTerminal(uuid);
        return ApiResponse.ok("Terminal deactivated successfully");
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete terminal record")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ApiResponse<?> delete(@PathVariable UUID uuid) {
        terminalService.deletePosTerminal(uuid);
        return ApiResponse.ok("Terminal record deleted successfully");
    }

    /* =========================================================
       COMPANY TERMINAL MANAGEMENT (ADMIN & SUPERADMIN)
       ========================================================= */

    @GetMapping("/my-company")
    @Operation(summary = "Get terminals for the logged-in user's company")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<Page<PosTerminalResponseDto>> getByCompany(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<PosTerminalResponseDto> terminals = terminalService.getPosTerminalByCompany(
                page, size, sortBy, direction
        );
        return ApiResponse.ok(terminals, "Company terminals fetched successfully");
    }

    @PutMapping("/update")
    @Operation(summary = "Update terminal details")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    public ApiResponse<?> update(@Valid @RequestBody PosTerminalRequestDto dto) {
        terminalService.updatePosTerminal(dto);
        return ApiResponse.ok("Terminal information updated successfully");
    }
}