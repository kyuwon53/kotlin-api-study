package com.kotlin.study.kotlinfp

class Chapter3

sealed class List<out A> {
    companion object {
        fun <A> empty(): List<A> = Nil
        fun <A> of(vararg elements: A): List<A> {
            return if (elements.isEmpty()) {
                Nil
            } else {
                val tail = elements.sliceArray(1 until elements.size)
                Cons(elements[0], of(*tail))
            }
        }
    }
}

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

// 3.8
fun <A, B> foldRight(list: List<A>, init: B, f: (A, B) -> B): B =
    when (list) {
        is Nil -> init
        is Cons -> f(list.head, foldRight(list.tail, init, f))
    }

fun <A> length(list: List<A>): Int =
    foldRight(list, 0) { _, length -> 1 + length }

// 3.9
tailrec fun <A, RETURN> foldLeft(list: List<A>, init: RETURN, f: (RETURN, A) -> RETURN): RETURN =
    when (list) {
        is Nil -> init
        is Cons -> foldLeft(list.tail, f(init, list.head), f)
    }

// 3.10

fun sum(list: List<Int>): Int = foldLeft(list, 0) { x, y -> x + y }
fun product(list: List<Double>): Double = foldLeft(list, 1.0) { x, y -> x * y }
fun <A> lengthFoldLeft(list: List<A>): Int = foldLeft(list, 0) { curr, _ -> curr + 1 }

// 3.11
fun <A> reverse(list: List<A>): List<A> = foldLeft(list, List.empty()) { acc, curr -> Cons(curr, acc) }

// 3.12

fun main() {
    val list: List<Int> = List.of(1, 2, 3, 4, 5, 6)
    print(reverse(list))
}
