package com.dedztbh.nlplearn.data

/**
 * Created by DEDZTBH on 2019-06-13.
 * Project com.dedztbh.nlplearn
 */

typealias PhraseStats = MutableSet<DoubleWord>

data class DoubleWord(
    val first: String,
    val second: String,
    var freq: Int = 1
) {
    var MI = 0.0
    var LE = 0.0
    var RE = 0.0
    fun inc() = freq++
    override fun toString(): String {
        return "$first $second tf=$freq MI=$MI LE=$LE RE=$RE\n"
    }
}

fun PhraseStats.totalPhraseCount(): Int {
    var cnt = 0
    forEach { cnt += it.freq }
    return cnt
}