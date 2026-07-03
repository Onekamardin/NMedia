package com.example.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nmedia.databinding.FragmentNewPostBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.utils.StringArg
import com.example.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        binding.edit.setText(arguments?.contentArg)

        binding.ok.setOnClickListener {
            val newContent = binding.edit.text.toString().trim()
            if (newContent.isNotEmpty()) {
                val newPost = Post(
                    id = 0,
                    authorId = 1L,
                    content = newContent,
                    published = System.currentTimeMillis(),
                    likedByMe = false,
                    likes = 0,
                    attachment = null
                )

                viewModel.saveContent(newPost)

                findNavController().navigateUp()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Текст поста не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    companion object {
        var Bundle.contentArg by StringArg
    }
}
