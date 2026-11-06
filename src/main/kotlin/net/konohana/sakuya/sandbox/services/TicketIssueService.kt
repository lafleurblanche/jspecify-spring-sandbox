package net.konohana.sakuya.sandbox.services

import net.konohana.sakuya.sandbox.bridge.StationId
import net.konohana.sakuya.sandbox.domain.FareCalculationEngine
import net.konohana.sakuya.sandbox.domain.InMemoryTicketRepository
import net.konohana.sakuya.sandbox.models.IssueTicketRequest
import net.konohana.sakuya.sandbox.models.IssueTicketResponse
import org.springframework.stereotype.Service

/**
 * 発券業務のオーケストレーションを行うアプリケーションサービス。
 *
 * Java 側純粋計算層（JSpecify による Non-Null 保証）、インライン値クラス（[StationId]）、
 * およびインメモリ状態管理の連携を担います。
 *
 * @property ticketRepository インメモリチケット採番・保持リポジトリ
 * @property stationLookupService 外部駅情報検証サービススタブ
 */
@Service
class TicketIssueService(
    private val ticketRepository: InMemoryTicketRepository,
    private val stationLookupService: StationExternalLookupService,
) {

    /**
     * 発券要求を受け付け、バリデーション・運賃計算・採番・保存を一括実行します。
     *
     * @param request 発券リクエスト DTO
     * @return 発券結果レスポンス DTO
     */
    fun processIssue(request: IssueTicketRequest): IssueTicketResponse {
        val stationId = StationId(request.staCode)
        stationLookupService.verifyStation(stationId)

        val seqNo = ticketRepository.nextSequence()

        // Java @NullMarked により ticketId は厳格な String（プラットフォーム型ではない）
        val ticketId: String = FareCalculationEngine.formatTicketCode(request.prefix, seqNo)
        val fare: Int = FareCalculationEngine.calculateTotalFare(request.distanceKm, request.customBaseFare)
        val suffix: String? = FareCalculationEngine.extractStationSuffix(request.staCode)

        ticketRepository.save(ticketId, request.staCode)

        return IssueTicketResponse(
            ticketId = ticketId,
            fare = fare,
            stationSuffix = suffix ?: "00",
            stationId = stationId.formatted(),
        )
    }
}
