package com.example.storyapps.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapps.data.database.StoriesResponseItem

class PagedTestSource: PagingSource<Int, LiveData<List<StoriesResponseItem>>>() {
    companion object{
        fun snapshot(item: List<StoriesResponseItem>): PagingData<StoriesResponseItem>{
            return PagingData.from(item)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoriesResponseItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoriesResponseItem>>> {
        return LoadResult.Page(emptyList(),0,1)
    }
}