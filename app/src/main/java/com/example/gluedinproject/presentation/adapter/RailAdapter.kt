package com.example.gluedinproject.presentation.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import coil.load
import com.example.gluedinproject.R
import com.example.gluedinproject.databinding.*
import com.example.gluedinproject.domain.model.*
class RailAdapter(private val onClick: (Content) -> Unit) : ListAdapter<ContentRail, RailAdapter.Holder>(object : DiffUtil.ItemCallback<ContentRail>() {
    override fun areItemsTheSame(a: ContentRail, b: ContentRail) = a.id == b.id
    override fun areContentsTheSame(a: ContentRail, b: ContentRail) = a == b
}) {
    class Holder(val binding: ItemRailBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemRailBinding.inflate(LayoutInflater.from(parent.context), parent, false)).also {
        it.binding.items.layoutManager = LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
        it.binding.items.adapter = ContentAdapter(onClick)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) { val rail = getItem(position); holder.binding.railTitle.text = rail.title; (holder.binding.items.adapter as ContentAdapter).submitList(rail.items) }
}
class ContentAdapter(private val onClick: (Content) -> Unit) : ListAdapter<Content, ContentAdapter.Holder>(object : DiffUtil.ItemCallback<Content>() {
    override fun areItemsTheSame(a: Content, b: Content) = a.id == b.id
    override fun areContentsTheSame(a: Content, b: Content) = a == b
}) {
    class Holder(val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) { val item = getItem(position); with(holder.binding) {
        title.text = item.title; meta.text = "${item.genre} • ${item.duration}"
        thumbnail.load(item.thumbnailUrl) { placeholder(R.drawable.image_placeholder); error(R.drawable.image_placeholder); crossfade(true) }
        root.contentDescription = "${item.title}, ${item.genre}, ${item.duration}. View details"
        root.setOnClickListener { onClick(item) }
    } }
}
