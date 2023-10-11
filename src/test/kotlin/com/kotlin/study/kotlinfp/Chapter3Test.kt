package com.kotlin.study.kotlinfp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Chapter3Test
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
class Chapter3_11 {
    @Test
    fun main() {
        val list: List<Int> = List.of(1, 2, 3, 4, 5, 6)
        assertThat(reverse(list)).isEqualTo(List.of(6, 5, 4, 3, 2, 1))
    }
}
// 3.12


// 3.13
fun <A> appendFoldRight(list: List<A>, element: A): List<A> =
    foldRight(list, List.of(element)) { curr, acc -> Cons(curr, acc) }

fun <A> append(list: List<A>, element: List<A>): List<A> =
    foldRight(list, element) { curr, acc -> Cons(curr, acc) }

class Chapter3_13 {
    @Test
    fun main() {
        val list1: List<Int> = List.of(1, 2, 3, 4)
        val list2: List<Int> = List.of(5, 6)
        assertThat(append(list1, list2)).isEqualTo(List.of(1, 2, 3, 4, 5, 6))
    }
}

// 3.14
fun <A> concat(target: List<List<A>>): List<A> =
    foldRight(target, List.empty()) { curr, acc -> append(curr, acc) }

class Chapter3_14 {
    @Test
    fun main() {
        val lists: List<List<Int>> = List.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6),
            List.of(7, 8, 9)
        )
        assertThat(concat(lists)).isEqualTo(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9))

        val lists2: List<List<Int>> = List.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6)
        )
        assertThat(concat(lists2)).isEqualTo(List.of(1, 2, 3, 4, 5, 6))

        val lists3: List<List<Int>> = List.of(
            List.of(1, 2, 3)
        )
        assertThat(concat(lists3)).isEqualTo(List.of(1, 2, 3))

        val listsEmpty: List<List<Int>> = List.of(
            List.empty()
        )
        assertThat(concat(listsEmpty)).isEqualTo(Nil)
    }
}

// 3.15
fun increaseOne(target: List<Int>): List<Int> = foldRight(target, List.empty()) { curr, acc -> Cons(curr + 1, acc) }
fun increase(target: List<Int>, num: Int): List<Int> =
    foldRight(target, List.empty()) { curr, acc -> Cons(curr + num, acc) }

class Chapter3_15 {
    @Test
    fun test() {
        val listWhenIncreaseOne: List<Int> = List.of(1, 2, 3, 4, 5)
        assertThat(increaseOne(listWhenIncreaseOne)).isEqualTo(List.of(2, 3, 4, 5, 6))

        val list: List<Int> = List.of(1, 2, 3, 4, 5)
        assertThat(increase(list, 3)).isEqualTo(List.of(4, 5, 6, 7, 8))
    }
}

// 3.16
fun parseString(target: List<Double>): List<String> =
    foldRight(target, List.empty()) { curr, acc -> Cons(curr.toString(), acc) }

class Chapter3_16 {
    @Test
    fun test() {
        val list: List<Double> = List.of(0.0, 1.0, 2.0, 3.0)
        assertThat(parseString(list)).isEqualTo(List.of("0.0", "1.0", "2.0", "3.0"))
    }
}

// 3.17
fun <A, RETURN> map(target: List<A>, transform: (A) -> RETURN): List<RETURN> =
    foldRight(target, List.empty()) { curr, acc -> Cons(transform(curr), acc) }

class Chapter3_17 {
    @Test
    fun test() {
        val list: List<Double> = List.of(0.0, 1.0, 2.0, 3.0)

        val transform: (Double) -> String = { it.toString() }

        assertThat(map(list, transform)).isEqualTo(List.of("0.0", "1.0", "2.0", "3.0"))
    }
}

// 3.18
fun <A> filter(target: List<A>, predicate: (A) -> Boolean): List<A> =
    foldRight(target, List.empty()) { curr, acc ->
        if (predicate(curr)) {
            Cons(curr, acc)
        } else {
            acc
        }
    }

class Chpater3_18 {
    @Test
    fun test() {
        val list: List<Int> = List.of(1, 2, 3, 4, 5)
        val predicate: (Int) -> Boolean = { it % 2 != 0 }
        assertThat(filter(list, predicate)).isEqualTo(List.of(1, 3, 5))
    }
}

// 3.19

fun <A, RETURN> flatMap(target: List<A>, mapper: (A) -> List<RETURN>): List<RETURN> =
    foldRight(target, List.empty()) { curr, acc -> foldRight(mapper(curr), acc) { curr, acc -> Cons(curr, acc) } }


class Chapter3_19 {
    @Test
    fun test() {
        val list: List<Int> = List.of(1, 2, 3)
        val mapper: (Int) -> List<Int> = { i -> List.of(i, i) }

        assertThat(flatMap(list, mapper)).isEqualTo(List.of(1, 1, 2, 2, 3, 3))
    }
}

// 3.20

fun <A> filterFlatMap(target: List<A>, predicate: (A) -> Boolean): List<A> =
    flatMap(target) { if (predicate(it)) List.of(it) else List.empty() }

class Chapter3_20 {
    @Test
    fun test() {
        val list: List<Int> = List.of(1, 2, 3, 4, 5)
        val predicate: (Int) -> Boolean = { it % 2 != 0 }
        assertThat(filterFlatMap(list, predicate)).isEqualTo(List.of(1, 3, 5))
    }
}

// 3.21
fun add(list1: List<Int>, list2: List<Int>): List<Int> =
    when (list1) {
        is Nil -> Nil
        is Cons -> when (list2) {
            is Nil -> Nil
            is Cons -> Cons(list1.head + list2.head, add(list1.tail, list2.tail))
        }
    }


class Chapter3_21 {
    @Test
    fun test() {
        val list1: List<Int> = List.of(1, 2, 3)
        val list2: List<Int> = List.of(4, 5, 6)
        val result: List<Int> = List.of(5, 7, 9)

        assertThat(add(list1, list2)).isEqualTo(result)
    }
}

// 3.22
fun <A> zipWith(list1: List<A>, list2: List<A>, sum: (A, A) -> A): List<A> =
    when (list1) {
        is Nil -> Nil
        is Cons -> when (list2) {
            is Nil -> Nil
            is Cons -> Cons(sum(list1.head, list2.head), zipWith(list1.tail, list2.tail, sum))
        }
    }

class Chapter3_22 {
    @Test
    fun test() {
        val list1: List<Int> = List.of(1, 2, 3)
        val list2: List<Int> = List.of(4, 5, 6)
        val result: List<Int> = List.of(5, 7, 9)

        assertThat(zipWith(list1, list2) { num1, num2 -> num1 + num2 }).isEqualTo(result)
    }
}

