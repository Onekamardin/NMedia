package com.example.nmedia.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nmedia.R
import com.example.nmedia.adapter.PostListener
import com.example.nmedia.adapter.PostsAdapter
import com.example.nmedia.databinding.ActivityMainBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.utils.Utils
import com.example.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModel by viewModels<PostViewModel>()
        val adapter = PostsAdapter(
            object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.repostById(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

            })
        binding.recyclerView.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        binding.save.setOnClickListener {
            val content = binding.content.text?.toString().orEmpty()
            if (content.isBlank()) {
                Toast.makeText(this, R.string.content_is_blank, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveContent(content)
            binding.content.setText("")
            binding.content.clearFocus()

            Utils.hideKeyboard(binding.content)
        }
        viewModel.edited.observe(this) { edited ->
            if (edited.id != 0L) {
                binding.content.append(edited.content)
                Utils.showKeyboard(binding.content)
            }
        }
    }
}