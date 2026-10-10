package com.example.gluedinproject.presentation.detail
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import coil.load
import com.example.gluedinproject.*
import com.example.gluedinproject.databinding.ActivityDetailBinding
import com.example.gluedinproject.presentation.*
import com.example.gluedinproject.presentation.player.PlayerActivity
import kotlinx.coroutines.launch
class DetailActivity : AppCompatActivity() {
    private val model: ContentViewModel by viewModels { SimpleFactory { ContentViewModel((application as OttApplication).getContent, intent.getStringExtra("content_id").orEmpty()) } }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); val b = ActivityDetailBinding.inflate(layoutInflater); setContentView(b.root); applyInsets(b.root)
        b.back.setOnClickListener { finish() }; b.retry.setOnClickListener { model.load() }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { model.state.collect { state ->
            renderState(state, b.content, b.loading, b.statePanel, b.message, b.retry)
            if (state is UiState.Success) { val item = state.data
                b.title.text = item.title; b.description.text = item.description; b.meta.text = "${item.genre} • ${item.duration}"
                b.banner.load(item.bannerUrl) { placeholder(R.drawable.image_placeholder); error(R.drawable.image_placeholder) }
                b.play.setOnClickListener { startActivity(Intent(this@DetailActivity, PlayerActivity::class.java).putExtra("content_id", item.id)) }
            }
        } } }
    }
}
