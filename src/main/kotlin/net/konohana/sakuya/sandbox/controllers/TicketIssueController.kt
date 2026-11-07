package net.konohana.sakuya.sandbox.controllers

import net.konohana.sakuya.sandbox.models.IssueTicketRequest
import net.konohana.sakuya.sandbox.models.IssueTicketResponse
import net.konohana.sakuya.sandbox.services.TicketIssueService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * チケット発券エンドポイントを提供する REST コントローラー。
 *
 * @property ticketIssueService 発券業務サービス
 */
@RestController
@RequestMapping("/api/tickets")
class TicketIssueController(
    private val ticketIssueService: TicketIssueService,
) {

    /**
     * チケット発券 API エンドポイント。
     *
     * @param request 発券パラメータ
     * @return 発行されたチケット情報（HTTP 201 Created）
     */
    @PostMapping("/issue")
    fun issueTicket(
        @RequestBody request: IssueTicketRequest,
    ): ResponseEntity<IssueTicketResponse> {
        val response = ticketIssueService.processIssue(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
