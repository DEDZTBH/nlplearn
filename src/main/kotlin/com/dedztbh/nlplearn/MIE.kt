package com.dedztbh.nlplearn

import com.dedztbh.nlplearn.StatMath.LE
import com.dedztbh.nlplearn.StatMath.MI
import com.dedztbh.nlplearn.StatMath.RE
import com.dedztbh.nlplearn.data.DoubleWord
import com.dedztbh.nlplearn.data.PhraseStats
import com.dedztbh.nlplearn.data.totalPhraseCount
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.corpus.tag.Nature
import com.hankcs.hanlp.mining.phrase.MutualInformationEntropyPhraseExtractor
import com.hankcs.hanlp.seg.common.Term

/**
 * Created by DEDZTBH on 2019-06-13.
 * Project com.dedztbh.nlplearn
 */

fun main() {
    MIE()
}

class MIE {
    val text = listOf(javaClass.getResource("text.txt").readText())
    val stopNatures = setOf(Nature.uj, Nature.w)
    val stopWords = javaClass.getResource("stop.txt").readText().split("\n")
    val breakWords = setOf("。", "；", "\n")

    init {
        run {
            println("例子: ${MutualInformationEntropyPhraseExtractor.extract(text.first(), 5)}")

            var processedList = text
            for (w in breakWords) {
                val newList = mutableListOf<String>()
                processedList.forEach {
                    newList.addAll(it.split(w).filter { it.trim() != "" })
                }
                processedList = newList
            }

            val wordsStat = processedList.map {
                HanLP.segment(it).filter {
                    !stopNatures.contains(it.nature) && !stopWords.contains(it.word)
                }
            }.let {
                //                println(it)
                countDouble(it)
            }

//            println(wordsStat.first)

            val totalPhraseCount = wordsStat.first.totalPhraseCount()
            wordsStat.first.forEach {
                it.MI = MI(it, wordsStat.second, totalPhraseCount)
                it.LE = LE(listOf(it.first, it.second), wordsStat.second)
                it.RE = RE(listOf(it.first, it.second), wordsStat.second)
            }
            wordsStat.first.sortedByDescending {
                it.MI + it.LE + it.RE
            }.subList(0, 5)
        }.let {
            println(it)
            Unit
        }
    }

    fun countDouble(segmentedSentences: List<List<Term>>): Pair<PhraseStats, MutableList<String>> {
        val result = mutableSetOf<DoubleWord>()
        val raw = mutableListOf<String>()
        segmentedSentences.forEach { sentence ->
            for (i in 0 until sentence.size - 1) {
                result.filter { it.first == sentence[i].word && it.second == sentence[i + 1].word }
                    .apply {
                        when (size) {
                            0 -> result.add(DoubleWord(sentence[i].word, sentence[i + 1].word))
                            1 -> first().inc()
                            else -> {
                                throw IllegalArgumentException()
                            }
                        }
                    }
            }
            raw.addAll(sentence.map { it.word })
        }
        return Pair(result, raw)
    }


}