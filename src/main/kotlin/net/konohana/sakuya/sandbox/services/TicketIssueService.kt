package net.konohana.sakuya.sandbox.services

import org.springframework.stereotype.Service
import net.konohana.sakuya.sandbox.domain.FareCalculationEngine
import net.konohana.sakuya.sandbox.models.TicketIssueResult

@Service
class TicketIssueService {

    fun issueTicket(
        prefix: String,
        sequenceNo: Int,
        distanceKm: Int,
        staCode: String,
        customBaseFare: Int? = null,
    ): TicketIssueResult {
        // Java側が @NullMarked のため、ticketId はプラットフォーム型（String!）ではなく非nullの String
        val ticketId: String = FareCalculationEngine.formatTicketCode(prefix, sequenceNo)

        // 第2引数は @Nullable なので、Kotlin の Int? がそのままスムーズに渡せる
        val fare: Int = FareCalculationEngine.calculateTotalFare(distanceKm, customBaseFare)

        // extractStationSuffix は @Nullable なので、戻り値型は厳密に String?
        val suffix: String? = FareCalculationEngine.extractStationSuffix(staCode)

        return TicketIssueResult(
            ticketId = ticketId,
            fare = fare,
            stationSuffix = suffix ?: "00",
        )
    }
}
