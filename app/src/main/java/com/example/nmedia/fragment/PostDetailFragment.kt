package com.example.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nmedia.R
import com.example.nmedia.databinding.FragmentPostDetailBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.viewmodel.PostViewModel

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private var postId: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getLong(ARG_POST_ID) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post = state.posts.find { it.id == postId }
            post?.let { setupPost(it) }
        }
        setupClickListeners()
    }



    private fun setupPost(updatedPost: Post) {
        binding.apply {
            author.text = updatedPost.author
            published.text = updatedPost.published.toString()
            content.text = updatedPost.content
            likes.text = formatCount(updatedPost.likes)
            shares.text = formatCount(updatedPost.shareCount)
            ivLike.isChecked = updatedPost.likedByMe

            videoContainer.visibility = if (!updatedPost.video.isNullOrBlank()) View.VISIBLE else View.GONE
            if (!updatedPost.video.isNullOrBlank()) {
                playButton.setOnClickListener { openVideo(updatedPost.video!!) }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            ivLike.setOnClickListener { viewModel.likeById(postId) }
            //ivShare.setOnClickListener { viewModel.repostById(postId) }

            btnEdit.setOnClickListener {
                findNavController().navigate(
                    R.id.action_postDetailFragment_to_postEditFragment,
                    Bundle().apply { putLong("post_id", postId) }
                )
            }

            btnDelete.setOnClickListener {
                viewModel.removeById(postId)
                findNavController().popBackStack()
            }
        }
    }


    private fun openVideo(videoUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply { data = videoUrl.toUri() }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Не найдено приложение для открытия видео",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Ошибка открытия видео: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formatCount(count: Int): String = when {
        count < 1000 -> "$count"
        count < 10_000 -> "${count / 1000}.${(count % 1000) / 100}K".replace(".0K", "K")
        count < 1_000_000 -> "${count / 1000}K"
        count < 10_000_000 -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M".replace(
            ".0M",
            "M"
        )

        else -> "${count / 1_000_000}M"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    companion object {
        private const val ARG_POST_ID = "post_id"
        fun newInstance(postId: Long) = PostDetailFragment().apply {
            arguments = Bundle().apply { putLong(ARG_POST_ID, postId) }
        }
    }
}
