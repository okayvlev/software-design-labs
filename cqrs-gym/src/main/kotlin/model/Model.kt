package model

import java.time.LocalDateTime


data class Member(
    val id: Long,
    val name: String,
    val registered: LocalDateTime
)

data class Statistics(
    val memberId: Long,
    val attendance: List<LocalDateTime>,
    val averageFrequency: Double,
    val averageDuration: Double
)

enum class Direction {
    In, Out
}