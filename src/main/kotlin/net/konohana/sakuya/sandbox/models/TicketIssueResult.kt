package net.konohana.sakuya.sandbox.models

data class TicketIssueResult(
    val ticketId: String,
    val fare: Int,
    val stationSuffix: String,
)
