package com.example.gluedinproject
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.gluedinproject.databinding.ActivityMainBinding
import com.example.gluedinproject.presentation.*
import com.example.gluedinproject.presentation.adapter.RailAdapter
import com.example.gluedinproject.presentation.detail.DetailActivity
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private val model: HomeViewModel by viewModels { SimpleFactory { HomeViewModel((application as OttApplication).getHome) } }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = ActivityMainBinding.inflate(layoutInflater); setContentView(b.root); applyInsets(b.root)
        val navigate: (String) -> Unit = { startActivity(Intent(this, DetailActivity::class.java).putExtra("content_id", it)) }
        val adapter = RailAdapter { navigate(it.id) }; b.rails.layoutManager = LinearLayoutManager(this); b.rails.adapter = adapter
        b.retry.setOnClickListener { model.load() }
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) { model.state.collect { state ->
            renderState(state, b.content, b.loading, b.statePanel, b.message, b.retry)
            if (state is UiState.Success) {
                val hero = state.data.hero; b.hero.visibility = if (hero == null) android.view.View.GONE else android.view.View.VISIBLE
                hero?.let { b.title.text = it.title; b.description.text = it.description; b.meta.text = "FEATURED • ${it.genre} • ${it.duration}"; b.banner.load(it.bannerUrl) { placeholder(R.drawable.image_placeholder); error(R.drawable.image_placeholder) }; b.details.setOnClickListener { _ -> navigate(it.id) } }
                adapter.submitList(state.data.rails.filter { it.items.isNotEmpty() })
            }
        } } }
    }
}
