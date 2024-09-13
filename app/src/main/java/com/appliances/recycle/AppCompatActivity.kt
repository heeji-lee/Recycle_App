package com.appliances.recycle

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.appliances.recycle.SerializedName.ImageClassificationResponse
import com.appliances.recycle.network.INetworkService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import retrofit2.Call
import retrofit2.Response


class AppCompatActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var btnCapture: Button
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_compat)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        btnCapture = findViewById(R.id.btnCapture)


        // "카메라 촬영" 버튼 클릭 이벤트 처리
        btnCapture.setOnClickListener {
            // 카메라를 실행하여 사진을 찍음
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)

            // 이미지를 서버로 업로드
            uploadImage(imageBitmap)
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.100.201.6:8080/")  // Spring 서버의 URL로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(INetworkService::class.java)

        // Bitmap을 파일로 변환
        val file = File(cacheDir, "image.jpg")
        try {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 파일을 MultipartBody.Part로 변환
        val requestFile = RequestBody.create("multipart/form-data".toMediaType(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // 이미지 업로드 요청
        val call = api.uploadImage(body)
        call.enqueue(object : Callback<ImageClassificationResponse> {
            override fun onResponse(call: Call<ImageClassificationResponse>, response: Response<ImageClassificationResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("Upload", "Full Response: ${response.body()}")
                    Log.d("Upload", "Success: ${result?.result}")
                    // 결과를 UI에 표시하는 등의 작업 수행
                    resultTextView.text = "Result: ${result?.result}, Confidence: ${result?.confidence}"
                } else {
                    Log.e("Upload", "Server error: ${response.code()}")
                    resultTextView.text = "Server error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ImageClassificationResponse>, t: Throwable) {  // 여기를 수정
                Log.e("Upload", "Error: ${t.message}")
                resultTextView.text = "Upload failed: ${t.message}"
            }
        })
    }
}