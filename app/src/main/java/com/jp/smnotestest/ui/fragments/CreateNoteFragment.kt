package com.jp.smnotestest.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
            binding.llCreateComplete.visibility = View.VISIBLE

            binding.etCreateTitle.editText?.setText(it.title)
            binding.etCreateDescription.editText?.setText(it.description)
            binding.btnCreateOrUpdate.text = "Update Note"
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val convertedDate = sdf.parse(it.createdAt!!)
            binding.tvCreateDate.text = SimpleDateFormat("dd-MM-yyyy").format(convertedDate)
            if (it.completed == 1) {
                binding.tvCreateComplete.text = "Completed"
                binding.ivCreateComplete.setColorFilter(R.color.primary)
            }
        }.run {
            Log.d("args", args.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llCreateComplete.setOnClickListener{
            note?.let {
                mainViewModel.completeNote(it)
                if (it.completed == 0) {
                    binding.tvCreateComplete.text = "Completed"
                    binding.ivCreateComplete.setColorFilter(R.color.primary)
                    it.completed = 1
                }
                else {
                    binding.tvCreateComplete.text = "Complete"
                    binding.ivCreateComplete.colorFilter = null
                    it.completed = 0
                }
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

                mainViewModel.updateNote(
                    note?.id!!,
                    binding.etCreateTitle.editText?.text.toString(),
                    binding.etCreateDescription.editText?.text.toString()
                ).invokeOnCompletion {
                    Toast.makeText(requireContext(), "Note Updated", Toast.LENGTH_SHORT).show()
                    note?.title = binding.etCreateTitle.editText?.text.toString()
                    note?.description = binding.etCreateDescription.editText?.text.toString()
                }
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

                mainViewModel.createNote(
                    binding.etCreateTitle.editText?.text.toString(),
                    binding.etCreateDescription.editText?.text.toString()
                ).invokeOnCompletion {
                    Toast.makeText(requireContext(), "Note Created", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_createNoteFragment_to_notesFragment)
                }
            }
        }
    }
}