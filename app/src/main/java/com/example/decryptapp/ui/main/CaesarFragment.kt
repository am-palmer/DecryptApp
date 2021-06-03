package com.example.decryptapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.decryptapp.CaesarCypher
import com.example.decryptapp.R

/**
 * Fragment for the Caesar cypher view.
 */
class CaesarFragment : Fragment() {

    private lateinit var caesar: CaesarCypher

    private lateinit var pageViewModel: PageViewModel

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
//        val textView: TextView = root.findViewById(R.id.section_label)
//        pageViewModel.text.observe(this, Observer<String> {
//            textView.text = it
//        })
        val messageTextView: EditText = root.findViewById(R.id.editTextMessage)
        val cypherTextView: EditText = root.findViewById(R.id.editTextCyphertext)
        val shiftKeyView: EditText = root.findViewById(R.id.editTextShift)
        val encryptButton: ImageButton = root.findViewById(R.id.imageButtonEncryptCaesar)

        encryptButton.setOnClickListener {
            // todo: get the value from the plaintext edittext, shift, and pass to caesar (coroutine?), then populate text boxes with result
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
                Toast.makeText(requireContext(), "Encrypted message", Toast.LENGTH_SHORT).show()
            }
        }

        val decryptButton: ImageButton = root.findViewById(R.id.imageButtonDecryptCaesar)
        decryptButton.setOnClickListener {
            // todo: decrypt
            if (cypherTextView.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a cypher to decrypt!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // todo: coroutine? maybe needed for dictionary check
                if (shiftKeyView.text.isNotEmpty()) {
                    // use the shift key provided
                    // todo: use key checkbox in ui
                    val result = caesar.decrypt(
                        messageTextView.text.toString(),
                        Integer.parseInt(shiftKeyView.text.toString())
                    )
                    messageTextView.setText(result)
                    Toast.makeText(
                        requireContext(),
                        "Decrypted using shift=${shiftKeyView.text}",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // bruteforce the shift value and message
                    val result =
                        caesar.bruteForce(messageTextView.text.toString(), true, requireContext())
                    // result[0] is our candidate solution. hopefully...
                    // set the result plaintext and key
                    shiftKeyView.setText(result[0].key.toString())
                    messageTextView.setText(result[0].str)
                    Toast.makeText(requireContext(), "Decrypted likely message", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }


        return root
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