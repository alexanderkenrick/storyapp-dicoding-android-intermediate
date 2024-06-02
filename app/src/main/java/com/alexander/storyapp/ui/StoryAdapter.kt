package com.alexander.storyapp.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexander.storyapp.data.response.story.Story
import com.alexander.storyapp.databinding.ItemStoryBinding
import com.alexander.storyapp.ui.detail.DetailActivity
import com.bumptech.glide.Glide

class StoryAdapter:  PagingDataAdapter<Story, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story, holder)
        }
    }

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story, holder: MyViewHolder) {
            with(binding){
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                Glide.with(holder.itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)
            }

            binding.root.setOnClickListener {
                val activity = holder.itemView.context as Activity
                val moveData = Intent(activity, DetailActivity::class.java)
                moveData.putExtra(DetailActivity.EXTRA_NAME, story.name)
                moveData.putExtra(DetailActivity.EXTRA_DESCRIPTION, story.description)
                moveData.putExtra(DetailActivity.EXTRA_IMAGE, story.photoUrl)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        androidx.core.util.Pair(holder.binding.ivItemPhoto as View, "image")
                    )
                activity.startActivity(moveData, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}