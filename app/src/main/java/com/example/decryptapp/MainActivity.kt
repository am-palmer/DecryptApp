package com.example.decryptapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.math.MathUtils
import com.example.decryptapp.ui.main.SectionsPagerAdapter
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    private val tag = "Main"

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
        val caesar = CaesarCypher();
        //caesar.bruteForce(caesarTest2, true, applicationContext)
//        caesar.encrypt(
//            "Pack my box with five dozen liquor jugs",
//            22
//        ) // Expected: lwyg iu xkt sepd bera zkvaj hemqkn fqco

        val rsa = RSA()
        //val result = rsa.extGCD(13, 11)
//        Log.d(
//            "Main",
//            "result for extGCD(13,11): triple ${result.first}, ${result.second}, ${result.third}"
//        )

        //Log.d("Main", "result of mod exp. with base=11, exp=13, mod=17: ${rsa.modExp(11, 13, 17)} (expected: 7)")
        //rsa.modExp(11,13,17)
        //rsa.modExp(11,20,17)
        //rsa.modExp(6,20,7)

        //rsa.modInverse(13, 17)
        //rsa.modInverse(11, 21)

        rsa.extGCD(13, 11) // Expected: gcd 1, MI 13 = -5, MI 11 = 6.

        // p = 11
        // q = 13
        // N = pq = 143
        // r (phi) = (p-1)(q-1) = 120
        // private key (e, N) = (7, 143)
        // public key (d, N) = (223, 143)
        val cypher = rsa.encrypt(7, 143, "hello world")
        val plaintext = rsa.decrypt(223, 143, cypher)

    }

}