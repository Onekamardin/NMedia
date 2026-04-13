package com.example.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nmedia.R
import com.example.nmedia.databinding.ActivityNewPostBinding
import com.example.nmedia.dto.Post

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString().trim()
            if (text.isBlank()) {
                setResult(RESULT_CANCELED)
            } else {
                val intent = Intent().putExtra(NewPostContract.KEY_TEXT, text)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}

object NewPostContract : ActivityResultContract<Unit, String?>() {
    const val KEY_TEXT = "post_text"
    override fun createIntent(context: Context, input: Unit) = Intent(context, NewPostActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?) = intent?.getStringExtra(KEY_TEXT)
}

object PostEditContract : ActivityResultContract<Post?, Post?>() {
    const val KEY_TEXT = "post_text"
    const val KEY_POST_ID = "post_id"

    override fun createIntent(context: Context, input: Post?): Intent {
        return Intent(context, PostEditActivity::class.java).apply {
            input?.let {
                putExtra(KEY_POST_ID, it.id)
                putExtra(KEY_TEXT, it.content)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Post? {
        if (resultCode != Activity.RESULT_OK) return null
        val text = intent?.getStringExtra(KEY_TEXT) ?: return null
        val id = intent.getLongExtra(KEY_POST_ID, 0L)
        return Post(id = id, content = text)
    }
}
