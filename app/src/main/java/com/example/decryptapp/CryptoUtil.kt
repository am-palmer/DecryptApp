package com.example.decryptapp

import android.content.Context
import android.util.Log

class CryptoUtil {

    private final val tag = "CryptoUtil"

    // todo: implement index of coincidence in the interface -> in the caesar portion, just so it is actually used somewhere.
    /**
     * Calculates the I.C. as a double for the given plain text string of English words (should contain no punctuation). Group size is 2 by default.
     * Characters are sorted into a hashMap (works well because of the limited range of possible values in 0..25
     * Then we iterate over the keys in the map and perform the I.O.C summation.
     */
    fun indexOfCoincidence(str: String, groupSize: Int? = 2): Double {
        // Todo: convert uppercase to lowercase
        val strFiltered = str.filter { !it.isWhitespace() } // Removes all whitespace
        var result = 0.0
        val k = groupSize ?: 2 // Pairs by default
        val n = strFiltered.length
        Log.d(tag, "indexOfCoincidence() String length: $n")
        val maxRepeats = (((n * n) - n) / k)

        // Increment values in our hash-map every time we encounter a given letter in the input
        var map = HashMap<Char, Int>()
        for (char in strFiltered) {
            map[char] = if (map[char] == null) {
                1 // If we're encountering a letter for the first time, value will be 1
            } else {
                map[char]!! + 1 // Otherwise, we increment the current value
            }

        }
        Log.d(tag, "indexOfCoincidence(): Character count map of input $map")

        // Summation over the hashMap
        var summation = 0
        for (entry in map.entries) {
            if (entry.value > 1) { // Check to see if a letter appeared at least twice in the text, (if not, we can ignore it as there will be zero pairs)
                summation += ((entry.value * (entry.value - 1)) / k) // I.O.C summation
            }
        }
        result = summation.toDouble() / maxRepeats.toDouble()
        Log.d(tag, "indexOfCoincidence() result: $result")
        return result
    }

    /**
     * Check how many of the words in the input string are in the top 10,000 english words.
     * Note: Expensive for large input, not recommended for big input strings.
     * (Note context is an Android specific object, it's not part of the logic of the function)
     */
    fun countEnglishWords(str: String, c: Context): Int {
        val englishDictionary = c.assets.open("10kEng.txt") // Get our dictionary
        val reader = englishDictionary.bufferedReader()
        val dictionary = reader.readLines()
        var count = 0
        val words = str.split(" ") // Split string into individual words based on spaces
        // TODO Make a hashmap, or some other kind of hashing function it will be much faster
        for (word in words) {
            for (entry in dictionary) {
                if (word == entry && word.length > 2) { // Ignore small words like 'it', 'a' as these can appear quite often even in random input
                    count++
                }
            }
        }
        reader.close()
        englishDictionary.close()
        return count
    }


}