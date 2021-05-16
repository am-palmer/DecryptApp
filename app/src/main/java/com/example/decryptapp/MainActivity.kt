package com.example.decryptapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.decryptapp.ui.main.SectionsPagerAdapter
import java.math.BigInteger

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
        //caesar.decrypt(caesarTest2, null, true, applicationContext)

        val rsa = RSA()
        val result = rsa.extGCD(13, 11)
        Log.d(
            "Main",
            "result for extGCD(13,11): triple ${result.first}, ${result.second}, ${result.third}"
        )

        //Log.d("Main", "result of mod exp. with base=11, exp=13, mod=17: ${rsa.modExp(11, 13, 17)} (expected: 7)")
        rsa.modExp(11,13,17)
        rsa.modExp(11,20,17)
        rsa.modExp(6,20,7)

    }

    class RSA {

        private val tag = "RSA"

        // todo: generalize
        //// Modulo m
        //private val m: Int = 0

        // Returns triple (d, x, y) where d = gcd(p, q), x(p) + y(q) = d. I.e x, y, --> Multiplicative. inverse of p and q.
        // todo: may be less expensive to return array
        fun extGCD(p: Int, q: Int): Triple<Int, Int, Int> {
            if (q == 0) {
                return Triple(p, 1, 0)
            }
            val result = extGCD(q, p % q)
            return Triple(result.first, result.third, (result.second) - (p / q) * result.third)
        }


        // Returns modular multiplicative inverse x where (a)x congruent 1 (mod m)
        fun modInverse(a: Int, b: Int, m: Int): Int {
            val result = extGCD(a, b)
            val gcd = result.first
            var x = result.second
            var y = result.third
            // Ensure x is in positive modular range
            while (x < 0) {
                x += m
            }

            return x
        }


        // Fast modular exponentiation using the right-to-left binary method. See https://en.wikipedia.org/wiki/Modular_exponentiation#Right-to-left_binary_method
        fun modExp(base: Int, exp: Int, m: Int): Int {
            if (m == 1) {
                return 0
            }
            //assert((m -1 ) * (m - 1) < Integer.MIN_VALUE)

            var result = 1
            var base = base % m
            var exp = exp
            while (exp > 0) {
                if ((exp % 2) == 1) {
                    result = (result * base) % m
                }
                // Signed shift right (equivalent to java's >>)
                exp = exp shr (1)
                base = (base * base) % m
            }
            Log.d("RSA.modExp", "modExp(base=$base, exp=$exp, m=$m)=$result")
            return result
        }

        // Iterate though the String msg, getting the ordinance value and raising to power e mod n
        fun encrypt(e: Int, N: Int, msg: String): String {
            var cypherText = ""
//          todo: code
//            for (char in msg) {
//                val m = ord(charar)
//                cypher += String(BigInteger.)
//            }
        return cypherText
        }

        fun decrypt() {

        }

    }

}