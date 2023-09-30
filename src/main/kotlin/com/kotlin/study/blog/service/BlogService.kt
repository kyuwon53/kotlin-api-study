package com.kotlin.study.blog.service

import com.kotlin.study.blog.dto.BlogDto
import com.kotlin.study.blog.entity.WordCount
import com.kotlin.study.blog.repository.WordRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class BlogService(
    val wordRepository: WordRepository
) {
    @Value("\${REST_API_KEY}")
    lateinit var restApiKey: String
    fun searchKakao(blogDto: BlogDto): String? {
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

        val lowQuery: String = blogDto.query.lowercase()
        val word: WordCount = wordRepository.findByIdOrNull(lowQuery)?.let { wordCount -> WordCount(lowQuery) }
            ?: throw EntityNotFoundException("Not found Word")

        word.cnt++

        wordRepository.save(word)

        return result
    }

    fun searchWordRank(): List<WordCount> = wordRepository.findTop10ByOrderByCntDesc()
}
