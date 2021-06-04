package com.example.decryptapp

import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase

class CaesarCypherTest : TestCase() {

    fun testEncrypt() {
        /**
         * Paste the message to test here
         */
        val message = "hello world"
        /**
         * Place the key (shift value) to use here
         */
        val key = 0

        val result = CaesarCypher().encrypt(message, key)

        println("Caesar Encryption result:")
        println(result)
    }

    fun testDecrypt() {}

    fun testBruteForce() {
        /**
         * Paste the message to test here
         */
        val cyphertext = "hello world"
            //val dictionary = CaesarCypherTest::class.java.getResource("/10kEng.txt")!!.readText()

        val result = CaesarCypher().bruteForce(cyphertext, true, InstrumentationRegistry.getInstrumentation().targetContext)

        println("Caesar decryption result (most likely candidate solution): " + result[0].str)

    }
}