package com.example.storyapps.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapps.data.Repository
import com.example.storyapps.data.database.StoriesResponseItem
import com.example.storyapps.utils.DataDummy
import com.example.storyapps.utils.ListAdapter
import com.example.storyapps.utils.MainDispatcherRule
import com.example.storyapps.utils.PagedTestSource
import com.example.storyapps.viewModel.StoriesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {
    @get:Rule
    var coroutineTest = MainDispatcherRule()

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()


    @Mock
    private lateinit var repository: Repository
    private lateinit var  storyViewModel: StoriesViewModel
    private val dummyStoryList = DataDummy.generateDummyStoryList()
    private val dummyToken = "token"

    @Before
    fun setup(){
        storyViewModel = StoriesViewModel(repository)
    }

    @Test
    fun `Get all stories successfully`() = runTest {
        val data = PagedTestSource.snapshot(dummyStoryList)
        val expectedResult :Flow<PagingData<StoriesResponseItem>> = flow{
            emit(data)
        }

        `when`(repository.getAllStories(dummyToken)).thenReturn(expectedResult)

        storyViewModel.getStory(dummyToken).observeForever {
            val asyncPagingDataDiffer =AsyncPagingDataDiffer(
                diffCallback = ListAdapter.DIFF_CALLBACK,
                updateCallback = noopListCallback,
                mainDispatcher = coroutineTest.testDispatcher,
                workerDispatcher = coroutineTest.testDispatcher
            )


            CoroutineScope(Dispatchers.IO).launch {
                asyncPagingDataDiffer.submitData(it)
            }
            advanceUntilIdle()
            verify(repository).getAllStories(dummyToken)
            Assert.assertNotNull(asyncPagingDataDiffer.snapshot())
        }
    }

    private val noopListCallback = object : ListUpdateCallback{
        override fun onInserted(position: Int, count: Int) {
        }

        override fun onRemoved(position: Int, count: Int) {
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
        }

    }

}