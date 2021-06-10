package com.example.decryptapp

import android.util.Log
import junit.framework.TestCase
import java.math.BigInteger

class RSATest : TestCase() {

    private val tag = "RSATest"

    // Generates a public key (e, N) with a given bit size
    fun testGenKey() {
        val pubKey = RSA().genKey(1024)
        Log.d(tag, "testGenKey(): Generated public key (e=${pubKey.first}, N=${pubKey.second})")
    }

    // Find the modular inverse of a number
    fun testModInverse() {
        val x = 270679
        val mod = BigInteger("58672380")
        val result = RSA().modInverse(x, mod.toInt())
        Log.d(tag, "testModInverse($x, mod=$mod): $result")
    }

    // example: N = 105327569
    // output expected: 10223, 10303
    fun testFermatFactors() {
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

    fun testEncryptTextRSA() {
        val p = 2239
        val q = 4457
        val N = p * q
        val e = 647 // co-prime to, and lesser than, phiN
        val msg = "uow crypto"
        val cypher = RSA().encryptText(e, BigInteger.valueOf(N.toLong()), msg)
        Log.d(
            tag,
            "testEncryptTextRSA(): \"$msg\" encrypted to \"$cypher\" using public key (N=$N,e=$e)"
        )
    }

    // Takes a private key (PhiN, d) where d is the modular inverse of e, to decrypt a cypher-text
    fun testDecryptTextRSA() {
        val cypher = "130 127 82 82 111 32 93 111 36 82 100"
        val p = 11
        val q = 13
        val N = p * q
        val phiN = (p - 1) * (q - 1)
        val e = 49
        val d = RSA().modInverse(e, phiN)
        val plaintext = RSA().decryptText(d, BigInteger.valueOf(N.toLong()), cypher)
        Log.d(
            tag,
            "testDecryptTextRSA(): \"$cypher\" decrypted to \"$plaintext\" using private key (Phi(N)=$phiN,d=$d)"
        )
    }

    // Takes public key (e, N) and message to produce encrypted BigInteger
    fun testEncryptRSA() {
        val N = BigInteger("3233")
        val e = 17
        val msg = 65
        val cypher = RSA().encrypt(e, N, msg.toBigInteger())
        Log.d(
            tag,
            "testEncryptRSA(): \"$msg\" encrypted to \"$cypher\" using public key (e=$e, N=$N)"
        )
    }

    // Takes private d (mod inverse of e), N and cypher to produce decrypted BigInteger
    fun testDecryptRSA() {
        Log.d(tag, "testDecryptRSA() started")
        val N = BigInteger("58687709")
        val d = 5419
        val cypher = BigInteger("41802438")
        val msg = RSA().decrypt(d, N, cypher)
        Log.d(
            tag,
            "testDecryptRSA(): \"$cypher\" decrypted to \"$msg\" using private d=$d (mod $N)"
        )
    }

    /**
     * Given the public key (e, N) and cypher-text, brute-force the decrypted message m by factorizing N to find p & q, and then using those to derive phi(N) and the modular inverse of e (i.e. the private key)
     */
    fun testBruteForce() {
        val N = 58687709
        val e = 270679
        val cypher = "41802438"

        val result = RSA().bruteForce(
            e,
            N.toBigInteger(),
            BigInteger(cypher)
        )

        Log.d(tag, "testBruteForce() decrypted message: $result")
    }

}