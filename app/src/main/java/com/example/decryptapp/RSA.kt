package com.example.decryptapp

import android.content.Context
import android.util.Log
import java.math.BigInteger
import kotlin.math.pow
import kotlin.random.Random

class RSA() {

    private val tag = "RSA"

    // Returns triple (d, x, y) where d = gcd(a, b), x(a) + y(b) = d. I.e x, y, --> Multiplicative. inverse of a and b.
    fun extGCD(a: Int, b: Int): Triple<Int, Int, Int> {
        if (b == 0) {
            return Triple(a, 1, 0)
        }
        val result = extGCD(b, (a % b))
        return Triple(result.first, result.third, (result.second) - (a / b) * result.third)
    }

    // Generates a public key, by first generating two primes p, q, then N = pq, phi(N), and returning some e co-prime to phi(N)
    fun genKey(keysize: Int = 1024): Pair<BigInteger, BigInteger> {
        // generate p, q
        val p = genLargePrime(keysize)
        val q = genLargePrime(keysize)

        val phiN = (p - 1) * (q - 1) // Per Fermat's little theorem

        // find e, coprime with phiN && 1 < e <= phiN
        val start = pow(keysize - 1.toLong(), 2)
        val end = pow(keysize.toLong(), 2) - 1
        var e: Long
        while (true) {
            e = Random.nextLong(start, end)
            if (isCoPrime(e, phiN)) {
                break
            }
        }

        return Pair(e.toBigInteger(), (p*q).toBigInteger())
    }

    // Generates a random large prime number, with a size determined by keysize
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
        //return BigInteger.valueOf(n).pow(exp).toLong()
        // Todo: test
        return n.toDouble().pow(exp.toDouble()).toLong()
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

    // Boolean test for primality, relying on the rabin miller primality test
    private fun isPrime(candidate: Long): Boolean {
        if (candidate <= 3) {
            return (candidate > 1)
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

    // Euclidean algorithm for two longs a, b, returns gcd
    private fun gcdL(a: Long, b: Long): Long {
        return if (b == 0L) a
        else gcdL(b, a % b)
    }

    // Returns modular multiplicative inverse x where (a)x congruent 1 (mod m). Won't work if the numbers aren't co-prime
    // Todo: BigInteger so we have support for bigger numbers
    fun modInverse(a: Int, m: Int): Int {
        val result = extGCD(a, m)
        var x = result.second
        x = (x % m + m) % m
        Log.d("$tag.modInverse", "Modular inverse of $a (mod $m): $x")
        return x
    }

    /**
     * Fast modular exponentiation using the right-to-left binary method. See https://en.wikipedia.org/wiki/Modular_exponentiation#Right-to-left_binary_method
     * Allows us to perform exponentiation for otherwise unfeasibly large numbers.
     */
    private fun modExp(base: Long, exp: Long, m: Long): Long {
        if (m == 1L) {
            return 0
        }
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

    // Square root function for BigInts
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
     * Uses Pollard's rho algorithm to find the factors for smaller values of N. Returns Pair(p, q), or (0, 0) if it fails.
     * Details: https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     */
    fun pollardFactors(
        N: BigInteger,
        x: BigInteger = BigInteger.valueOf(2L)
    ): Pair<BigInteger, BigInteger> {

        var xVar = x
        var yVar = x
        var factor = BigInteger.ONE

        // Pollard's rho algorithm logic
        while (factor.equals(BigInteger.ONE)) {
            // "Tortoise" step
            xVar = rhoMod(xVar, N)

            // "Hare" step
            yVar = rhoMod(rhoMod(yVar, N), N)

            // Check gcd(x - y, N). If it's not 1, we've found a factor.
            factor = (xVar.minus(yVar)).gcd(N)
        }

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

    // Iterate though the String msg, getting the ordinance value and raising to power e mod n. Returns a cypher of encrypted ordinance values
    fun encryptText(e: Int, N: BigInteger, msg: String): String {
        var cypher = ""
        for (c in msg) {
            cypher += modExp(c.toLong(), e.toLong(), N.toLong()).toString() + " "
        }
        Log.d("$tag.encrypt", "$msg encrypted to [$cypher]")
        return cypher
    }

    // Decrypts a given string of ordinance values separated by whitespace into ascii plaintext
    fun decryptText(d: Int, N: BigInteger, cypher: String): String {
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

    fun decrypt(d: Int, N: BigInteger, cypher: BigInteger): BigInteger {
        return modExp(cypher.toLong(), d.toLong(), N.toLong()).toBigInteger()
    }

    fun encrypt(e: Int, N: BigInteger, msg: BigInteger): BigInteger {
        return modExp(msg.toLong(), e.toLong(), N.toLong()).toBigInteger()
    }

    /**
     * Given the public key (e, N) and cypher-text = cypher, use Pollard's rho algorithm to factorize N (finding p, q).
     * From that, derive phi(N) = (p-1)(q-1), and then the modular multiplicative inverse of e = d (mod N).
     * This is then used to decrypt the cyphertext.
     */
    //
    fun bruteForce(e: Int, N: BigInteger, cypher: BigInteger): BigInteger {

        // Use Pollard's rho algorithm to find the factors p, q of N
        val primeFactors = pollardFactors(
            N,
            BigInteger("2")
        )

        // Check we actually factorized N
        if (primeFactors.first.multiply(primeFactors.second) != N) {
            throw java.lang.ArithmeticException("Failed to factorize N and ran out of options")
        }

        // Calculate phi(N)
        val phiN = (primeFactors.first.minus(BigInteger.ONE)).multiply(
            primeFactors.second.minus(
                BigInteger.ONE
            )
        )

        // Calculate modular multiplicative inverse of e (mod phiN)
        // Todo: support for values bigger than Integer.MAX_VALUE
        val d = modInverse(e, phiN.toInt())

        // Return the decrypted message.
        return decrypt(d, N, cypher)
    }

}