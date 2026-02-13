package com.ritsard.baisard.posInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ritsard.baisard.domain.member.controller.PosTerminalController;
import com.ritsard.baisard.domain.member.dto.request.PosTerminalRequestDto;
import com.ritsard.baisard.domain.member.dto.response.PosTerminalResponseDto;
import com.ritsard.baisard.domain.member.service.PosTerminalService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ─────────────────────────────────────────────────────────────────────────────
// Standalone MockMvc — no Spring context, no security filter chain needed.
// Same Mockito pattern as AuthServiceTest.
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class PosTerminalControllerTest {

    // ── inline exception handler ──────────────────────────────────────────────
    // Registered into standaloneSetup so NotFoundException → 404 works
    // without loading the full Spring context or a real @ControllerAdvice.
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(NotFoundException.class)
        ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @Mock
    private PosTerminalService terminalService;

    @InjectMocks
    private PosTerminalController posTerminalController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // ── shared fixtures ───────────────────────────────────────────────────────
    private UUID terminalUuid;
    private PosTerminalResponseDto responseDto;
    private Page<PosTerminalResponseDto> responsePage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(posTerminalController)
                // Register the inline handler so NotFoundException maps to 404
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        // JavaTimeModule handles LocalDate serialization in PosTerminalInfo
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        terminalUuid = UUID.randomUUID();

        // Field order matches PosTerminalResponseDto record:
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

        responsePage = new PageImpl<>(List.of(responseDto), PageRequest.of(0, 10), 1);
    }

    // ── helper ────────────────────────────────────────────────────────────────
    // Field order matches PosTerminalRequestDto record:
    // uuidPosTerminal, posName, registeredName, address, vatTinNumber,
    // vat, discountMax, costCenter, branchCenter, useCenter, printerName, isRetailType
    private PosTerminalRequestDto buildRequestDto() {
        return new PosTerminalRequestDto(
                terminalUuid,
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

    /* =======================================================================
       GET /pos-terminal/all
       ======================================================================= */
    @Nested
    @DisplayName("GET /pos-terminal/all")
    class GetAllTerminals {

        @Test
        @DisplayName("200 — returns paginated terminals with default params")
        void shouldReturn200WithDefaultParams() throws Exception {
            when(terminalService.getPosTerminals(null, 0, 10, "createdAt", "desc"))
                    .thenReturn(responsePage);

            mockMvc.perform(get("/pos-terminal/all"))
                    .andExpect(status().isOk())
                    // ── NOTE: replace "$.data" with your actual ApiResponse wrapper field ──
                    // e.g. "$.result", "$.body", "$.payload" — paste ApiResponse and I'll fix
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].posName", is("Test Terminal")))
                    .andExpect(jsonPath("$.data.content[0].minNumber", is("MIN-001")))
                    .andExpect(jsonPath("$.data.totalElements", is(1)));

            verify(terminalService).getPosTerminals(null, 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("200 — forwards keyword, page, size and direction params to service")
        void shouldForwardParamsToService() throws Exception {
            when(terminalService.getPosTerminals("Test", 1, 5, "createdAt", "asc"))
                    .thenReturn(responsePage);

            mockMvc.perform(get("/pos-terminal/all")
                            .param("keyword", "Test")
                            .param("page", "1")
                            .param("size", "5")
                            .param("direction", "asc"))
                    .andExpect(status().isOk());

            verify(terminalService).getPosTerminals("Test", 1, 5, "createdAt", "asc");
        }

        @Test
        @DisplayName("200 — returns empty page when no terminals exist")
        void shouldReturn200WithEmptyPage() throws Exception {
            Page<PosTerminalResponseDto> emptyPage =
                    new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(terminalService.getPosTerminals(null, 0, 10, "createdAt", "desc"))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/pos-terminal/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(0)))
                    .andExpect(jsonPath("$.data.totalElements", is(0)));
        }
    }

    /* =======================================================================
       GET /pos-terminal/{uuid}
       ======================================================================= */
    @Nested
    @DisplayName("GET /pos-terminal/{uuid}")
    class GetById {

        @Test
        @DisplayName("200 — returns terminal DTO when found")
        void shouldReturn200WhenFound() throws Exception {
            when(terminalService.getPosTerminalById(terminalUuid))
                    .thenReturn(responseDto);

            mockMvc.perform(get("/pos-terminal/{uuid}", terminalUuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.uuidPosTerminal", is(terminalUuid.toString())))
                    .andExpect(jsonPath("$.data.posName", is("Test Terminal")))
                    .andExpect(jsonPath("$.data.minNumber", is("MIN-001")))
                    .andExpect(jsonPath("$.data.ptuNumber", is("PTU-001")));

            verify(terminalService).getPosTerminalById(terminalUuid);
        }

        @Test
        @DisplayName("404 — propagates NotFoundException from service")
        void shouldReturn404WhenNotFound() throws Exception {
            when(terminalService.getPosTerminalById(terminalUuid))
                    .thenThrow(new NotFoundException("Terminal info not found"));

            mockMvc.perform(get("/pos-terminal/{uuid}", terminalUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Terminal info not found")));
        }
    }

    /* =======================================================================
       PATCH /pos-terminal/{uuid}/deactivate
       ======================================================================= */
    @Nested
    @DisplayName("PATCH /pos-terminal/{uuid}/deactivate")
    class Deactivate {

        @Test
        @DisplayName("200 — deactivates terminal successfully")
        void shouldReturn200OnSuccess() throws Exception {
            doNothing().when(terminalService).deactivatePosTerminal(terminalUuid);

            mockMvc.perform(patch("/pos-terminal/{uuid}/deactivate", terminalUuid))
                    .andExpect(status().isOk())
                    // ── NOTE: replace "$.message" if your ApiResponse uses a different field ──
                    .andExpect(jsonPath("$.message", is("Terminal deactivated successfully")));

            verify(terminalService).deactivatePosTerminal(terminalUuid);
        }

        @Test
        @DisplayName("404 — propagates NotFoundException from service")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new NotFoundException("Terminal info not found"))
                    .when(terminalService).deactivatePosTerminal(terminalUuid);

            mockMvc.perform(patch("/pos-terminal/{uuid}/deactivate", terminalUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Terminal info not found")));
        }

        @Test
        @DisplayName("service is called exactly once with the correct UUID")
        void shouldCallServiceOnce() throws Exception {
            doNothing().when(terminalService).deactivatePosTerminal(terminalUuid);

            mockMvc.perform(patch("/pos-terminal/{uuid}/deactivate", terminalUuid));

            verify(terminalService, times(1)).deactivatePosTerminal(terminalUuid);
            verifyNoMoreInteractions(terminalService);
        }
    }

    /* =======================================================================
       DELETE /pos-terminal/{uuid}
       ======================================================================= */
    @Nested
    @DisplayName("DELETE /pos-terminal/{uuid}")
    class Delete {

        @Test
        @DisplayName("200 — deletes terminal successfully")
        void shouldReturn200OnSuccess() throws Exception {
            doNothing().when(terminalService).deletePosTerminal(terminalUuid);

            mockMvc.perform(delete("/pos-terminal/{uuid}", terminalUuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message", is("Terminal record deleted successfully")));

            verify(terminalService).deletePosTerminal(terminalUuid);
        }

        @Test
        @DisplayName("404 — propagates NotFoundException from service")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new NotFoundException("Terminal info not found"))
                    .when(terminalService).deletePosTerminal(terminalUuid);

            mockMvc.perform(delete("/pos-terminal/{uuid}", terminalUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Terminal info not found")));
        }

        @Test
        @DisplayName("service is called exactly once with the correct UUID")
        void shouldCallServiceOnce() throws Exception {
            doNothing().when(terminalService).deletePosTerminal(terminalUuid);

            mockMvc.perform(delete("/pos-terminal/{uuid}", terminalUuid));

            verify(terminalService, times(1)).deletePosTerminal(terminalUuid);
            verifyNoMoreInteractions(terminalService);
        }
    }

    /* =======================================================================
       GET /pos-terminal/my-company
       ======================================================================= */
    @Nested
    @DisplayName("GET /pos-terminal/my-company")
    class GetByCompany {

        @Test
        @DisplayName("200 — returns paginated company terminals with default params")
        void shouldReturn200WithDefaultParams() throws Exception {
            when(terminalService.getPosTerminalByCompany(0, 10, "createdAt", "desc"))
                    .thenReturn(responsePage);

            mockMvc.perform(get("/pos-terminal/my-company"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].posName", is("Test Terminal")))
                    .andExpect(jsonPath("$.data.totalElements", is(1)));

            verify(terminalService).getPosTerminalByCompany(0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("200 — forwards page, size and direction params to service")
        void shouldForwardPaginationParams() throws Exception {
            when(terminalService.getPosTerminalByCompany(2, 5, "createdAt", "asc"))
                    .thenReturn(responsePage);

            mockMvc.perform(get("/pos-terminal/my-company")
                            .param("page", "2")
                            .param("size", "5")
                            .param("direction", "asc"))
                    .andExpect(status().isOk());

            verify(terminalService).getPosTerminalByCompany(2, 5, "createdAt", "asc");
        }

        @Test
        @DisplayName("200 — returns empty page when company has no terminals")
        void shouldReturn200WithEmptyPage() throws Exception {
            Page<PosTerminalResponseDto> emptyPage =
                    new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(terminalService.getPosTerminalByCompany(0, 10, "createdAt", "desc"))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/pos-terminal/my-company"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(0)));
        }
    }

    /* =======================================================================
       PUT /pos-terminal/update
       ======================================================================= */
    @Nested
    @DisplayName("PUT /pos-terminal/update")
    class Update {

        @Test
        @DisplayName("200 — updates terminal successfully")
        void shouldReturn200OnSuccess() throws Exception {
            PosTerminalRequestDto dto = buildRequestDto();
            doNothing().when(terminalService).updatePosTerminal(any());

            mockMvc.perform(put("/pos-terminal/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message",
                            is("Terminal information updated successfully")));

            verify(terminalService).updatePosTerminal(any(PosTerminalRequestDto.class));
        }

        @Test
        @DisplayName("404 — propagates NotFoundException when terminal not found")
        void shouldReturn404WhenNotFound() throws Exception {
            PosTerminalRequestDto dto = buildRequestDto();

            doThrow(new NotFoundException("Terminal info not found for update"))
                    .when(terminalService).updatePosTerminal(any());

            mockMvc.perform(put("/pos-terminal/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Terminal info not found for update")));
        }

        @Test
        @DisplayName("400 — returns bad request when body is missing")
        void shouldReturn400WhenBodyMissing() throws Exception {
            mockMvc.perform(put("/pos-terminal/update")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(terminalService);
        }

        @Test
        @DisplayName("service receives the exact DTO fields from the request body")
        void shouldPassCorrectDtoToService() throws Exception {
            PosTerminalRequestDto dto = buildRequestDto();
            doNothing().when(terminalService).updatePosTerminal(any());

            mockMvc.perform(put("/pos-terminal/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));

            verify(terminalService).updatePosTerminal(argThat(received -> {
                assert received.posName().equals("Updated Terminal");
                assert received.vatTinNumber().equals("111-111-111-111");
                assert received.isRetailType();
                return true;
            }));
        }
    }
}