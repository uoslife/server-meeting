package com.program

import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

object MatchingExample {
    // this method generated randomly shuffled preference list for men and woman
    fun preflist(n: Int): List<Any> {
        val men = n + 10
        val woman = n + 20
        val menlist = IntStream.rangeClosed(11, men).boxed().collect(Collectors.toList())
        val womanlist = IntStream.rangeClosed(21, woman).boxed().collect(Collectors.toList())
        val menpref: MutableMap<Int, List<Int?>> = HashMap()
        val womanpref: MutableMap<Int, List<Int?>> = HashMap()
        for (i in 0 until n) {
            val womantemp: MutableList<Int?> = ArrayList()
            val mentemp: MutableList<Int?> = ArrayList()
            for (j in 0 until n) {
                womantemp.add(womanlist[j])
                mentemp.add(menlist[j])
            }
            // 선호도를 랜덤으로 섞음
            Collections.shuffle(womantemp)
            Collections.shuffle(mentemp)
            menpref[menlist[i]] = womantemp
            womanpref[womanlist[i]] = mentemp
        }
        // 남자, 여자 리스트 / 각 남자, 여자 선호도 반환
        return Arrays.asList(menlist, womanlist, menpref, womanpref)
    }

    // algorithm implementation
    fun galeShapleyAlgo(
        n: Int,
        menpref: Map<Int?, List<Int>>,
        womanpref: Map<Int?, List<Int?>>,
        menlist: List<Int>,
        womanlist: List<Int>
    ): Map<Int, Int> {

        // Initialization
        val menUnmatched: MutableList<Int> = ArrayList()
        val womanUnmatched: MutableList<Int> = ArrayList()
        val pair: MutableMap<Int, Int> = HashMap()
        val menIndex: MutableMap<Int, Int> = HashMap()
        for (i in 0 until n) {
            menIndex[menlist[i]] = 0
        }
        for (i in 0 until n) {
            menUnmatched.add(menlist[i])
            womanUnmatched.add(womanlist[i])
        }

        // start time
        val startTime = System.nanoTime()

        // algorithm starts
        while (!menUnmatched.isEmpty()) {
            val man = menUnmatched[0]
            val hisPref = menpref[man]!!
            var indexnum = menIndex[man]!!
            val woman = hisPref[indexnum]
            val herPref = womanpref[woman]!!
            if (womanUnmatched.contains(woman)) { // 선택한 여성이 매칭되지 않았으면 선택
                pair[man] = woman
                menUnmatched.removeAt(menUnmatched.indexOf(man))
                womanUnmatched.removeAt(womanUnmatched.indexOf(woman))
            } else if (
                pair.containsValue(woman) && hisPref.contains(woman)
            ) { // 이미 매칭되었는데 선호도가 높으면?
                // getting current partner
                var currentPartner = 0
                for (partner in pair.keys) {
                    if (pair[partner] == woman) { // 매칭되어있는 남성 찾기
                        currentPartner = partner
                    }
                }
                if (
                    herPref.indexOf(man) < herPref.indexOf(currentPartner)
                ) { // 여성 선호도에서 현재 남자가 이미 선정된 남성보다 높으면 매칭 바꾸기
                    pair.remove(currentPartner, woman)
                    menUnmatched.add(currentPartner) // 남자는 다시 unmatch
                    pair[man] = woman
                    menUnmatched.removeAt(menUnmatched.indexOf(man))
                }
            }
            indexnum++
            menIndex[man] = indexnum
        }
        // end time
        val endTime = System.nanoTime()
        val totalTime = (endTime - startTime) / 1000
        println("Time took to run algorithm $totalTime microseconds")
        return pair
    }

    // test for stable match
    fun testForStableMatch(
        pair: Map<Int, Int>,
        menpref: Map<Int?, List<Int>>,
        womanpref: Map<Int?, List<Int?>>
    ) {
        for ((man, woman) in pair) {
            val hisPref = menpref[man]!! // 남자가 선호 리스트
            for (i in hisPref.indices) {
                val tempwoman = hisPref[i]
                if (tempwoman == woman) { // 현재 매칭된 여성이라면 종료
                    break
                } else {
                    // getting current partner
                    var tempWomanPartner = 0
                    for (partner in pair.keys) {
                        if (pair[partner] == tempwoman) { // 매칭된 여성의 남성 파트너 구하기
                            tempWomanPartner = partner
                        }
                    }
                    val herPref = womanpref[tempwoman]!!
                    if (
                        herPref.indexOf(man) < herPref.indexOf(tempWomanPartner)
                    ) { // 매칭된 남성보다 현재 남성이 더 선호되면 unstable
                        println(
                            man.toString() +
                                " and " +
                                tempwoman +
                                " are unstable partner and prefer each other over their current partner"
                        )
                    }
                }
            }
            println("$man and $woman are stable match")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO Auto-generated method stub
        val n = 10
        // for (int i = 0; i < 5; i++) {
        // call preflist method
        // men list, woman list auto generated
        // men's preference list and woman's preference list also auto generated
        val generatedpref = preflist(n)
        val menlist = generatedpref[0] as List<Int>
        val womanlist = generatedpref[1] as List<Int>
        val menpref = generatedpref[2] as Map<Int?, List<Int>>
        val womanpref = generatedpref[3] as Map<Int?, List<Int?>>
        var finalPair: Map<Int, Int> = HashMap()
        println("men's preference list $menpref")
        println("woman's preference list $womanpref")

        // calling algorithm - returns matched pair
        finalPair = galeShapleyAlgo(n, menpref, womanpref, menlist, womanlist)
        // }
        println("Matched pair: $finalPair")

        // test for stable match
        testForStableMatch(finalPair, menpref, womanpref)
    }
}
