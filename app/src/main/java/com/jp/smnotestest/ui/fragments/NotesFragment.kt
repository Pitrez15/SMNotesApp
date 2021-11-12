package com.jp.smnotestest.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jp.smnotestest.R
import com.jp.smnotestest.adapters.NotesAdapter
import com.jp.smnotestest.databinding.FragmentNotesBinding
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.ui.viewmodels.MainViewModel
import com.jp.smnotestest.utils.NoteListener

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

            override fun onChangeItemCompleteSwitchClicked(item: Note) {
                mainViewModel.completeNote(item)
            }

            override fun onDeleteNoteClicked(item: Note) {
                mainViewModel.deleteNote(item)
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

        mainViewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                binding.tvNotesNoNotes.visibility = View.VISIBLE
                binding.rvNotes.visibility = View.GONE
            }
            else {
                binding.tvNotesNoNotes.visibility = View.GONE
                binding.rvNotes.visibility = View.VISIBLE
                notesAdapter.differ.submitList(notes.filter { note -> note.deleted != 1 })
            }
        })
    }
}