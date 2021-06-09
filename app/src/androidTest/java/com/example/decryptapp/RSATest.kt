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

    fun testEncryptRSA() {
        val p = 11
        val q = 13
        val N = p * q
        val e = 49 // co-prime to, and lesser than, phiN
        val msg = "hello world"
        val cypher = RSA().encrypt(e, BigInteger.valueOf(N.toLong()), msg)
        Log.d(tag, "testEncryptRSA(): \"$msg\" encrypted to \"$cypher\" using public key (N=$N,e=$e)")
    }

    // todo
    // Takes a private key (PhiN, d) where d is the modular inverse of e, to decrypt a cypher-text
    fun testDecryptRSA() {
        val cypher = "130 127 82 82 111 32 93 111 36 82 100"
        val p = 11
        val q = 13
        val N = p * q
        val phiN = (p - 1) * (q - 1)
        val e = 49
        val d = RSA().modInverse(e, phiN)
        val plaintext = RSA().decrypt(d, BigInteger.valueOf(N.toLong()), cypher)
        Log.d(
            tag,
            "testDecryptRSA(): \"$cypher\" decrypted to \"$plaintext\" using private key (Phi(N)=$phiN,d=$d)"
        )
    }

    // example: N = 105327569
    // output expected: 10223, 10303
    fun testFermatFactors() {
        // Takes a string
        Log.d(tag, "testFermatFactors() begins")
        val value = "309"
        val result = RSA().fermatFactors(BigInteger(value))
        Log.d(tag, "Factors of $value: (${result.first}, ${result.second})")
    }

    // N = 1043584443301, p, q = (1008001, 1035301)
    fun testPollardFactors() {
        Log.d(tag, "testPollardFactors() begins")
        val value = BigInteger("502560280658509")
        val result = RSA().pollardFactors(value, BigInteger("2"))
        Log.d(tag, "testPollardFactors(): Factors of $value: (${result.first}, ${result.second})")
    }

    // todo implement
    fun testBruteForce() {}

}