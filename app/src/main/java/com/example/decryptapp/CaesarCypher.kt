package com.example.decryptapp

import android.content.Context
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class CaesarCypher {
    //todo: coroutines, make this a coroutine so it doesn't eat the main thread
    // todo: negative key? ie shifting to the left. test if it works

    // todo: must support capitalized input! either directly or shift to lowercase first

    private val tag = "Caesar"

    // todo: test
    // Encrypts the string using the given key. Supports letters and spaces
    fun encrypt(str: String, key: Int): String {
        var cipher = ""
        for (char in str) {
            if (char.isLetter()) {
                var newValue = (((char.toLowerCase().toInt() + 26 - 96) + key) % 26)

                if (newValue == 0) {
                    newValue = 'z'.toInt() - 96
                }
                val newChar = (newValue + 96).toChar()
                cipher += newChar
            } else {
                cipher += " "
            }
        }
        Log.d("$tag.encrypt", "Result for $key: [$cipher]")
        return cipher
    }

    // Returns the decrypted string for a given key. Supports only letters and spaces
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
                output += char
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
        // todo: optimize further
        var result = ArrayList<CaesarResult>()

        // Try all key values and add them to the result ArrayList
        for (i in 0..26) {
            // Call decryptUsingKey() for each possible value key [0..26]
            result.add(CaesarResult(decrypt(str, i), i, null))
        }

        // Print to console
        Log.d("$tag.bruteForce", "Results: ")
        for (x in result) {
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
            result.sortWith { o1, o2 -> compareValues(o2.wordCount, o1.wordCount) }
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