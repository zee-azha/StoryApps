package com.example.storyapps.ViewModel

import com.example.storyapps.data.Repository
import com.example.storyapps.data.datalocal.LoginResponse
import com.example.storyapps.data.datalocal.RegisterResponse
import com.example.storyapps.utils.DataDummy
import com.example.storyapps.utils.MainDispatcherRule
import com.example.storyapps.utils.Resource
import com.example.storyapps.viewModel.UserViewModel
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @get:Rule
    var coroutineTest = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var  userViewModel: UserViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyEmail = "zez@gmail.com"
    private val dummyPassword = "password"
    private val dummyToken = "token"
    private val dummyName = "Zeezha"


    @Before
    fun setup(){
        userViewModel = UserViewModel(repository)
    }
    @Test
    fun `get Registration is sucessfully` () = runTest {
        val expectedResult = flowOf(Resource.Success(dummyRegisterResponse))
        `when`(repository.register(dummyName,dummyEmail,dummyPassword)).thenReturn(expectedResult)
        userViewModel.register(dummyName,dummyEmail,dummyPassword).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(true)
                    Assert.assertNotNull(it.data)
                    Assert.assertSame(it.data, dummyRegisterResponse)
                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.data!!.error)
                }
                is Resource.Loading ->{}
            }
        }
        verify(repository).register(dummyName,dummyEmail,dummyPassword)
    }

    @Test
    fun `get registration is failed ` () = runTest {
        val expectedResult : Flow<Resource<RegisterResponse>> = flowOf(Resource.Failure("failed"))
        `when`(repository.register(dummyName,dummyEmail,dummyPassword)).thenReturn(expectedResult)

        userViewModel.register(dummyName,dummyEmail,dummyPassword).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(false)
                    Assert.assertFalse(it.data!!.error)
                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.message)
                }
                is Resource.Loading ->{}
            }
        }
        verify(repository).register(dummyName,dummyEmail,dummyPassword)
    }

    @Test
    fun `get login is failed `() = runTest {
        val expectedResult : Flow<Resource<LoginResponse>> = flowOf(Resource.Failure("failed"))
        `when`(repository.login(dummyEmail,dummyPassword)).thenReturn(expectedResult)

        userViewModel.login(dummyEmail,dummyPassword).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(false)
                    Assert.assertFalse(it.data!!.error)
                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.message)
                }
                is Resource.Loading ->{}
            }
        }
        verify(repository).login(dummyEmail,dummyPassword)
    }

    @Test
    fun `get login is successfully`() = runTest{
        val expectedResult = flowOf(Resource.Success(dummyLoginResponse))
        `when`(repository.login(dummyEmail,dummyPassword)).thenReturn(expectedResult)
        userViewModel.login(dummyEmail,dummyPassword).collect{
            when(it){
                is Resource.Success ->{
                    Assert.assertTrue(true)
                    Assert.assertNotNull(it.data)
                    Assert.assertSame(it.data, dummyLoginResponse)
                }
                is Resource.Failure -> {
                    Assert.assertNotNull(it.data!!.error)
                }
                is Resource.Loading ->{}
            }
        }
        verify(repository).login(dummyEmail,dummyPassword)
    }

    @Test
    fun `get save token successfully `(): Unit = runTest {
        userViewModel.saveToken(dummyToken)
        verify(repository).saveToken(dummyToken)

    }

    @Test
    fun `get token successfully and not empty`() = runTest {
        val expectedResult = flowOf(dummyToken)
        `when`(repository.getToken()).thenReturn(expectedResult)

        userViewModel.getToken().collect{
            Assert.assertNotNull(it)
            Assert.assertEquals(dummyToken,it)
        }
        verify(repository).getToken()
    }

    @Test
    fun `delete token is successfully`():Unit = runTest{
        userViewModel.deleteToken()
        verify(repository).deleteToken()
    }
}