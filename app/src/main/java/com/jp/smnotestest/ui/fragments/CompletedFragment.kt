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
import com.jp.smnotestest.databinding.FragmentCompletedBinding
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.ui.viewmodels.MainViewModel
import com.jp.smnotestest.utils.NoteListener

class CompletedFragment : Fragment() {

    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!

    val mainViewModel: MainViewModel by viewModels()

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)

        notesAdapter = NotesAdapter(object : NoteListener {
            override fun onItemClicked(item: Note) {
                val bundle = Bundle().apply {
                    putParcelable("note", item)
                }
                /*findNavController().navigate(
                    R.id.action_completedFragment_to_createNoteFragment,
                    bundle
                )*/
            }

            override fun onDeleteNoteClicked(item: Note) {
                mainViewModel.deleteNote(item)
            }
        })
        val mLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvCompleted.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = notesAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*mainViewModel.getCompletedNotes()

        mainViewModel.notesCompleted.observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                binding.tvCompletedNoNotes.visibility = View.VISIBLE
                binding.rvCompleted.visibility = View.GONE
            }
            else {
                binding.tvCompletedNoNotes.visibility = View.GONE
                binding.rvCompleted.visibility = View.VISIBLE
                notesAdapter.differ.submitList(notes.filter { note -> note.deleted != 1 })
            }
        })*/
    }
}