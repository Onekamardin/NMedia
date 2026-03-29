package com.example.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nmedia.R
import com.example.nmedia.databinding.ActivityMainBinding
import com.example.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                tvLikeCount.text = formatCount(post.likes)
                tvShareCount.text = formatCount(post.shareCount)
                ivLike.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.ic_liked_24
                    } else R.drawable.ic_heart
                )
//                ivShare.setOnClickListener {
//                    post.shareCount++
//                    tvShareCount.text = formatCount(post.shareCount)
//                }
            }
        }
        binding.ivLike.setOnClickListener {
            viewModel.like()
        }
        binding.ivShare.setOnClickListener {
            viewModel.repost()
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


}