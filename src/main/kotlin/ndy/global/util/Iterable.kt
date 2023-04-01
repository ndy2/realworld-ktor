package ndy.global.util

fun <A, B, C> List<Triple<A, B, C>>.unzip(): Triple<List<A>, List<B>, List<C>> {
    val expectedSize = this.size
    val listA = ArrayList<A>(expectedSize)
    val listB = ArrayList<B>(expectedSize)
    val listC = ArrayList<C>(expectedSize)
    for (triple in this) {
        listA.add(triple.first)
        listB.add(triple.second)
        listC.add(triple.third)
    }
    return Triple(listA, listB, listC)
}
