package com.sofascore.scoreandroidacademy.data.repository

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>() {
        override fun toString() = "[Success: $data]"
    }
    // Optional data allows to expose data stub just for loading state.
    data class Loading<T>(val data: T? = null) : Resource<T>() {
        override fun toString() = "[Loading: $data]"
    }
    data class Failure<T>(val error: Throwable) : Resource<T>() {
        override fun toString() = "[Failure: $error]"
    }

    fun unwrap(): T? =
        when (this) {
            is Loading -> data
            is Success -> data
            is Failure -> null
        }

    inline fun unwrapOr(default: T): T = unwrap() ?: default

    inline fun unwrapOr(crossinline makeDefault: () -> T): T = unwrap() ?: makeDefault()

    inline fun <Y> map(crossinline f: (T) -> Y): Resource<Y> = try {
        when (this) {
            is Loading -> Loading(data?.run(f))
            is Success -> Success(data.run(f))
            is Failure -> Failure(error)
        }
    } catch (e: Throwable) {
        Failure(e)
    }

    inline fun onSuccess(handle: (T) -> Unit): Resource<T> {
        if (this is Success) {
            handle(data)
        }
        return this
    }

    inline fun onLoading(handle: (T?) -> Unit): Resource<T> {
        if (this is Loading) {
            handle(data)
        }
        return this
    }

    inline fun onFailure(handle: (Throwable) -> Unit): Resource<T> {
        if (this is Failure) {
            handle(error)
        }
        return this
    }
}