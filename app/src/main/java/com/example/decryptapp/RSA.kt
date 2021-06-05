package com.example.decryptapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.math.MathUtils
import java.lang.ArithmeticException
import java.math.BigInteger
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random

class RSA() {

    // todo: if anything is seriously wrong, we might need BigIntegers instead of longs

    private val tag = "RSA"

    // list of very small primes, useful for cracking (?) demo systems
    private val verySmallPrimes = arrayOf(
        2L,
        3L,
        5L,
        7L,
        11L,
        13L,
        17L,
        19L,
        23L,
        29L,
        31L,
        37L,
        41L,
        43L,
        47L,
        53L,
        59L,
        61L,
        67L,
        71L,
        73L,
        79L,
        83L,
        89L,
        97L,
        101L
    )

    // Returns triple (d, x, y) where d = gcd(a, b), x(a) + y(b) = d. I.e x, y, --> Multiplicative. inverse of a and b.
    fun extGCD(a: Int, b: Int): Triple<Int, Int, Int> {
        if (b == 0) {
            return Triple(a, 1, 0)
        }
        val result = extGCD(b, (a % b))
        val triple =
            Triple(result.first, result.third, (result.second) - (a / b) * result.third)
        Log.d(
            "$tag.extGCD",
            "Extended GCD of $a, $b: GCD=${triple.first}, ($a)^-1=${triple.second}, ($b)^-1=${triple.third}"
        )
        return triple
    }

    // Generates a key
    fun genKey(keysize: Int = 1024): Long {
        // generate p, q
        val p = genLargePrime(keysize)
        val q = genLargePrime(keysize)

        val n = p * q
        val phiN = (p - 1) * (q - 1) // per fermat's little theorem

        // find e, coprime with phiN && 1 < e <= phiN
        val start = pow(keysize - 1.toLong(), 2)
        val end = pow(keysize.toLong(), 2) - 1
        var e = 0L
        while (true) {
            e = Random.nextLong(start, end)
            if (isCoprime(e, phiN)) {
                break
            }
        }

        return e

    }

    // Generates a random large prime number, with a size determined by keysize
    // todo test this
    fun genLargePrime(keysize: Int): Long {
        while (true) {
            val start = pow(keysize - 1.toLong(), 2)
            val end = pow(keysize.toLong(), 2) - 1

            var x = Random.nextLong(start, end)
            if (isPrime(x)) {
                return x
            }
        }
    }

    fun pow(n: Long, exp: Int): Long {
        // todo test
        return BigInteger.valueOf(n).pow(exp).toLong()
    }

    /**
     * Probabilistic test of primality. Returns true if candidate N is PROBABLY prime or false if composite.
     * Long N: a Long > 3, odd candidate to be tested for primality
     * Long c: a number s.t. c**{2r} = N-1
     * For pseudo-code and explanation see https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test#Miller%E2%80%93Rabin_test
     */
    fun rabinMillerPrimality(N: Long, c: Long): Boolean {
        val a = Random.nextLong(2L, (N - 2))
        var x = modExp(a, c, N)
        if (x == 1L || x == N - 1) {
            return true // Fermat's theorem
        }
        var d = c
        while (d != N - 1) {
            x = modExp(x, 2, N)
            d *= 2
            if (x == 1L) {
                return false
            }
            if (x == N - 1) {
                return true
            }

        }
        return false
    }

    // True/false test for co-primality of p, q
    private fun isCoprime(p: Long, q: Long): Boolean {
        return (gcdL(p, q) == 1L)
    }

    // Boolean test for primality. Checking if it's NOT prime, defer to rabinMiller if uncertain
    private fun isPrime(candidate: Long): Boolean {
        if (candidate <= 3) {
            return (candidate > 1)
        }

        if (candidate in verySmallPrimes) {
            return true
        }

        // Division check
        for (p in verySmallPrimes) {
            if (candidate % p == 0L) {
                return false
            }
        }

        // find d, M.I. of e mod phiN, i.e. e*d equiv 1 (mod phiN)
        var c = candidate - 1
        while (c % 2 == 0L) {
            c /= 2
        }

        for (i in (0..128)) {
            if (!rabinMillerPrimality(candidate, c)) return false
        }

        return true
    }

