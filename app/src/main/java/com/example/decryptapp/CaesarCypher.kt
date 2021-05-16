package com.example.decryptapp

import android.content.Context
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class CaesarCypher {
    //todo: coroutines
    // todo: negative key? ie shifting to the left

    private val tag = "Caesar"

    // todo: encrypt()
    // Encrypts the
    fun encrypt(str: String, key: Int) {

    }

    // Returns the decrypted string for a given key.
    fun decrypt(str: String, key: Int): String {
        //Log.d("decryptCaesarKey", "started with key: ($key)")
        var output = ""
        for (char in str) {
            if (char.isLetter()) {
                var newValue = (((char.toInt() + 26 - 96) - key) % 26)

                if (newValue == 0) {
                    newValue = 'z'.toInt() - 96
                }
                val newChar = (newValue + 96).toChar()
                output += newChar
                //Log.d("decryptCaesar", "$char is shifted -$key spaces and becomes $newChar")
            } else {
                output += " "
            }

        }
        return output
    }

    /**
     * (Attempts to) decrypt the given input String (str) using the caesar cypher.
     *
     * Returns an ArrayList of CaesarResult objects, each containing the output string, the key for that string, and a nullable wordCount value
     * If smart=true, the decrypted strings are sorted by the number of English words they contain
     * (using a dictionary of the 10,000 most common words), and this number is recorded in wordCount.
     * Generally, this should identify the solving key (given the input is in English), or most likely candidates.
     */
    fun bruteForce(
        str: String,
        smart: Boolean? = false,
        c: Context
    ): ArrayList<CaesarResult> {

        // note the I.C of english text: ~0.066
        // only 25 possible options
        //var result = Pair<ArrayList<String>, ArrayList<Int>>(ArrayList(), ArrayList())
        //var result = ArrayList<Triple<String, Int, Int>>()
        var result = ArrayList<CaesarResult>()
        // case 1: if given key, compute directly
//
//        if (key != null) {
//            Log.d("$tag.decrypt", "Key provided ($key)")
//            //(decryptCaesarKey(str, key), indexOfCoincidence(str, 2))
//            result.add(CaesarResult(decryptWithKey(str, key), key, null))
//        }

        // Try all possible key values and add them to the result ArrayList
//        if (key == null) {
        // todo: 0 == 26 here? can we just check 0..25?
        for (i in 0..26) {
            // Call decryptUsingKey() for each possible value key [0..26]
            //result.first.add(decryptWithKey(str, i))
            result.add(CaesarResult(decrypt(str, i), i, null))
            //result.second.add(i)
        }
//        }
        Log.d("$tag.bruteForce", "Results: ")
        for (x in result) {
            //Log.d(tag, "${result.first[i]} (${result.second[i]} as key)")
            Log.d("$tag.decrypt", "Result for key=${x.key}: ${x.str}")
        }

        // Sort the resulting strings for each key by the number of English words they contain
        if (smart == true) {
            val util = CryptoUtil()
            Log.d(
                "$tag.bruteForce",
                "Determining how many words in each output string are in English"
            )
            for (x in result) {
                // We use the utility function to count the number of english words in each decrypted string
                x.wordCount = util.countEnglishWords(x.str, c)
                Log.d("$tag.bruteForce", "${x.wordCount} English words recognized in [${x.str}]")
            }
            // Sort the result objects by wordCount
            result.sortWith { o1, o2 -> compareValues(o1.wordCount, o2.wordCount) }
            // todo test
            Log.d("$tag.bruteForce", "Sorted list:")
            for (x in result) {
                Log.d("$tag.bruteForce", "key=${x.key}, wordCount=${x.wordCount}: [${x.str}]")
            }
        }

        return result
    }

    class CaesarResult(var str: String, var key: Int, var wordCount: Int?)

}