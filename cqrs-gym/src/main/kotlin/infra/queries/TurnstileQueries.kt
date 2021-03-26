package infra.queries

import java.time.LocalDateTime

data class CheckMembershipQuery(val memberId: Long, val now: LocalDateTime) : Query<Boolean>