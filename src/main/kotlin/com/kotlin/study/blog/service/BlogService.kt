package com.kotlin.study.blog.service

import com.kotlin.study.blog.dto.BlogDto
import org.springframework.stereotype.Service

@Service
class BlogService {
    fun searchKakao(blogDto: BlogDto): String? {
        return "searchKakao";
    }
}
