package com.appliances.recycle.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appliances.recycle.repository.RegisterRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel (private val registerRepository: RegisterRepository) : ViewModel() {

    fun join(email: String, mname: String, pw: String, address:String, phone:String,  onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val call = registerRepository.RegisterUser(email, mname,pw, address, phone)
            Log.e("ViewModel", "콜내용확인" + call)
            Log.e("ViewModel", "콜내용확인" + pw)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.e("ViewModel", "성공했니?")
                        onSuccess()
                    } else {
                        Log.e("ViewModel", "실패했니?")
                        Log.e("ViewModel", "Registration failed with status code: ${response.code()} and error body: ${response.errorBody()?.string()}")
                        onFailure()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ViewModel", "Error during registration", t)
                    onFailure()
                }
            })
        }
    }
}