package com.kotlin.study.core.response

data class ErrorResponse(
    val message: String,
    val errorType: String = "Invalid Argument"
)
