package com.kotlin.study.blog.repository

import com.kotlin.study.blog.entity.WordCount
import org.springframework.data.repository.CrudRepository

interface WordRepository : CrudRepository<WordCount, String>
