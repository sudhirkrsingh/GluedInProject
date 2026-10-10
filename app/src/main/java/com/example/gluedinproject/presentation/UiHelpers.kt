package com.example.gluedinproject.presentation
import androidx.activity.enableEdgeToEdge
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
fun AppCompatActivity.applyInsets(root: View) {
    enableEdgeToEdge()
    WindowInsetsControllerCompat(window, root).isAppearanceLightStatusBars = false
    WindowInsetsControllerCompat(window, root).isAppearanceLightNavigationBars = false
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(bars.left, bars.top, bars.right, bars.bottom); insets
    }
}
fun renderState(state: UiState<*>, content: View, loading: View, panel: View, message: TextView, retry: View) {
    content.isVisible = state is UiState.Success
    loading.isVisible = state is UiState.Loading
    panel.isVisible = state is UiState.Error || state is UiState.Empty
    message.text = when(state) { is UiState.Error -> state.message; UiState.Empty -> "No content available."; else -> "" }
    retry.isVisible = state is UiState.Error || state is UiState.Empty
}
