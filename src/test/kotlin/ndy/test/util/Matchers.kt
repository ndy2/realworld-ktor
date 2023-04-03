package ndy.test.util

import io.kotest.matchers.shouldBe

/**
 * check update occurred if pair.first is not null
 */
infix fun <T, U : T> T.shouldBeUpdatedToIf(pair: Pair<U, U>) {
    if (pair.first == null) {
        this shouldBe pair.second
    } else {
        this shouldBe pair.first
    }
}

infix fun <A, B> A.isNotNullOr(that: B): Pair<A, B> = Pair(this, that)
