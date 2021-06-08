package com.example.decryptapp

import android.content.Context
import android.util.Log
import java.math.BigInteger
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
            if (isCoPrime(e, phiN)) {
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
    private fun isCoPrime(p: Long, q: Long): Boolean {
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

    // SQRT for BigInts
    // todo: optimize further
    // todo: ceiling or floor?
    private fun sqrt(N: BigInteger): BigInteger {
        if (N <= BigInteger.valueOf(0)) {
            throw ArithmeticException("Expected a positive number")
        } else {
            val two = BigInteger.valueOf(2L)
            if (N < two) {
                // I.e. sqrt of 0 or 1.
                return N
            }
            var y = N.divide(two)
            while (y.compareTo(N.divide(y)) > 0) {
                y = N.divide(y).add(y).divide(two)
            }
            return if (N.compareTo(y.multiply(y)).equals(0)) {
                y
            } else {
                y.add(BigInteger.ONE)
            }
        }
    }

    private fun isSquare(N: BigInteger): Boolean {
        val sqr: BigInteger = sqrt(N)
        return sqr.multiply(sqr).equals(N) || sqr.add(BigInteger.ONE)
            .multiply(sqr.add(BigInteger.ONE)).equals(N)
    }

    // todo: optimize further if possible
    // Returns the two factors p,q of N as a Pair of BigIntegers
    fun fermatFactors(N: BigInteger): Pair<BigInteger, BigInteger> {
        Log.d("$tag.fermatFactors()", "Started with N = $N")
        var a = sqrt(N)
        if ((a.multiply(a)).equals(N)) {
            return Pair(a, a)
        }
        var b = a.multiply(a).subtract(N)
        while (!isSquare(b)) {
            a = a.plus(BigInteger.ONE)
            b = a.multiply(a).subtract(N)
        }
        val r1 = a.subtract(sqrt(b))
        val r2 = N.divide(r1)

        return Pair(r1, r2)
    }

    /**
     * Uses a list of the first 100K primes to find the factorization of N.
     * Value of N passed should be smaller than the largest product of the 100,000th prime squared (1299709^2 = 1689243484681).
     */
    // todo: this is just bad, don't use it, or optimize it somehow
    fun bruteForceSmallKey(N: BigInteger, c: Context): Pair<Int, Int> {
        if (N > BigInteger("1689243484681")) {
            throw java.lang.ArithmeticException("Expected a value of N <= 1689243484681.")
        } else {
            // todo: we can load the primes as a list of ints, they're all smaller than MAX_VALUE. the products however are NOT, so they must be BigInts
            val primeList =
                c.assets.open("100kPrimes.txt") // Text file with the first 100,000 primes, one per line
            val reader = primeList.bufferedReader()
            // todo: awful, optimize
            val primesString = reader.readLines()
            var primeInts =
                ArrayList<Int>() // Ints are fine here, largest is smaller than Integer.MAX_VALUE
            for (str in primesString) {
                // todo actually terrible, we don't want to do this, read them as Ints first thing if possible
                primeInts.add(Integer.parseInt(str))
            }

            // Iterate through the list and try every combination
            // todo: there should be a smarter way to do this depending on N, some lower bound or something. we shouldn't have to try every single combination
            for (x in primeInts) {
                for (y in primeInts) {
                    val candidate = BigInteger.valueOf(x.toLong() * y)
                    if (candidate.equals(N)) return Pair(
                        x,
                        y
                    ) // When we find a match, we have found the factors p,q of N
                }
            }
        }
        Log.wtf(
            "$tag.bruteForceSmallKey",
            "bruteForceSmallKey() failed to find candidate. Is N the product of two primes?",
            IllegalStateException()
        )
        return Pair(0, 0) // Shouldn't ever happen
    }

    /**
     * Uses Pollard's rho algorithm to find the factors for smaller values of N. Returns Pair(p, q), or (0, 0) if it fails.
     * Details: https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     */
    fun pollardFactorization(
        N: BigInteger,
        x: BigInteger
    ): Pair<BigInteger, BigInteger> {
        // todo: x?
        //var loop = 1
        //var count: BigInteger
        //var xFixed = x
        var xVar = BigInteger("2")
        var yVar = BigInteger("2")
        //var size = x
        var factor = BigInteger.ONE

        while (factor.equals(BigInteger.ONE)) {
            // Tortoise step
            xVar = rhoMod(xVar, N)

            // Hare step
            yVar = rhoMod(rhoMod(yVar, N), N)

            // Check gcd(x - y, N). If it's not 1, we've found a factor.
            factor = (xVar.minus(yVar)).gcd(N)
        }

//        // todo: not working correctly
//        do {
//            count = size
//            do {
//                xVar = xVar.multiply(xVar.plus(BigInteger.ONE)).divide(N)
//                factor = (xVar.minus(xFixed)).abs().gcd(N)
//                count.dec() // todo: does this work? here
//            } while (factor.equals(1))
//            size.multiply(BigInteger.valueOf(2L))
//            xFixed = xVar
//            loop.inc()
//        } while (factor.equals(1))

        return if (factor.equals(N)) {
            Pair(BigInteger.ZERO, BigInteger.ZERO)
        } else {
            var p = factor
            var q = N.divide(p)
            // Make sure p is the smaller of the two factors
            if (p > q) {
                val temp = q
                q = p
                p = temp
            }
            Pair(p, q)
        }
    }

    // Modulus function for Pollard's rho algorithm
    private fun rhoMod(x: BigInteger, mod: BigInteger): BigInteger {
        return (x.multiply(x.plus(BigInteger.ONE))).mod(mod)
    }

    // todo: code this
    /**
     * Given the public key (e, N) and cypher-text = cypher, try a combination of methods to factorize N (finding p, q).
     * From that, derive phi(N) = (p-1)(q-1), and then the modular multiplicative inverse of e = d (mod N).
     *
     */
    //
    fun bruteForce(e: Int, N: BigInteger, cypher: String, c: Context): String {
        val maxFixedN =
            BigInteger("1689243484681") // Used for our quick factorization for small values of N
        var primeFactors = Pair(0, 0)
        if (N <= maxFixedN) {
            primeFactors = bruteForceSmallKey(N, c)
        }

        val phiN = BigInteger.valueOf(primeFactors.first.toLong() * primeFactors.second)

        // todo: use phi(N) to derive M.I. of e, then use that to decrypt message

        var plaintext = ""

        return plaintext
    }

    // todo: use quadratic sieve to bruteforce small primes N (?)
    fun bruteForceSieve() {

    }

}