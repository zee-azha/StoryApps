package com.example.storyapps.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapps.DetailsActivity
import com.example.storyapps.DetailsActivity.Companion.EXTRA_STORY
import com.example.storyapps.data.database.StoriesResponseItem
import com.example.storyapps.databinding.ItemListStoriesBinding
import com.example.storyapps.utils.SimpleDateFormatter.setLocalTime

class ListAdapter: PagingDataAdapter<StoriesResponseItem, ListAdapter.ListViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(holder.itemView.context, data)
        }
    }

    class ListViewHolder(private val binding: ItemListStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, listStories: StoriesResponseItem) {
            with(binding) {
                tvName.text = listStories.name
                tvCreated.text = setLocalTime(listStories.createdAt)
                tvStory.text = listStories.description
                Glide.with(itemView)
                    .load(listStories.photoUrl)
                    .into(image)
                root.setOnClickListener {
                    val intent = Intent(itemView.context, DetailsActivity::class.java)
                    intent.putExtra(EXTRA_STORY, listStories)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            context as Activity,
                            Pair(binding.image, "image"),
                            Pair(binding.tvName, "name"),
                            Pair(binding.tvStory, "story"),
                            Pair(binding.tvCreated, "created"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesResponseItem>() {
            override fun areItemsTheSame(oldItem: StoriesResponseItem, newItem: StoriesResponseItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesResponseItem, newItem: StoriesResponseItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
    }
