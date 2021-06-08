package com.example.decryptapp

import android.util.Log
import junit.framework.TestCase
import java.math.BigInteger

class RSATest : TestCase() {

    private val tag = "RSATest"

    fun testExtGCD() {}

    fun testGenKey() {}

    fun testGenLargePrime() {}

    fun testPow() {}

    fun testRabinMillerPrimality() {}

    fun testModInverse() {}

    fun testModExp() {}

    fun testEncrypt() {}

    fun testDecrypt() {}

    // example: N = 105327569
    // output expected: 10223, 10303
    fun testFermatFactors() {
        // Takes a string
        Log.d(tag, "testFermatFactors() begins")
        val value = "309"
        val result = RSA().fermatFactors(BigInteger(value))
        Log.d(tag, "Factors of $value: (${result.first}, ${result.second})")
    }

    fun testBruteForceFermat() {}

    fun testPollardFactorization() {
        Log.d(tag, "testPollardFactorization() begins")
        val value = BigInteger("105327569") // output expected: 10223, 10303 todo: NOT working
        val result = RSA().pollardFactorization(value, BigInteger("2"))
        Log.d(tag, "testPollardFactorization(): Factors of $value: (${result.first}, ${result.second})")
    }
}