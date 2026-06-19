package com.example.nmedia.exception

import retrofit2.Response

class HttpException(
    val response: Response<*>,
    message: String = "HTTP error ${response.code()}: ${response.message()}"
) : Exception(message)

class NetworkException(message: String) : Exception(message)

