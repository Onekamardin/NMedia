package com.example.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nmedia.R
import com.example.nmedia.databinding.CardPostBinding
import com.example.nmedia.dto.Post

typealias LikeListener = (Post) -> Unit
typealias ShareListener = (Post) -> Unit
typealias RemoveListener = (Post) -> Unit

interface PostListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
}

class PostsAdapter(
    private val listener: PostListener
) :
    ListAdapter<Post, PostsViewHolder>(PostDiffCallBack) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding, listener)
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
    private val listener: PostListener

) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            //tvLikeCount.text = formatCount(post.likes)
            tvShareCount.text = formatCount(post.shareCount)
            ivLike.isChecked = post.likedByMe
            ivLike.text = formatCount(post.likes)
            binding.ivLike.setOnClickListener {
                listener.onLike(post)
            }
            binding.ivShare.setOnClickListener {
                listener.onShare(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}