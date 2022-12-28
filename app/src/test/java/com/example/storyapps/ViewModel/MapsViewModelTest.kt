package com.example.storyapps.ViewModel

import androidx.paging.ExperimentalPagingApi
import com.example.storyapps.data.Repository
import com.example.storyapps.data.datalocal.StoriesResponse
import com.example.storyapps.utils.DataDummy
import com.example.storyapps.utils.Resource
import com.example.storyapps.viewModel.MapsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {


    @Mock
    private lateinit var repository: Repository
    private lateinit var mapsViewModel: MapsViewModel
    private val dummyStoriesResponse= DataDummy.generateDummyStoryResponse()
    private val dummyToken = DataDummy.generateDummyToken()

    @Before
    fun setup(){
        mapsViewModel = MapsViewModel(repository)
    }

    @Test
    fun `Get Story with location successfully`() = runTest {
        val expectedResult = flowOf(Resource.Success(dummyStoriesResponse))
        `when`(repository.getAllStoryWithLocation(dummyToken)).thenReturn(expectedResult)
        mapsViewModel.getAllStoriesWithLocation(dummyToken).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(true)
                    Assert.assertNotNull(it.data)
                    Assert.assertSame(it.data,dummyStoriesResponse)
                }
                is Resource.Failure ->{
                    Assert.assertFalse(it.data!!.error)
                }
                is Resource.Loading ->{
                }

            }
        }
        verify(repository).getAllStoryWithLocation(dummyToken)
    }

    @Test
    fun `Get Story with location is failed`() = runTest {
        val expectedResult : Flow<Resource<StoriesResponse>> = flowOf(Resource.Failure("failed"))
        `when`(repository.getAllStoryWithLocation(dummyToken)).thenReturn(expectedResult)
        mapsViewModel.getAllStoriesWithLocation(dummyToken).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(false)
                    Assert.assertNotNull(it.data!!.error)
                }
                is Resource.Failure ->{
                    Assert.assertNotNull(it.message)
                }
                is Resource.Loading ->{
                }

            }
        }
        verify(repository).getAllStoryWithLocation(dummyToken)

    }
}