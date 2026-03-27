package com.example.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nmedia.databinding.ActivityMainBinding
import com.example.nmedia.dto.Post

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

        val post = Post(
            1,
            "Нетология. Университет интернет проф...",
            "21 мая в 18:36",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен.",
            shareCount = 1205,
            likes = 1000,
            likedByMe = true
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            tvLikeCount.text = formatCount(post.likes)
            tvShareCount.text = formatCount(post.shareCount)
            if (post.likedByMe) {
                ivLike.setImageResource(R.drawable.ic_liked_24)
            }
            ivLike.setOnClickListener {
                if (post.likedByMe) post.likes-- else post.likes++
                post.likedByMe = !post.likedByMe
                ivLike.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.ic_liked_24
                    } else R.drawable.ic_heart
                )
                tvLikeCount.text = formatCount(post.likes)
            }
            ivShare.setOnClickListener {
                post.shareCount++
                tvShareCount.text = formatCount(post.shareCount)
            }


        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 10_000 -> String.format("%.1fK", count / 1000f).replace(".0K", "K")
            count < 1_000_000 -> "${count / 1000}K"
            count < 10_000_000 -> String.format("%.1fM", count / 1_000_000f).replace(".0M", "M")
            else -> "${count / 1_000_000}M"
        }
    }

}