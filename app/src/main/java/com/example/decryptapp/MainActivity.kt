package com.example.decryptapp

import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import com.example.decryptapp.ui.main.SectionsPagerAdapter
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    // todo: move this out of here
    //val manager = assets
    //private lateinit var englishDictionary: InputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // todo: move all this out
        val lordeLyrics =
            "my friends and i weve cracked the code we count our dollars on the train to the party and everyone who knows us knows that were fine with this we didnt come from money z"
        //val lordeLyricsFiltered = lordeLyrics.filter { !it.isWhitespace() }
        val test = "abracadabra"
        val caesarTest = "aol xbpjr mve qbtwlk vcly aol shgf kvn" // key is 7
        val caesarTest2 = "Lwyg iu xkt sepd bera zkvaj hemqkn fqco" // key is 22


        //println("(Creation finished)")
        //println("Running indexOfCoincidence()")
        //indexOfCoincidence(lordeLyrics, 2)
        //decryptCaesar(caesarTest2, 22)
        decryptCaesar(caesarTest2, null, true)
    }

    // todo: move all this to its own class, one specifically for caesar, one for IOC calculations

    fun indexOfCoincidence(str: String, groupSize: Int?): Double {
        val strFiltered = str.filter { !it.isWhitespace() }
        var result = 0.0
        val k = groupSize ?: 2 // pairs by default
        val n = strFiltered.length
        Log.d("I.O.C", "String length: $n")
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
        Log.d("I.O.C", map.toString())
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

    //todo: coroutines
    private fun decryptCaesar(str: String, key: Int?, smart: Boolean? = false): Pair<ArrayList<String>, ArrayList<Int>> {
        // note the I.C of english text: ~0.066
        // only 25 possible options
        var result = Pair<ArrayList<String>, ArrayList<Int>>(ArrayList(), ArrayList())
        // case 1: if given key, compute directly
        Log.d("decryptCaesar", "Value of a is " + 'a'.toInt())
        Log.d("decryptCaesar", "Value of b is " + 'b'.toInt())

        if (key != null) {
            Log.d("decryptCaesar", "Key provided ($key)")
            //(decryptCaesarKey(str, key), indexOfCoincidence(str, 2))
            result.first.add(decryptCaesarKey(str, key))
            result.second.add(key)
            //result.second.add(indexOfCoincidence(str, 2))
        }

        if (key == null) {
            for (i in 0..26) {
                result.first.add(decryptCaesarKey(str, i))
                result.second.add(i)
            }
        }
        Log.d("decryptCaesar", "Result pairs: ")
        for (i in 0..26) {
            Log.d("decryptCaesar", "${result.first[i]} (${result.second[i]} as key)")
        }

        // todo: load in 10k from assets using AssetManager, and check each result string, and try to figure out which string is the most likely decrypted solution
        if (smart == true){
            Log.d("decryptCaesar", "Determining how many words in each output string are in English")
            for (str in result.first){
                val count = countEnglishWords(str)
                Log.d("decryptCaesar", "$count common English words in $str")
            }
        }

        return result
    }

    private fun decryptCaesarKey(str: String, key: Int): String {
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

    // Check how many of the words in the input string are in the top 10,000 english words
    // Note: extremely expensive for large input, don't use it with big input strings
    private fun countEnglishWords(str: String): Int {
        // reader
        val englishDictionary = assets.open("10k.txt") // init dictionary
        val reader = englishDictionary.bufferedReader()
        val dictionary = reader.readLines()
        // todo: optimize, very expensive
        var count = 0
        val words = str.split(" ")
        for (word in words){
            for (entry in dictionary){
                if (word == entry && word.length > 2){
                    count++
                }
            }
        }
        reader.close()
        englishDictionary.close()
        return count
    }

}