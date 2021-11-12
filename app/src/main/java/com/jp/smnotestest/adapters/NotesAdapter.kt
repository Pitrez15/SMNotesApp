package com.jp.smnotestest.adapters

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jp.smnotestest.R
import com.jp.smnotestest.models.Note
import com.jp.smnotestest.utils.NoteListener
import kotlinx.android.synthetic.main.item_note.view.*
import java.text.SimpleDateFormat

class NotesAdapter(
    var noteListener: NoteListener
): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.itemView.apply {
            tvNoteItemTitle.text = note.title
            tvNoteItemDescription.text = note.description
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val convertedDate = sdf.parse(note.createdAt!!)
            tvNoteItemDate.text = SimpleDateFormat("dd-MM-yyyy").format(convertedDate)

            setOnClickListener {
                noteListener.onItemClicked(note)
            }

            if (note.completed == 1) {
                ivNoteCompleted.visibility = View.VISIBLE
            }

            btnNoteDelete.setOnClickListener {
                noteListener.onDeleteNoteClicked(note)
            }

            /*swNoteComplete.setOnClickListener {
                noteListener.onChangeItemCompleteSwitchClicked(note)
            }*/
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}