package net.konohana.sakuya.sandbox.controllers;

import net.konohana.sakuya.sandbox.models.FareQuoteRequest;
import net.konohana.sakuya.sandbox.models.FareQuoteResponse;
import net.konohana.sakuya.sandbox.services.FareQuoteService;
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
 * Java 側 {@link FareQuoteController} に対する MockMvc 単体・結合テスト。
 *
 * static import を使用せず、クラス名修飾による呼び出し規約を徹底しています。
 */
@ExtendWith(MockitoExtension.class)
class FareQuoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FareQuoteService fareQuoteService;

    @BeforeEach
    void setUp() {
        FareQuoteController controller = new FareQuoteController(fareQuoteService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void quoteFare_returns200AndCalculatedResponse() throws Exception {
        FareQuoteResponse mockResponse = new FareQuoteResponse(
            "FRTHB00101",
            "FRTHB00105",
            800,
            "From Sakuragaoka (4 km)"
        );

        Mockito.when(fareQuoteService.calculateQuote(ArgumentMatchers.any(FareQuoteRequest.class)))
            .thenReturn(mockResponse);

        String jsonPayload = """
                {
                    "fromStaCode": "FRTHB00101",
                    "toStaCode": "FRTHB00105",
                    "customBaseFare": 500
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/java/fares/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.fromStaCode").value("FRTHB00101"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.toStaCode").value("FRTHB00105"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalFare").value(800))
            .andExpect(MockMvcResultMatchers.jsonPath("$.note").value("From Sakuragaoka (4 km)"));
    }

    @Test
    void quoteFare_returns400WhenCodeIsBlank() throws Exception {
        String invalidPayload = """
                {
                    "fromStaCode": "",
                    "toStaCode": "FRTHB00105"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/java/fares/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
