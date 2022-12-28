package com.example.storyapps.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapps.data.database.RemoteKeys
import com.example.storyapps.data.database.StoriesDatabase
import com.example.storyapps.data.database.StoriesResponseItem

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val database: StoriesDatabase, private val apiService: Api, private val token: String): RemoteMediator<Int, StoriesResponseItem>(){


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoriesResponseItem>
    ): MediatorResult {
        val page = when(loadType){
            LoadType.REFRESH->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND->{
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return  MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND ->{
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories(token, page, state.config.pageSize)
            val endOfPaginationReached = responseData.items.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storiesDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.items.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)

                responseData.items.forEach {
                    val stories = StoriesResponseItem(
                        it.id,
                        it.name,
                        it.description,
                        it.createdAt,
                        it.photoUrl,
                        it.lon,
                        it.lat
                    )
                    database.storiesDao().insertStory(stories)
                }

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoriesResponseItem>): RemoteKeys? {
        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoriesResponseItem>): RemoteKeys? {
        return  state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoriesResponseItem>): RemoteKeys? {
        return  state.pages.firstOrNull {it.data.isNotEmpty()}?.data?.firstOrNull()?.let {data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }

    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

}