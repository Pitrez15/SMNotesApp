package com.jp.smnotestest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jp.smnotestest.R
import com.jp.smnotestest.adapters.NotesAdapter
import com.jp.smnotestest.databinding.FragmentNotesBinding
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.ui.viewmodels.MainViewModel
import com.jp.smnotestest.utils.NoteListener
import com.jp.smnotestest.utils.ResultHelper

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    val mainViewModel: MainViewModel by viewModels()

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        notesAdapter = NotesAdapter(object : NoteListener {
            override fun onItemClicked(item: Note) {
                val bundle = Bundle().apply {
                    putParcelable("note", item)
                }
                findNavController().navigate(
                    R.id.action_notesFragment_to_createNoteFragment,
                    bundle
                )
            }

            override fun onDeleteNoteClicked(item: Note) {
                mainViewModel.deleteNote(item)
                mainViewModel.deleteNoteStatus.observe(viewLifecycleOwner, { result ->
                    result?.let {
                        when (it) {
                            is ResultHelper.Success -> {
                                Toast.makeText(requireContext(), "Note deleted.", Toast.LENGTH_SHORT).show()
                            }
                            is ResultHelper.Error -> {
                                Toast.makeText(requireContext(), "Note was not deleted.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        })

        val mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNotes.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = notesAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.getNotes()

        mainViewModel.notes.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (it) {
                    is ResultHelper.Success -> {
                        binding.tvNotesNoNotes.visibility = View.GONE
                        binding.rvNotes.visibility = View.VISIBLE
                        notesAdapter.differ.submitList(it.data?.filter { note -> note.deleted != 1 })
                    }
                    is ResultHelper.Error -> {
                        binding.tvNotesNoNotes.visibility = View.VISIBLE
                        binding.rvNotes.visibility = View.GONE
                    }
                }
            }
        })
    }
}