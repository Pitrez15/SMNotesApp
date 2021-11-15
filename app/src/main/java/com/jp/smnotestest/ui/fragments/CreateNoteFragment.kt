package com.jp.smnotestest.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jp.smnotestest.R
import com.jp.smnotestest.databinding.FragmentCreateNoteBinding
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.ui.viewmodels.MainViewModel
import com.jp.smnotestest.utils.ResultHelper
import java.text.SimpleDateFormat

class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private val args: CreateNoteFragmentArgs? by navArgs()
    var note: Note? = null
    var editMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(inflater, container, false)

        args?.note?.let {
            note = it
            editMode = true
            binding.llCreateDate.visibility = View.VISIBLE
            binding.llCreateShare.visibility = View.VISIBLE

            binding.etCreateTitle.editText?.setText(it.title)
            binding.etCreateDescription.editText?.setText(it.description)
            binding.btnCreateOrUpdate.text = "Update Note"
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val convertedDate = sdf.parse(it.createdAt!!)
            binding.tvCreateDate.text = SimpleDateFormat("dd-MM-yyyy").format(convertedDate)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llCreateShare.setOnClickListener{
            note?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${it.title}:\n${it.description}")
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)

                /*mainViewModel.completeNote(it)
                if (it.completed == 0) {
                    binding.tvCreateComplete.text = "Completed"
                    binding.ivCreateComplete.setColorFilter(R.color.primary)
                    it.completed = 1
                }
                else {
                    binding.tvCreateComplete.text = "Complete"
                    binding.ivCreateComplete.colorFilter = null
                    it.completed = 0
                }*/
            }
        }

        binding.btnCreateOrUpdate.setOnClickListener {
            if (editMode) {
                if (binding.etCreateTitle.editText?.text.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Title is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (binding.etCreateDescription.editText?.text.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Description is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (binding.etCreateTitle.editText?.text.toString() == note?.title &&
                    binding.etCreateDescription.editText?.text.toString() == note?.description) {
                    Toast.makeText(requireContext(), "Nothing to update", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                updateNote(
                    note?.id!!,
                    binding.etCreateTitle.editText?.text.toString(),
                    binding.etCreateDescription.editText?.text.toString()
                )
            }
            else {
                if (binding.etCreateTitle.editText?.text.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Title is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (binding.etCreateDescription.editText?.text.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Description is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                createNote(
                    binding.etCreateTitle.editText?.text.toString(),
                    binding.etCreateDescription.editText?.text.toString()
                )
            }
        }
    }

    private fun updateNote(id: Int, title: String, description: String) {
        mainViewModel.updateNote(id, title, description)
        mainViewModel.updateNoteStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        Toast.makeText(requireContext(), "Note updated.", Toast.LENGTH_SHORT).show()
                        note?.title = title
                        note?.description = description
                    }
                    is ResultHelper.Error -> {
                        Toast.makeText(requireContext(), "Note was not updated.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun createNote(title: String, description: String) {
        mainViewModel.createNote(title, description)
        mainViewModel.createStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        Toast.makeText(requireContext(), "Note created.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_createNoteFragment_to_notesFragment)
                    }
                    is ResultHelper.Error -> {
                        Toast.makeText(requireContext(), "Note was not created.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}