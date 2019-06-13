package com.dedztbh.nlplearn

import kotlin.math.log2

/**
 * Created by DEDZTBH on 2019-06-13.
 * Project com.dedztbh.nlplearn
 */
object StatMath {
    fun Psingle(word: String, raw: List<String>): Double {
        return raw.count { it == word }.toDouble() / raw.size
    }

    fun Plist(words: List<String>, raw: List<String>, phraseCnt: Int): Double {
        var successCnt = 0
        for (i in 0 until raw.size - words.size + 1) {
            if (raw.subList(i, i + words.size) == words) {
                successCnt++
            }
        }
        return successCnt.toDouble() / phraseCnt
    }

    fun MI(x: String, y: String, raw: List<String>, phraseCount: Int): Double {
        val pxy = Plist(listOf(x, y), raw, phraseCount)
        val px = Psingle(x, raw)
        val py = Psingle(y, raw)
//        println(pxy)
//        println(px)
//        println(py)
        return log2(pxy / (px * py))
    }

    fun Pe(count: Int, totalCount: Int): Double = count.toDouble() / totalCount

    fun LE(words: List<String>, raw: List<String>): Double {
        val foundMap = mutableMapOf<String, Int>()
        var totalCnt = 0
        for (i in 1 until raw.size - words.size + 1) {
            if (raw.subList(i, i + words.size) == words) {
                foundMap.incByKey(raw[i - 1])
                totalCnt++
            }
        }
        var sum = 0.0
        foundMap.keys.forEach { k ->
            sum += Pe(foundMap[k]!!, totalCnt).let { P ->
                P * log2(P)
            }
        }
        return -sum
    }

    fun RE(words: List<String>, raw: List<String>): Double {
        val foundMap = mutableMapOf<String, Int>()
        var totalCnt = 0
        for (i in 0 until raw.size - words.size) {
            if (raw.subList(i, i + words.size) == words) {
                foundMap.incByKey(raw[i + 1])
                totalCnt++
            }
        }
        var sum = 0.0
        foundMap.keys.forEach { k ->
            sum += Pe(foundMap[k]!!, totalCnt).let { P ->
                P * log2(P)
            }
        }
        return -sum
    }
}


fun MutableMap<String, Int>.incByKey(key: String, amount: Int = 1) {
    if (!containsKey(key)) {
        this[key] = 0
    }
    this[key] = this[key]!! + amount
}