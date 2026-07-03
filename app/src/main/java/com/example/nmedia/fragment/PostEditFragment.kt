package com.example.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nmedia.databinding.FragmentPostEditBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.viewmodel.PostViewModel

class PostEditFragment : Fragment() {
    private var _binding: FragmentPostEditBinding? = null
    private val binding get() = _binding!!
    private var postId: Long = 0L
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getLong(ARG_POST_ID) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]

        if (postId != 0L) {
            val post = viewModel.getPostById(postId)
            if (post != null) {
                binding.edit.setText(post.content)
            } else {
                viewModel.data.observe(viewLifecycleOwner) { state ->
                    val foundPost = state.posts.find { it.post.id == postId }
                    foundPost?.let {
                        binding.edit.setText(it.post.content)
                        viewModel.data.removeObservers(viewLifecycleOwner)
                    }
                }
            }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            ok.setOnClickListener {
                val newContent = edit.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    if (postId != 0L) {
                        val currentPost = viewModel.getPostById(postId)
                        if (currentPost != null) {
                            val updatedPost = currentPost.copy(content = newContent)
                            viewModel.edit(updatedPost)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Не удалось загрузить пост для редактирования",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                    } else {
                        val newPost = Post(
                            id = 0L,
                            authorId = 1L,
                            content = newContent,
                            published = System.currentTimeMillis(),
                            likedByMe = false,
                            likes = 0,
                            attachment = null
                        )
                        viewModel.saveContent(newPost)
                    }
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Текст поста не может быть пустым",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            cancel.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POST_ID = "post_id"
        fun newInstance(postId: Long = 0L) = PostEditFragment().apply {
            arguments = Bundle().apply { putLong(ARG_POST_ID, postId) }
        }
    }
}
