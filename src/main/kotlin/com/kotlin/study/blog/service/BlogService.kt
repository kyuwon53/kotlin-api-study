package com.kotlin.study.blog.service

import com.kotlin.study.blog.dto.BlogDto
import com.kotlin.study.core.exception.InvalidInputException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class BlogService {
    @Value("\${REST_API_KEY}")
    lateinit var restApiKey: String
    fun searchKakao(blogDto: BlogDto): String? {
        val messageList = mutableListOf<ExceptionMessage>()

        if (blogDto.query.trim().isEmpty()) {
            messageList.add(ExceptionMessage.EMPTY_QUERY)
        }

        if (blogDto.sort.trim() !in arrayOf("accuracy", "recency")) {
            messageList.add(ExceptionMessage.NOT_IN_SORT)
        }

        when {
            blogDto.page < 1 -> messageList.add(ExceptionMessage.LESS_THAN_MIN)
            blogDto.page > 50 -> messageList.add(ExceptionMessage.MORE_THAN_MAX)
        }

        if (messageList.isNotEmpty()) {
            val messsage = messageList.joinToString { it.message }
            throw InvalidInputException(messsage)
        }

        val webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com")
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build()

        val response = webClient
            .get()
            .uri {
                it.path("/v2/search/blog")
                    .queryParam("query", blogDto.query)
                    .queryParam("sort", blogDto.sort)
                    .queryParam("page", blogDto.page)
                    .queryParam("size", blogDto.size)
                    .build()
            }
            .header("Authorization", "KakaoAK $restApiKey")
            .retrieve()
            .bodyToMono<String>()

        val result = response.block()

        return result
    }
}

private enum class ExceptionMessage(val message: String) {
    EMPTY_QUERY("query parameter required"),
    NOT_IN_SORT("sort parameter one of accuracy and recency"),
    LESS_THAN_MIN("page is less than min"),
    MORE_THAN_MAX("page is more than max")
}
