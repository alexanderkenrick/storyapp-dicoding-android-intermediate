package com.alexander.storyapp

import com.alexander.storyapp.data.response.story.Story

object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                i.toString(),
                "createdAt $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}