    // Euclidean algorithm for two ints a, b, returns gcd
    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a
        else gcd(b, a % b)
    }

    // Euclidean algorithm for two longs a, b, returns gcd
    private fun gcdL(a: Long, b: Long): Long {
        return if (b == 0L) a
        else gcdL(b, a % b)
    }


    // Returns modular multiplicative inverse x where (a)x congruent 1 (mod m). Won't work if the numbers aren't co-prime
    fun modInverse(a: Int, m: Int): Int {
        val result = extGCD(a, m)
        var x = result.second
        x = (x % m + m) % m
        Log.d("$tag.modInverse", "Modular inverse of $a (mod $m): $x")
        return x
    }


    // Fast modular exponentiation using the right-to-left binary method. See https://en.wikipedia.org/wiki/Modular_exponentiation#Right-to-left_binary_method
    fun modExp(base: Long, exp: Long, m: Long): Long {
        if (m == 1L) {
            return 0
        }
        //assert((m -1 ) * (m - 1) < Integer.MIN_VALUE)
        var result = 1L
        var base = base % m
        var exp = exp
        while (exp > 0) {
            if ((exp % 2) == 1L) {
                result = (result * base) % m
            }
            // Signed shift right (equivalent to java's >>)
            exp = exp shr (1)
            base = (base * base) % m
        }
        //Log.d("RSA.modExp", "modExp(base=$base, exp=$exp, m=$m)=$result")
        return result
    }

    // Iterate though the String msg, getting the ordinance value and raising to power e mod n
    fun encrypt(e: Int, N: BigInteger, msg: String): String {
        var cypher = ""
        for (c in msg) {
            cypher += modExp(c.toLong(), e.toLong(), N.toLong()).toString() + " "
        }
        Log.d("$tag.encrypt", "$msg encrypted to [$cypher]")
        return cypher
    }

    // Decrypts a given string of numbers separated by whitespace into ascii plaintext
    fun decrypt(d: Int, N: BigInteger, cypher: String): String {
        var plaintext = ""

        // Split on whitespace
        val input = cypher.split(" ")
        for (x in input) {
            if (x.isNotEmpty()) {
                val c = x.toInt()
                plaintext += (modExp(c.toLong(), d.toLong(), N.toLong())).toChar()
            }
        }
        Log.d("$tag.decrypt", "[$cypher] decrypted to \"$plaintext\"")
        return plaintext
    }


//    // todo might not be needed
//    private fun isSQRT(n: Long): BigInteger{
//        var x = n
//        var y = Math.floorDiv((x + Math.floorDiv(n, x)), 2L)
//        while (y < x) {
//            x = y
//            y = Math.floorDiv((x + Math.floorDiv(n, x)), 2L)
//        }
//        return BigInteger.valueOf(x)
//    }


    // With reference to https://stackoverflow.com/questions/2685524/check-if-biginteger-is-not-a-perfect-square
    private fun bigIntSQRT(N: BigInteger): BigInteger {
        if (N <= BigInteger.valueOf(0)) {
            throw ArithmeticException("Expected a positive number")
        } else {
            val bitLength = N.bitLength()
            var root = BigInteger.ONE.shiftLeft(bitLength / 2)
            while (!isSqrt(N, root)) {
                root = root.add(N.divide(root).divide(BigInteger("2")))
            }

            return root
        }
    }

    private fun isSqrt(N: BigInteger, root: BigInteger): Boolean {
        val lowerBound = root.pow(2)
        val upperBound = root.add(BigInteger.ONE).pow(2)
        return lowerBound <= N && N < upperBound
    }

    // Returns the two factors p,q of N
    private fun fermatFactors(N: BigInteger): Pair<BigInteger, BigInteger> {
        // todo
        val a = isSQRT(N)
        val z = pow(a, 2) - N
        b =
            return Pair(1L, 2L)
    }

    // Given the public key (e, N) and cyphertext, Use Fermat's factorization method to figure out the factors of N and decode the message
    fun bruteForceFermat(e: Int, N: Int, cypher: String): String {
        var plaintext = ""

        return plaintext
    }

}