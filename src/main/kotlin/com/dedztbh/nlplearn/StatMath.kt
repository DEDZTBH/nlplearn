package com.dedztbh.nlplearn

import com.dedztbh.nlplearn.data.DoubleWord
import kotlin.math.log2

/**
 * Created by DEDZTBH on 2019-06-13.
 * Project com.dedztbh.nlplearn
 */
object StatMath {
    fun Psingle(word: String, raw: List<String>): Double {
        val wordCnt = raw.count { it == word }
        println("$word: $wordCnt/${raw.size}")
        return wordCnt.toDouble() / raw.size
    }

    fun Plist(phrase: DoubleWord, phraseCnt: Int): Double = phrase.freq.toDouble() / phraseCnt

    fun MI(phrase: DoubleWord, raw: List<String>, phraseCount: Int) =
        phrase.run {
            val pxy = Plist(this, phraseCount)
            val px = Psingle(first, raw)
            val py = Psingle(second, raw)
//        println("$first$second MI")
//        println(phraseCount)
//        println(pxy)
//        println(px)
//        println(py)
//        println(pxy / (px * py))
//        println(log2(pxy / (px * py)))

            pxy * log2(pxy / (px * py)) * phraseCount
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
                foundMap.incByKey(raw[i + words.size])
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