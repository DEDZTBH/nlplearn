import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.corpus.tag.Nature
import com.hankcs.hanlp.mining.phrase.MutualInformationEntropyPhraseExtractor
import com.hankcs.hanlp.seg.common.Term

/**
 * Created by DEDZTBH on 2019-06-13.
 * Project nlplearn
 */

val text = """
算法工程师
算法（Algorithm）是一系列解决问题的清晰指令，也就是说，能够对一定规范的输入，在有限时间内获得所要求的输出。如果一个算法有缺陷，或不适合于某个问题，执行这个算法将不会解决这个问题。不同的算法可能用不同的时间、空间或效率来完成同样的任务。一个算法的优劣可以用空间复杂度与时间复杂度来衡量。算法工程师就是利用算法处理事物的人。
1职位简介
算法工程师是一个非常高端的职位；
专业要求：计算机、电子、通信、数学等相关专业；
学历要求：本科及其以上的学历，大多数是硕士学历及其以上；
语言要求：英语要求是熟练，基本上能阅读国外专业书刊；
必须掌握计算机相关知识，熟练使用仿真工具MATLAB等，必须会一门编程语言。
2研究方向
视频算法工程师、图像处理算法工程师、音频算法工程师 通信基带算法工程师
3目前国内外状况
目前国内从事算法研究的工程师不少，但是高级算法工程师却很少，是一个非常紧缺的专业工程师。算法工程师根据研究领域来分主要有音频/视频算法处理、图像技术方面的二维信息算法处理和通信物理层、雷达信号处理、生物医学信号处理等领域的一维信息算法处理。
在计算机音视频和图形图像技术等二维信息算法处理方面目前比较先进的视频处理算法：机器视觉成为此类算法研究的核心；另外还有2D转3D算法(2D-to-3D conversion)，去隔行算法(de-interlacing)，运动估计运动补偿算法(Motion estimation/Motion Compensation)，去噪算法(Noise Reduction)，缩放算法(scaling)，锐化处理算法(Sharpness)，超分辨率算法(Super Resolution),手势识别(gesture recognition),人脸识别(face recognition)。
在通信物理层等一维信息领域目前常用的算法：无线领域的RRM、RTT，传送领域的调制解调、信道均衡、信号检测、网络优化、信号分解等。
另外数据挖掘、互联网搜索算法也成为当今的热门方向。
算法工程师逐渐往人工智能方向发展。
""".trimIndent().let {
    listOf(it)
}

val stopWords = setOf(Nature.uj, Nature.w)
val breakWords = setOf("。", "；", "\n")

fun main() = run {
    println("例子: ${MutualInformationEntropyPhraseExtractor.extract(text.first(), 5)}")

    var processedList = mutableListOf<String>()
    for (w in breakWords) {
        val newList = processedList
        text.forEach {
            newList.addAll(it.split(w).filter { it.trim() != "" })
        }
        processedList = newList
    }

    val wordsStat = processedList.map {
        HanLP.newSegment().seg(it).filter {
            !stopWords.contains(it.nature)
        }
    }.let {
        //        println(it)
        countDouble(it)
    }

    wordsStat.second

}.let {
    println(it)
    Unit
}

fun countDouble(segmentedSentences: List<List<Term>>): Pair<MutableSet<DoubleWord>, MutableList<String>> {
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

data class DoubleWord(
    val first: String,
    val second: String,
    var freq: Int = 1
) {
    fun inc() = freq++

    override fun toString(): String {
        return "$first->$second tf=$freq"
    }
}

fun P(word: String, raw: List<String>): Double {
    return raw.count { it == word }.toDouble() / raw.size
}

fun P(words: List<String>, raw: List<String>, phraseCnt: Int): Double {
    var successCnt = 0
    for (i in 0 until raw.size - words.size + 1) {
        if (raw.subList(i, i + words.size) == words) {
            successCnt++
        }
    }
    return successCnt.toDouble() / phraseCnt
}

fun MI(x: String, y: String, raw: List<String>, doubleStats: Set<DoubleWord>) {
    
}

fun Set<DoubleWord>.totalPhraseCount(): Int {
    var cnt = 0
    forEach { cnt += it.freq }
    return cnt
}