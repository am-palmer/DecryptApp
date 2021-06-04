package com.example.decryptapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.decryptapp.CaesarCypher
import com.example.decryptapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment for the Caesar cypher view.
 */
class CaesarFragment : Fragment() {

    private lateinit var caesar: CaesarCypher

    private lateinit var pageViewModel: PageViewModel

    private lateinit var messageTextView: EditText
    private lateinit var cypherTextView: EditText
    private lateinit var shiftKeyView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
        caesar = CaesarCypher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_caesar, container, false)
        messageTextView = root.findViewById(R.id.editTextMessage)
        cypherTextView = root.findViewById(R.id.editTextCyphertext)
        shiftKeyView = root.findViewById(R.id.editTextShift)
        val encryptButton: ImageButton = root.findViewById(R.id.imageButtonEncryptCaesar)

        encryptButton.setOnClickListener {
            if (messageTextView.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a message to encrypt!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (shiftKeyView.text.isNullOrEmpty() || Integer.parseInt(shiftKeyView.text.toString()) < 0 || Integer.parseInt(
                    shiftKeyView.text.toString()
                ) > 25
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a shift value between 0 and 25",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val cypherText = caesar.encrypt(
                    messageTextView.text.toString(),
                    Integer.parseInt(shiftKeyView.text.toString())
                )
                cypherTextView.setText(cypherText)
                Toast.makeText(
                    requireContext(),
                    "Encrypted message using shift=${shiftKeyView.text}",
                    Toast.LENGTH_SHORT
                ).show()
                shiftKeyView.text.clear()
            }
        }

        val decryptButton: ImageButton = root.findViewById(R.id.imageButtonDecryptCaesar)
        decryptButton.setOnClickListener {
            if (cypherTextView.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a cypher to decrypt!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                CoroutineScope(Dispatchers.Default).launch {
                    decryptInBackground(cypherTextView.text.toString(), requireContext())
                }

                // todo: coroutine
                // Let's not even do this
//                if (shiftKeyView.text.isNotEmpty()) {
//                    // use the shift key provided
//
//                    val result = caesar.decrypt(
//                        messageTextView.text.toString(),
//                        Integer.parseInt(shiftKeyView.text.toString())
//                    )
//                    messageTextView.setText(result)
//                    Toast.makeText(
//                        requireContext(),
//                        "Decrypted using shift=${shiftKeyView.text}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                }
                // bruteforce the shift value and message
                // result[0] is our candidate solution. hopefully...


            }
        }
        return root
    }

    private fun decryptInBackground(text: String, context: Context) {
        val result =
            caesar.bruteForce(text, true, context)
        updateUIAfterDecrypt(result[0].str)
    }

    private fun updateUIAfterDecrypt(plaintext: String) = CoroutineScope(Dispatchers.Main).launch {
        shiftKeyView.text.clear()
        messageTextView.setText(plaintext)
        Toast.makeText(requireContext(), "Decrypted likely message", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): CaesarFragment {
            return CaesarFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}