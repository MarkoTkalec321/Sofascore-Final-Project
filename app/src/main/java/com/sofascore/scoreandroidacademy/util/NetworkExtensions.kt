package com.sofascore.scoreandroidacademy.util

import com.sofascore.scoreandroidacademy.data.remote.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> safeResponse(func: suspend () -> T): Result<T> {
    return try {
        val result = func.invoke()
        if (result is Response<*> && result.isSuccessful.not()) {
            Result.Error(HttpException(result))
        } else {
            Result.Success(result)
        }
    } catch (e: Throwable) {
        Result.Error(e)
    }
}

suspend fun ResponseBody.toByteArray(): ByteArray = withContext(Dispatchers.IO) {
    this@toByteArray.byteStream().use { inputStream ->
        inputStream.readBytes()
    }
}