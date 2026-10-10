package com.example.gluedinproject.data.remote
import android.content.res.AssetManager
import com.example.gluedinproject.data.dto.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
interface ContentRemoteDataSource { suspend fun fetchHome(): HomeDto }
class MockContentRemoteDataSource(private val assets: AssetManager, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ContentRemoteDataSource {
    override suspend fun fetchHome(): HomeDto = withContext(dispatcher) {
        val json = JSONObject(assets.open("home.json").bufferedReader().use { it.readText() })
        val content = json.getJSONArray("content")
        val rails = json.getJSONArray("rails")
        HomeDto(json.optString("heroId").takeIf { it.isNotBlank() }, List(content.length()) { index ->
            val c = content.getJSONObject(index)
            ContentDto(c.getString("id"), c.getString("title"), c.getString("description"), c.getString("thumbnailUrl"), c.getString("bannerUrl"), c.getString("videoUrl"), c.getString("genre"), c.getString("duration"))
        }, List(rails.length()) { index ->
            val r = rails.getJSONObject(index); val ids = r.getJSONArray("contentIds")
            RailDto(r.getString("id"), r.getString("title"), List(ids.length()) { ids.getString(it) })
        })
    }
}
