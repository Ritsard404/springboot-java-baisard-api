package com.ritsard.baisard.domain.member.controller;

import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.service.PosTerminalService;
import com.ritsard.baisard.jwt.utils.AuthManager;
import com.ritsard.baisard.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/pos-terminal")
@RequiredArgsConstructor
@Tag(name = "POS Terminal", description = "APIs for managing POS Terminal Information")
public class PosTerminalController {

    private final PosTerminalService terminalService;
    private final AuthManager<Member> authManager;

    @Operation(summary = "Get all terminals (Global)")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Page<PosTerminalResponseDto>> getAllTerminals(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(terminalService.getPosTerminals(keyword, page, size, sortBy, direction));
    }

    @Operation(summary = "Get terminal by ID")
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<PosTerminalResponseDto> getById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(terminalService.getPosTerminalById(uuid));
    }

    @Operation(summary = "Get terminals for the logged-in user's company")
    @GetMapping("/my-company")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<PosTerminalResponseDto>> getByCompany(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(terminalService.getPosTerminalByCompany(page, size, sortBy, direction));
    }

//    @Operation(summary = "Create a new terminal")
//    @PostMapping("/new")
//    @PreAuthorize("hasAuthority('SUPERADMIN')")
//    public ResponseEntity<Void> createTerminal() {
//        terminalService.newPosTerminal(authManager.getMember());
//        return ResponseEntity.ok().build();
//    }

    @Operation(summary = "Deactivate terminal (Set to Train Mode)")
    @PatchMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID uuid) {
        terminalService.deactivatePosTerminal(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete terminal record")
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        terminalService.deletePosTerminal(uuid);
        return ResponseEntity.noContent().build();
    }
}