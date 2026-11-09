package net.konohana.sakuya.sandbox.controllers;

import net.konohana.sakuya.sandbox.models.IssueTicketRequest;
import net.konohana.sakuya.sandbox.models.IssueTicketResponse;
import net.konohana.sakuya.sandbox.services.TicketIssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Java コントローラーから Kotlin サービス ({@link TicketIssueService}) を呼び出す相互運用検証エンドポイント。
 */
@RestController
@RequestMapping("/api/cross/java-to-kotlin")
public class JavaCrossBridgeController {

    private final TicketIssueService ticketIssueService;

    public JavaCrossBridgeController(TicketIssueService ticketIssueService) {
        this.ticketIssueService = ticketIssueService;
    }

    /**
     * Kotlin サービスを呼び出して発券処理を実行します。
     *
     * @param request 発券リクエスト (Kotlin data class)
     * @return 発券レスポンス (Kotlin data class)
     */
    @PostMapping("/issue")
    public ResponseEntity<IssueTicketResponse> issue(@RequestBody IssueTicketRequest request) {
        if (request.getPrefix().isBlank() || request.getStaCode().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        IssueTicketResponse response = ticketIssueService.processIssue(request);
        return ResponseEntity.ok(response);
    }
}
