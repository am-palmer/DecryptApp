package com.example.decryptapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.example.decryptapp.R
import com.example.decryptapp.RSA
import java.math.BigInteger

/**
 * Fragment for the RSA cryptosystem view.
 */
class RSAFragment : Fragment() {

    private lateinit var rsa: RSA

    private lateinit var decryptRSAImageButton: ImageButton
    private lateinit var encryptRSAImageButton: ImageButton
    private lateinit var clearButton: Button
    private lateinit var cypherRSAEditText: EditText
    private lateinit var messageRSAEditText: EditText
    private lateinit var rsaNEditText: EditText
    private lateinit var rsaEEditText: EditText
    private lateinit var rsaPEditText: EditText
    private lateinit var rsaQEditText: EditText
    private lateinit var rsaPhiNEditText: EditText
    private lateinit var rsaDEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rsa = RSA()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rsa, container, false)
        decryptRSAImageButton = root.findViewById(R.id.imageButtonDecryptRSA)
        encryptRSAImageButton = root.findViewById(R.id.imageButtonEncryptRSA)
        clearButton = root.findViewById(R.id.buttonRSAClear)
        cypherRSAEditText = root.findViewById(R.id.editTextCypherRSA)
        messageRSAEditText = root.findViewById(R.id.editTextMsgRSA)
        rsaNEditText = root.findViewById(R.id.editTextRSAN)
        rsaEEditText = root.findViewById(R.id.editTextRSAE)
        rsaPEditText = root.findViewById(R.id.editTextRSAP)
        rsaQEditText = root.findViewById(R.id.editTextRSAQ)
        rsaPhiNEditText = root.findViewById(R.id.editTextRSAPhiN)
        rsaDEditText = root.findViewById(R.id.editTextRSAModInvE)

        clearButton.setOnClickListener {
            clearViews()
            Toast.makeText(requireContext(), "Cleared data", Toast.LENGTH_SHORT).show()
        }

        decryptRSAImageButton.setOnClickListener {
            decryptRSA()
        }

        encryptRSAImageButton.setOnClickListener {
            encryptRSA()
        }

        return root
    }

    // Todo: Suspend function so it doesn't freeze main thread
    private fun decryptRSA() {
        if (cypherRSAEditText.text.isNullOrEmpty() || rsaNEditText.text.isNullOrEmpty() || rsaEEditText.text.isNullOrEmpty() || !rsaNEditText.text.isDigitsOnly() || !rsaEEditText.text.isDigitsOnly()) {
            Toast.makeText(
                requireContext(),
                "Please enter a cypher to decrypt, as well as the public key (e, N)!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Clear the views


            val e = Integer.parseInt(rsaEEditText.text.toString())
            val N = Integer.parseInt(rsaNEditText.text.toString())
            val cypher = cypherRSAEditText.text.toString()
            val result = rsa.bruteForceText(e, N.toBigInteger(), cypher)

            // Update the UI
            messageRSAEditText.setText(result.msg)
            rsaDEditText.setText(result.d.toString())
            rsaPhiNEditText.setText(result.phiN.toString())
            rsaPEditText.setText(result.p.toString())
            rsaQEditText.setText(result.q.toString())

            Toast.makeText(
                requireContext(),
                "Factorized N=$N to find d=${result.d} and decrypted cyphertext",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun encryptRSA() {
        if (messageRSAEditText.text.isNullOrEmpty() || rsaEEditText.text.isNullOrEmpty() || rsaNEditText.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Need message and public key!", Toast.LENGTH_SHORT)
                .show()
        } else {
            val msg = messageRSAEditText.text.toString()
            val e = Integer.parseInt(rsaEEditText.text.toString())
            val N = BigInteger(rsaNEditText.text.toString())

            val cypher = rsa.encryptText(e, N, msg)

            // Clear views
            rsaPEditText.text.clear()
            rsaQEditText.text.clear()
            rsaPhiNEditText.text.clear()
            rsaDEditText.text.clear()
            messageRSAEditText.text.clear()

            cypherRSAEditText.setText(cypher)

            Toast.makeText(
                requireContext(),
                "Encrypted message with public key (e=$e, N=$N)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearViews() {
        cypherRSAEditText.text.clear()
        messageRSAEditText.text.clear()
        rsaNEditText.text.clear()
        rsaEEditText.text.clear()
        rsaPEditText.text.clear()
        rsaQEditText.text.clear()
        rsaDEditText.text.clear()
        rsaPhiNEditText.text.clear()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "2"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): RSAFragment {
            return RSAFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}