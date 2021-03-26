package infra.queries

import model.Member

data class GetMemberInfoQuery(val memberId: Long) : Query<Member>