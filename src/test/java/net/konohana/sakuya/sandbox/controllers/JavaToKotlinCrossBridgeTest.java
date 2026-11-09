package net.konohana.sakuya.sandbox.controllers;

import net.konohana.sakuya.sandbox.models.IssueTicketRequest;
import net.konohana.sakuya.sandbox.models.IssueTicketResponse;
import net.konohana.sakuya.sandbox.services.TicketIssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Java コントローラーから Kotlin サービス ({@link TicketIssueService}) への相互連携テスト。
 * static import を排し、クラス名修飾による呼び出しで統一しています。
 */
@ExtendWith(MockitoExtension.class)
class JavaToKotlinCrossBridgeTest {

    private MockMvc mockMvc;

    @Mock
    private TicketIssueService ticketIssueService;

    @BeforeEach
    void setUp() {
        JavaCrossBridgeController controller = new JavaCrossBridgeController(ticketIssueService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void verifyJavaControllerDelegatesToKotlinService() throws Exception {
        IssueTicketResponse mockResponse = new IssueTicketResponse(
            "TKT-CROSS-001",
            500,
            "01",
            "STA-FRTHB00101"
        );

        Mockito.when(ticketIssueService.processIssue(ArgumentMatchers.any(IssueTicketRequest.class)))
            .thenReturn(mockResponse);

        String requestJson = """
                {
                    "prefix": "QC",
                    "distanceKm": 10,
                    "staCode": "FRTHB00101",
                    "customBaseFare": 500
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cross/java-to-kotlin/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.ticketId").value("TKT-CROSS-001"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fare").value(500))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stationSuffix").value("01"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stationId").value("STA-FRTHB00101"));
    }

    @Test
    void verifyJavaControllerReturns400WhenPrefixOrStaCodeIsBlank() throws Exception {
        String invalidJson = """
                {
                    "prefix": "",
                    "distanceKm": 10,
                    "staCode": "FRTHB00101"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cross/java-to-kotlin/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
