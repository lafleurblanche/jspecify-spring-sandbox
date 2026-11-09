package net.konohana.sakuya.sandbox.controllers;

import net.konohana.sakuya.sandbox.models.FareQuoteRequest;
import net.konohana.sakuya.sandbox.models.FareQuoteResponse;
import net.konohana.sakuya.sandbox.services.FareQuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Java 側運賃見積り REST コントローラー。
 */
@RestController
@RequestMapping("/api/java/fares")
public class FareQuoteController {

    private final FareQuoteService fareQuoteService;

    public FareQuoteController(FareQuoteService fareQuoteService) {
        this.fareQuoteService = fareQuoteService;
    }

    /**
     * 運賃見積り計算エンドポイント。
     *
     * @param request 見積りリクエスト
     * @return 見積りレスポンス
     */
    @PostMapping("/quote")
    public ResponseEntity<FareQuoteResponse> quoteFare(@RequestBody FareQuoteRequest request) {
        if (request.fromStaCode().isBlank() || request.toStaCode().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        FareQuoteResponse response = fareQuoteService.calculateQuote(request);
        return ResponseEntity.ok(response);
    }
}
