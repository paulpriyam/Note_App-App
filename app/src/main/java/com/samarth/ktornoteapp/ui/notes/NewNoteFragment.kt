package com.samarth.ktornoteapp.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.samarth.ktornoteapp.R
import com.samarth.ktornoteapp.databinding.FragmentNewNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    val noteViewModel: NoteViewModel by activityViewModels()
    val args: NewNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel.oldNote = args.note

        noteViewModel.oldNote?.noteTitle?.let {
            binding.newNoteTitleEditText.setText(it)
        }

        noteViewModel.oldNote?.description?.let {
            binding.newNoteDescriptionEditText.setText(it)
        }
        binding.date.isVisible = noteViewModel.oldNote != null
        noteViewModel.oldNote?.date?.let {
            binding.date.text = noteViewModel.milliToDate(it)
        }
    }

    override fun onPause() {
        super.onPause()
        if (noteViewModel.oldNote == null) {
            createNote()
        } else {
            updateNote()
        }
    }


    private fun createNote() {

        val noteTitle = binding.newNoteTitleEditText.text?.toString()?.trim()
        val description = binding.newNoteDescriptionEditText.text?.toString()?.trim()

        if (noteTitle.isNullOrEmpty() && description.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Note is Empty!", Toast.LENGTH_SHORT).show()
            return
        }

        noteViewModel.createNote(noteTitle, description)
    }

    private fun updateNote() {

        val noteTitle = binding.newNoteTitleEditText.text?.toString()?.trim()
        val description = binding.newNoteDescriptionEditText.text?.toString()?.trim()

        if (noteTitle.isNullOrEmpty() && description.isNullOrEmpty()) {
            noteViewModel.deleteNote(noteViewModel.oldNote?.noteId.toString())
            return
        }
        noteViewModel.updateNote(noteTitle, description)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}