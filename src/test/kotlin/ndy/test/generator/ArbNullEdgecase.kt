package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

abstract class ArbNullEdgecase<out A>(
    private val sample: (RandomSource) -> Sample<A>
) : Arb<A>() {
    final override fun edgecase(rs: RandomSource) = null

    final override fun sample(rs: RandomSource) = sample.invoke(rs)
}

