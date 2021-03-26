package infra.queries

import model.Statistics

data class GetStatisticsQuery(val memberId: Long) : Query<Statistics>