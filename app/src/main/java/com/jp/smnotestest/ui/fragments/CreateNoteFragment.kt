package com.jp.smnotestest.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
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
    var hasFile = false
    var selectedUri: Uri? = null

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
            if (!it.attachment.isNullOrEmpty()) {
                hasFile = true
                binding.llCreateAddAttachment.visibility = View.GONE
                binding.ivCreateAttachment.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it.attachment).into(binding.ivCreateAttachment)
            }

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

        binding.llCreateAddAttachment.setOnClickListener {
            getAttachment()
        }

        binding.llCreateShare.setOnClickListener{
            note?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${it.title}:\n${it.description}\n${it.attachment}")
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
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
                    binding.etCreateDescription.editText?.text.toString() == note?.description &&
                    ((hasFile && selectedUri == null) || (!hasFile && selectedUri == null))) {
                    Toast.makeText(requireContext(), "Nothing to update", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                updateNote(
                    note?.id!!,
                    binding.etCreateTitle.editText?.text.toString(),
                    binding.etCreateDescription.editText?.text.toString(),
                    note?.attachment
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

    private fun updateNote(id: Int, title: String, description: String, url: String?) {
        if (hasFile && selectedUri != null) {
            mainViewModel.uploadFile(selectedUri!!)
            mainViewModel.uploadFileStatus.observe(viewLifecycleOwner, { result ->
                result?.let {
                    when (it) {
                        is ResultHelper.Success -> {
                            mainViewModel.updateNote(id, title, description, it.message)
                            mainViewModel.resetUploadFileLiveData()
                        }
                        is ResultHelper.Error -> {
                            if (it.message != "Reset") {
                                Toast.makeText(requireContext(), "File was not uploaded.", Toast.LENGTH_SHORT).show()
                                mainViewModel.resetUploadFileLiveData()
                            }
                        }
                    }
                }
            })
        }
        else {
            mainViewModel.updateNote(id, title, description, url)
        }

        mainViewModel.updateNoteStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        Toast.makeText(requireContext(), "Note updated.", Toast.LENGTH_SHORT).show()
                        note?.title = title
                        note?.description = description
                        mainViewModel.resetUpdateNoteLiveData()
                    }
                    is ResultHelper.Error -> {
                        if (it.message != "Reset") {
                            Toast.makeText(requireContext(), "Note was not updated.", Toast.LENGTH_SHORT).show()
                            mainViewModel.resetUploadFileLiveData()
                        }
                    }
                }
            }
        })
    }

    private fun createNote(title: String, description: String) {
        if (hasFile && selectedUri != null) {
            mainViewModel.uploadFile(selectedUri!!)
            mainViewModel.uploadFileStatus.observe(viewLifecycleOwner, { result ->
                result?.let {
                    when (it) {
                        is ResultHelper.Success -> {
                            mainViewModel.createNote(title, description, it.message)
                        }
                        is ResultHelper.Error -> {
                            Toast.makeText(requireContext(), "File was not uploaded.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
        else {
            mainViewModel.createNote(title, description)
        }

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

    private fun getAttachment() {
        getContent.launch("image/*")
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        hasFile = true
        Glide.with(requireContext()).load(uri).into(binding.ivCreateAttachment)
        binding.llCreateAddAttachment.visibility = View.GONE
        binding.ivCreateAttachment.visibility = View.VISIBLE
    }
}