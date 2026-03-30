package com.example.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nmedia.R
import com.example.nmedia.databinding.CardPostBinding
import com.example.nmedia.dto.Post

typealias LikeListener = (Post) -> Unit
typealias ShareListener = (Post) -> Unit

class PostsAdapter(private val likeListener: LikeListener, private val shareListener: ShareListener) :
    ListAdapter<Post, PostsViewHolder>(PostDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding, likeListener, shareListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
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

class PostsViewHolder(
    private val binding: CardPostBinding,
    private val likeListener: LikeListener,
    private val shareListener: ShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
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
            binding.ivLike.setOnClickListener {
                likeListener(post)
            }
            binding.ivShare.setOnClickListener {
                shareListener(post)
            }
        }
    }
}

object PostDiffCallBack: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}