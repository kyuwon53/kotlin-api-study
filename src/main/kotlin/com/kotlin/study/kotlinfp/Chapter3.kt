package com.kotlin.study.kotlinfp

class Chapter3

fun main() {
}

sealed class List<out A>
object Nil : List<Nothing>()
data class Cons<out A>(val head: A, val tail: List<A>) : List<A>()

// 3.1
fun <A> tail(list: List<A>): List<A> =
    when (list) {
        is Nil -> throw IllegalArgumentException("삭제할 원소가 존재하지 않습니다.")
        is Cons -> list.tail
    }

// 3.2
fun <A> setHead(list: List<A>, x: A): List<A> =
    when (list) {
        is Nil -> throw IllegalArgumentException("List가 Nil이므로 대치할 첫 원소가 존재하지 않습니다.")
        is Cons -> Cons(x, list.tail)
    }

// 3.3
fun <A> drop(list: List<A>, n: Int): List<A> =
    if (n == 0) list
    else when (list) {
        is Nil -> throw IllegalArgumentException("삭제할 원소가 존재하지 않습니다.")
        is Cons -> drop(list.tail, n - 1)
    }

// 3.4
fun <A> dropWhile(list: List<A>, function: (A) -> Boolean): List<A> =
    when (list) {
        is Nil -> list
        is Cons -> if (function(list.head)) {
            dropWhile(list.tail, function)
        } else {
            list
        }
    }

// 3.5
fun <A> init(list: List<A>): List<A> =
    when (list) {
        is Nil -> throw IllegalArgumentException("빈 리스트는 init 할 수 없습니다")
        is Cons -> if (list.tail == Nil) {
            Nil
        } else {
            Cons(list.head, init(list.tail))
        }
    }
