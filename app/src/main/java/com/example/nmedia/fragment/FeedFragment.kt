package com.example.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nmedia.R
import com.example.nmedia.adapter.PostListener
import com.example.nmedia.adapter.PostsAdapter
import com.example.nmedia.databinding.FragmentFeedBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import android.os.Handler
import android.os.Looper

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: PostViewModel by activityViewModels()
        val BASE_URL = "http://10.0.2.2:9999"

        val adapter = PostsAdapter(object : PostListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)
            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }
                val chooser = Intent.createChooser(intent, getString(R.string.nmedia))
                startActivity(chooser)
            }
            override fun onRemove(post: Post) = viewModel.removeById(post.id)
            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postEditFragment,
                    Bundle().apply { putLong("post_id", post.id) }
                )
            }
            override fun onPostClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postDetailFragment,
                    Bundle().apply { putLong("post_id", post.id) }
                )
            }
        }, BASE_URL)

        binding.recyclerView.adapter = adapter
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.emptyText.isVisible = state.empty && !state.loading
            binding.errorGroup.visibility = if (state.error) View.VISIBLE else View.GONE
        }
        binding.retryButton.setOnClickListener {
            viewModel.retryLoadPosts()
        }

        fun showErrorSnackbar(message: String) {
            Handler(Looper.getMainLooper()).post {
                Snackbar.make(
                    binding.root,
                    message,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Повторить") { _ ->
                        viewModel.retryLoadPosts()
                    }
                    .show()
            }
        }
        viewModel.setErrorCallback { message ->
            showErrorSnackbar(message)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}



