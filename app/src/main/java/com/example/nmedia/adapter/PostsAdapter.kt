package com.example.nmedia.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nmedia.R
import com.example.nmedia.databinding.CardPostBinding
import com.example.nmedia.dto.Post
import com.example.nmedia.model.PostWithAuthor

interface PostListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onPostClick(post: Post)
}

class PostsAdapter(
    private val listener: PostListener,
    private val baseUrl: String
) : ListAdapter<PostWithAuthor, PostsViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding, listener, baseUrl)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(getItem(position))
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
    private val listener: PostListener,
    private val baseUrl: String
) : RecyclerView.ViewHolder(binding.root) {

    private var isInteractionWithControls = false

    fun bind(item: PostWithAuthor) {
        val post = item.post
        val author = item.author

        with(binding) {
            author.name = author.name

            // Аватар
            if (!author.avatar.isNullOrBlank()) {
                val avatarUrl = "$baseUrl/avatars/${author.avatar}"
                Glide.with(itemView.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_error)
                    .circleCrop()
                    .timeout(10000)
                    .into(avatarImageView)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(avatarImageView)
            }

            published.text = post.published.toString()
            content.text = post.content

            videoContainer.visibility = if (post.attachment != null && !post.attachment?.url.isNullOrBlank()) View.VISIBLE else View.GONE

            playButton.setOnClickListener {
                isInteractionWithControls = true
                post.attachment?.let { attachment ->
                    openVideo(attachment.url)
                }
            }

            val shareCount = 0
            ivShare.text = formatCount(shareCount)

            ivLike.isChecked = post.likedByMe

            ivLike.text = formatCount(post.likes)

            ivLike.setOnClickListener {
                isInteractionWithControls = true
                listener.onLike(post)
            }

            ivShare.setOnClickListener {
                isInteractionWithControls = true
                listener.onShare(post)
            }

            menu.setOnClickListener {
                isInteractionWithControls = true
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

        itemView.setOnClickListener {
            if (!isInteractionWithControls) {
                listener.onPostClick(post)
            }
            isInteractionWithControls = false
        }
    }

    private fun openVideo(videoUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = videoUrl.toUri()
            }

            if (intent.resolveActivity(binding.root.context.packageManager) != null) {
                binding.root.context.startActivity(intent)
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Не найдено приложение для открытия видео",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                binding.root.context, "Ошибка открытия видео: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<PostWithAuthor>() {
    override fun areItemsTheSame(oldItem: PostWithAuthor, newItem: PostWithAuthor): Boolean {
        return oldItem.post.id == newItem.post.id
    }

    override fun areContentsTheSame(oldItem: PostWithAuthor, newItem: PostWithAuthor): Boolean {
        val p1 = oldItem.post
        val p2 = newItem.post
        return p1.id == p2.id &&
                p1.likes == p2.likes &&
                p1.likedByMe == p2.likedByMe &&
                p1.content == p2.content
    }
}
