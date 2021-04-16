package com.example.decryptapp

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

class MainActivity : AppCompatActivity() {

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

        val lordeLyrics =
            "my friends and i weve cracked the code we count our dollars on the train to the party and everyone who knows us knows that were fine with this we didnt come from money z"
        //val lordeLyricsFiltered = lordeLyrics.filter { !it.isWhitespace() }
        val test = "abracadabra"

        val caesarTest = "aol xbpjr mve qbtwlk vcly aol shgf kvn" // key is 7
        val caesarTest2 = "Lwyg iu xkt sepd bera zkvaj hemqkn fqco" // key is 22
        //println("(Creation finished)")
        //println("Running indexOfCoincidence()")
        //indexOfCoincidence(lordeLyrics, 2)
        decryptCaesar(caesarTest2, 22)
    }

    fun indexOfCoincidence(str: String, groupSize: Int?): Double {
        val strFiltered = str.filter { !it.isWhitespace() }
        var result = 0.0
        val a = 26 // for english
        val k = groupSize ?: 2 // pairs by default
        val n = strFiltered.length
        Log.d("I.O.C", "String length: $n")
        val maxRepeats = (((n * n) - n) / k)

        // Increment values in our hash-map every time we encounter a given letter in the input
        var map = HashMap<Char, Int>()
        for (char in strFiltered) {
            //map[char] = map.getOrDefault(char, 0) + 1
            map[char] = if (map.get(char) == null) {
                1
            } else {
                map.get(char)!! + 1
            }

        }
        Log.d("I.O.C", map.toString())
        var totalRepeats = 0
        for (entry in map.entries) {
            if (entry.value > 1) {
                totalRepeats += ((entry.value * (entry.value - 1)) / k)
            }
        }
        Log.d("I.O.C", "Max repeats: $maxRepeats")
        Log.d("I.O.C", "Total repeats: $totalRepeats")
        result = totalRepeats.toDouble() / maxRepeats.toDouble()
        Log.d("I.O.C", "I.O.C. result: $result")
        return result
    }

    fun decryptCaesar(str: String, key: Int?): ArrayList<String> {
        // note the I.C of english text: ~0.066
        // only 25 possible options
        var result = ArrayList<String>()
        // case 1: if given key, compute directly
        Log.d("decryptCaesar", "Value of a is " + 'a'.toInt())
        Log.d("decryptCaesar", "Value of b is " + 'b'.toInt())

        if (key != null) {
            Log.d("decryptCaesar", "Key provided ($key)")
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
            Log.d("decryptCaesar", "Output of caesar cypher: $output")
            result.add(output)
        }

        if (key == null){
            // todo case 2: if no key, compute for all keys, then order by I.O.C closest to 0.066
        }

        return result

    }

}