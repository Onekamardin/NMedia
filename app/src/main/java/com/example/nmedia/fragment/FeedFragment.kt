package com.example.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nmedia.R
import com.example.nmedia.adapter.PostListener
import com.example.nmedia.adapter.PostsAdapter
import com.example.nmedia.databinding.FragmentFeedBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val viewModel: PostViewModel by activityViewModels()

        val adapter = PostsAdapter(object : PostListener {
            override fun onLike(post: Post) = viewModel.likeById(post.id)
            override fun onShare(post: Post) {
                viewModel.repostById(post.id)
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
        })

        binding.recyclerView.adapter = adapter

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        return binding.root
    }
}
