package com.kotlin.study.blog.dto

data class BlogDto(
    val query: String,
    val sort: String,
    val page: Int,
    val size: Int
)
