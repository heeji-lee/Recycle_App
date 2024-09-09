package com.appliances.recycle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.appliances.recycle.databinding.ActivityRegisterBinding
import com.appliances.recycle.repository.RegisterRepository
import com.sylovestp.firebasetest.testspringrestapp.retrofitN.MyApplication
import com.sylovestp.firebasetest.testspringrestapp.retrofitN.INetworkService
import com.appliances.recycle.viewModel.RegisterViewModel
import com.appliances.recycle.viewModelFactory.RegisterViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var networkService: INetworkService

    // RegisterViewModel을 ViewModelProvider로 초기화
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(RegisterRepository(networkService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataBinding 초기화
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myApplication = applicationContext as MyApplication
        networkService = myApplication.networkService


        // Register 버튼 클릭 이벤트 처리
        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val mname = binding.editTextMname.text.toString()
            val pw = binding.editTextPassword.text.toString()
            val adress = binding.editTextAdress.text.toString()
            val phone = binding.editTextPhone.text.toString()


            // ViewModel에서 회원가입 처리 호출
            registerViewModel.join(
                email = email,
                mname = mname,
                pw = pw,
                address = adress,   // 필요한 경우 주소와 전화번호 추가
                phone = phone,
                onSuccess = {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Log.e("RegisterActivity", "Registration failed: 서버에서 받은 응답을 확인하세요."+registerViewModel + "  "+email+ "  "+ pw )
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
