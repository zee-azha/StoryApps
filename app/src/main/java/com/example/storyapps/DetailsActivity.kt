package com.example.storyapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapps.data.database.StoriesResponseItem
import com.example.storyapps.databinding.ActivityDetailsBinding
import com.example.storyapps.utils.SimpleDateFormatter.setLocalTime

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val story = intent.getParcelableExtra<StoriesResponseItem>(EXTRA_STORY)

        showStory(story)
    }

    private fun showStory(story: StoriesResponseItem?) {
        binding.apply {
            if (story != null) {
                tvCreated.text = setLocalTime(story.createdAt)
                tvName.text = story.name
                tvStory.text = story.description
                Glide
                    .with(this@DetailsActivity)
                    .load(story.photoUrl)
                    .into(imagePreview)
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}