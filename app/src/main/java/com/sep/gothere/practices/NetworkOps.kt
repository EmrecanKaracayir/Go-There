package com.sep.gothere.practices

import com.sep.gothere.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

inline fun <ResultType, ResponseType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> ResponseType,
    crossinline saveFetchResult: suspend (ResponseType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = { },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = channelFlow {
    val data = query().first()
    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }
        try {
            saveFetchResult(fetch())
            onFetchSuccess()
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            loading.cancel()
            query().collect { send(Resource.Error(t, it)) }
        }
    } else {
        query().collect { send(Resource.Success(it)) }
    }
}

suspend inline fun <ResponseType> networkOnlyImmediateResource(
    crossinline fetch: suspend () -> ResponseType,
    crossinline onFetchLoading: () -> Unit,
    crossinline onFetchSuccess: (ResponseType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit
): Resource<ResponseType> {
    onFetchLoading()
    return try {
        val data = fetch()
        onFetchSuccess(data)
        Resource.Success(data)
    } catch (t: Throwable) {
        onFetchFailed(t)
        Resource.Error(t)
    }
}