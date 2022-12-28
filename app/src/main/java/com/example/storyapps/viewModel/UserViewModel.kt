package com.example.storyapps.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel constructor(private val repository: Repository) : ViewModel() {



    suspend fun register(username: String, email: String, password: String) =
        repository.register(username, email, password)

    suspend fun login(email: String, password: String) = repository.login(email, password)



    fun saveToken(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }

    fun deleteToken(){
        viewModelScope.launch {
            repository.deleteToken()
        }
    }


    fun getToken(): Flow<String?> = repository.getToken()
}