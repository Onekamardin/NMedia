package com.example.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nmedia.R
import com.example.nmedia.databinding.ActivityPostEditBinding

class PostEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostEditBinding

    private var postId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getLongExtra(PostEditContract.KEY_POST_ID, 0L)
        val initialText = intent.getStringExtra(PostEditContract.KEY_TEXT) ?: ""

        if (postId != 0L) {
            binding.edit.setText(initialText)
            binding.edit.setSelection(initialText.length) // Курсор в конец текста
            title = "Редактирование поста"
        } else {
            title = "Новый пост"
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString().trim()
            if (text.isBlank()) {
                Toast.makeText(this, "Текст поста не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(PostEditContract.KEY_TEXT, text)
                    putExtra(PostEditContract.KEY_POST_ID, postId)
                }
            )
            finish()
        }

        binding.cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
