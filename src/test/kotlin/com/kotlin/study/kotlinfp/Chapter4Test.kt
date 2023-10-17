package com.kotlin.study.kotlinfp

import com.kotlin.study.kotlinfp.FList.Cons
import com.kotlin.study.kotlinfp.FList.Nil
import com.kotlin.study.kotlinfp.Option.None
import com.kotlin.study.kotlinfp.Option.Some
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.pow

class Chapter4Test

sealed class Option<out VALUE : Any> {
    object None : Option<Nothing>()
    data class Some<out VALUE : Any> @PublishedApi internal constructor(@PublishedApi internal val get: VALUE) :
        Option<VALUE>()

    companion object {
        inline operator fun <VALUE : Any> invoke(value: VALUE): Option<VALUE> = Some(value)
        inline operator fun <VALUE : Any> invoke(): Option<VALUE> = None
    }
}

// 4.1
fun <VALUE : Any, OTHER : Any> Option<VALUE>.map(block: (VALUE) -> OTHER): Option<OTHER> =
    when (this) {
        is Some -> Some(block(get))
        is None -> this
    }

fun <VALUE : Any> Option<VALUE>.getOrElse(default: () -> VALUE): VALUE =
    if (this is Some) get else default()

fun <VALUE : Any, OTHER : Any> Option<VALUE>.flatMap(block: (VALUE) -> Option<OTHER>): Option<OTHER> =
    map(block).getOrElse { None }

fun <VALUE : Any> Option<VALUE>.orElse(block: () -> Option<VALUE>): Option<VALUE> =
    map { Option(it) }.getOrElse(block)

fun <VALUE : Any> Option<VALUE>.filter(block: (VALUE) -> Boolean): Option<VALUE> =
    flatMap { if (block(it)) Option(it) else None }

class optionTest() {
    @Test
    fun test() {
        val some = Option(3);
        val none = Option<Int>();

        assertThat(none.map { it + 1 }).isEqualTo(None)
        assertThat(some.map { it + 1 }).isEqualTo(Option(4))

        assertThat(none.getOrElse { "none" }).isEqualTo("none")
        assertThat(some.getOrElse { "none" }).isEqualTo(3)

        assertThat(some.flatMap { Option(it + 1) }).isEqualTo(Option(4))
        assertThat(none.flatMap { Option(it + 1) }).isEqualTo(None)

        assertThat(some.orElse { Option("none") }).isEqualTo(Option(3))
        assertThat(none.orElse { Option() }).isEqualTo(None)

        assertThat(some.filter { it == 3 }).isEqualTo(Option(3))
        assertThat(some.filter { it != 3 }).isEqualTo(None)
        assertThat(none.filter { it == 3 }).isEqualTo(None)
        assertThat(none.filter { it != 3 }).isEqualTo(None)
    }
}

// 4.2
class Employee(
    val name: String,
    val department: String,
    val manager: Option<String>
)

fun lookupByName(name: String): Option<Employee> = TODO()
fun timDepartment(): Option<String> =
    lookupByName("Tim").map { it.department }

fun FList<kotlin.Double>._sum(block: (Double, Double) -> Double): Number =
    when (this) {
        is Cons -> drop().fold(head) { acc, item -> block(acc, item) }
        is Nil -> 0
    }

fun <ITEM : Any> FList<ITEM>.isEmpty() =
    this.size == 0

fun FList<Double>.sum(): Double = _sum { acc, curr -> acc + curr } as Double

fun mean(list: FList<Double>): Option<Double> =
    if (list.isEmpty()) None
    else Some(list.sum() / list.size)

fun variance(data: FList<Double>): Option<Double> =
    mean(data).flatMap { meanValue ->
        mean(data.map { x ->
            (x - meanValue).pow(2)
        })
    }

class varianceTest {
    @Test
    fun test() {
        val data = FList(1.0, 2.0, 3.0, 4.0, 5.0)
        val empty = FList<Double>()

        assertThat(variance(data)).isEqualTo(Option(2.0))
        assertThat(variance(empty)).isEqualTo(None)
    }
}

// 4.3

fun <VALUE : Any, OTHER : Any, RETURN : Any> Option<VALUE>.combine(
    other: Option<OTHER>,
    block: (VALUE, OTHER) -> RETURN
): Option<RETURN> =
    flatMap { value -> other.map { block(value, it) } }

class combineTest {
    @Test
    fun test() {
        val doubleData = Option(1.8)
        val intData = Option(10)
        val result: Option<String> = doubleData.combine(intData) { x, y -> "${x * y}" }

        assertThat(result.getOrElse { "error" }).isEqualTo("18.0");
    }
}

// 4.4
fun <ITEM : Any> FList<Option<ITEM>>.sequence(): Option<FList<ITEM>> =
    foldRight(Option(FList())) { curr, acc ->
        curr.combine(acc) { head, tail -> Cons(head, tail) }
    }

class sequenceTest {
    @Test
    fun test() {
        val target = FList(Option(1), Option(2), Option(3))
        val target2 = FList(Option(1), Option(), Option(3))
        val empty = FList(Option<Int>())


        assertThat(target.sequence()).isEqualTo(Option(FList(1, 2, 3)))
        assertThat(target2.sequence()).isEqualTo(None)
        assertThat(empty.sequence()).isEqualTo(None)
    }
}

// 4.5
