package com.example.decryptapp

import android.content.Context
import android.util.Log

class CryptoUtil {

    private final val icTAG = "indexOfCoincidence"

    //val manager = assets
    //private lateinit var englishDictionary: InputStream

    // todo: implement this in the interface -> in the rsa portion, just so it is actually used.
    // Calculates the I.C. as a Double for the given str. Groupsize is 2 by default
    fun indexOfCoincidence(str: String, groupSize: Int? = 2): Double {
        val strFiltered = str.filter { !it.isWhitespace() }
        var result = 0.0
        val k = groupSize ?: 2 // pairs by default
        val n = strFiltered.length
        Log.d(icTAG, "String length: $n")
        val maxRepeats = (((n * n) - n) / k)

        // Increment values in our hash-map every time we encounter a given letter in the input
        var map = HashMap<Char, Int>()
        for (char in strFiltered) {
            map[char] = if (map[char] == null) {
                1
            } else {
                map[char]!! + 1
            }

        }
        Log.d(icTAG, "Character count map of input: $map")
        var totalRepeats = 0
        for (entry in map.entries) {
            if (entry.value > 1) {
                totalRepeats += ((entry.value * (entry.value - 1)) / k)
            }
        }
        //Log.d("I.O.C", "Max repeats: $maxRepeats")
        //Log.d("I.O.C", "Total repeats: $totalRepeats")
        result = totalRepeats.toDouble() / maxRepeats.toDouble()
        Log.d("I.O.C", "I.O.C. result: $result")
        return result
    }

    // Check how many of the words in the input string are in the top 10,000 english words
    // Note: extremely expensive for large input, don't use it with big input strings
    fun countEnglishWords(str: String, c: Context): Int {
        // reader
        val englishDictionary = c.assets.open("10kEng.txt") // init dictionary
        val reader = englishDictionary.bufferedReader()
        val dictionary = reader.readLines()
        // todo: optimize, very expensive
        var count = 0
        val words = str.split(" ")
        for (word in words) {
            for (entry in dictionary) {
                if (word == entry && word.length > 2) {
                    count++
                }
            }
        }
        reader.close()
        englishDictionary.close()
        return count
    }


}