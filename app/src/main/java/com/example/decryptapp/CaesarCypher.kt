package com.example.decryptapp

import android.content.Context
import android.util.Log

class CaesarCypher {
    //todo: coroutines
    // todo: negative key? ie shifting to the left

    private final val TAG = "CasarCypher"

    fun decrypt(
        str: String,
        key: Int?,
        smart: Boolean? = false,
        c: Context
    ): Pair<ArrayList<String>, ArrayList<Int>> {

        // note the I.C of english text: ~0.066
        // only 25 possible options
        var result = Pair<ArrayList<String>, ArrayList<Int>>(ArrayList(), ArrayList())
        // case 1: if given key, compute directly
        Log.d(TAG, "Value of a is " + 'a'.toInt())
        Log.d(TAG, "Value of b is " + 'b'.toInt())

        if (key != null) {
            Log.d(TAG, "Key provided ($key)")
            //(decryptCaesarKey(str, key), indexOfCoincidence(str, 2))
            result.first.add(decryptUsingKey(str, key))
            result.second.add(key)
            //result.second.add(indexOfCoincidence(str, 2))
        }

        if (key == null) {
            for (i in 0..26) {
                result.first.add(decryptUsingKey(str, i))
                result.second.add(i)
            }
        }
        Log.d(TAG, "Result pairs: ")
        for (i in 0..26) {
            Log.d(TAG, "${result.first[i]} (${result.second[i]} as key)")
        }

        // todo: load in 10k from assets using AssetManager, and check each result string, and try to figure out which string is the most likely decrypted solution
        if (smart == true) {
            val util = CryptoUtil()
            Log.d(
                TAG,
                "Determining how many words in each output string are in English"
            )
            for (str in result.first) {
                val count = util.countEnglishWords(str, c)
                Log.d(TAG, "$count common English words in $str")
            }
        }

        return result
    }

    private fun decryptUsingKey(str: String, key: Int): String {
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

}