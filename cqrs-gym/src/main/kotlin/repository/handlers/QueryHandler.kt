package repository.handlers

import infra.queries.Query

interface QueryHandler<T, S> where T : Query<S> {
    fun execute(query: T): S
}