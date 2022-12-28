package com.example.storyapps.ViewModel

import com.example.storyapps.data.Repository
import com.example.storyapps.data.datalocal.UploadResponse
import com.example.storyapps.utils.DataDummy
import com.example.storyapps.utils.MainDispatcherRule
import com.example.storyapps.utils.Resource
import com.example.storyapps.viewModel.AddStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get:Rule
    var coroutineTest = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var  storyViewModel: AddStoryViewModel
    private val dummyUploadResponse = DataDummy.generateDummyUploadResponse()
    private val dummyMultiPart = DataDummy.generateDummyMultipartFile()
    private val dummyBodyRequest = DataDummy.generateDummyRequestBody()
    private val dummyToken = "token"


    @Before
    fun setup(){
        storyViewModel = AddStoryViewModel(repository)
    }

    @Test
    fun `Get token successfully`() = runTest {
        val expectedResult = flowOf(dummyToken)
        `when`(storyViewModel.getAuthToken()).thenReturn(expectedResult)

       storyViewModel.getAuthToken().collect{
            Assert.assertNotNull(it)
            Assert.assertEquals(dummyToken,it)
        }
        Mockito.verify(repository).getToken()
    }

    @Test
    fun `Upload file failed`() = runTest {
        val expectedResult: Flow<Resource<UploadResponse>> = flowOf(Resource.Failure("failed"))
        `when`(storyViewModel.uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null)).thenReturn(expectedResult)
        storyViewModel.uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(false)
                    Assert.assertNotNull(it.data!!.error)

                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.message)
                }
                is Resource.Loading ->{}
            }
        }
        Mockito.verify(repository).uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null)
    }

    @Test
    fun `Upload file successfully`() = runTest {
        val expectedResult = flowOf(Resource.Success(dummyUploadResponse))
        `when`(storyViewModel.uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null)).thenReturn(expectedResult)
        storyViewModel.uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(true)
                    Assert.assertNotNull(it.data)
                    Assert.assertSame(dummyUploadResponse,it.data)
                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.data!!.error)
                }
                is Resource.Loading ->{}
            }
        }
        Mockito.verify(repository).uploadStory(dummyToken,dummyMultiPart,dummyBodyRequest,null,null)
    }
}