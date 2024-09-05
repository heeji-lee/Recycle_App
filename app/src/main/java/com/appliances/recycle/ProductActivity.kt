package com.appliances.recycle

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appliances.recycle.databinding.ActivityProductBinding
import com.appliances.recycle.dto.PredictionResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sylovestp.firebasetest.testspringrestapp.retrofit.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class ProductActivity : AppCompatActivity() {

    private lateinit var apiService: INetworkService
    private lateinit var imageView: ImageView
    private lateinit var resultView: TextView
    private var imageUri: Uri? = null  // Nullable URI

    private val cameraRequestCode = 1
    private lateinit var cameraImageUri: Uri


    // 이미지 선택 방법을 묻는 다이얼로그 표시
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")

        // 기본 중앙에 뜨는 alertdialog
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Select Image Source")
//        builder.setItems(options) { dialog, which ->
//            when (which) {
//                0 -> openCamera()  // 카메라
//                1 -> openGallery()  // 갤러리
//            }
//        }
//        builder.show()

        // 밑에서 슬라이드 형식으로 올라옴 bottomsheetdialog
        val builder = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_image_source, null)
        val listView = view.findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> openCamera()
                1 -> openGallery()
            }
            builder.dismiss()
        }

        // 다이얼로그에 설정한 뷰를 연결하고 표시
        builder.setContentView(view)
        builder.show()
    }

    // 카메라 열기
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                cameraImageUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    it
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                selectImageLauncher.launch(cameraIntent)
            }
        }
    }

    // 갤러리 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    // 이미지 파일 생성 (임시 파일 생성, 앱의 외부 저장소 저장)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private val selectImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null && result.data?.data != null) {
                // 갤러리에서 이미지를 선택한 경우
                imageUri = result.data?.data
            } else {
                // 카메라로 사진을 찍은 경우
                imageUri = cameraImageUri
            }

            // 이미지 로드 및 코너 둥글게 적용
//            Glide.with(this)
//                .load(imageUri) // 이미지 URL 또는 로컬 리소스
//                .apply(RequestOptions().circleCrop())
//                .into(imageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.btnPhoto
        resultView = binding.predictResultView

        binding.btnPhoto.setOnClickListener {
            showImageSourceDialog() // 다이얼로그
            Toast.makeText(this@ProductActivity, " ${imageUri}", Toast.LENGTH_SHORT).show()
            imageUri?.let { it1 -> processImage(it1) }
        }

        // 툴바와 바텀 네비게이션 설정
        setupToolbar()
        setupBottomNavigation()

        // 버튼에 리스너 추가
        val washingMachineButton: ImageButton = findViewById(R.id.btn_washing_machine)
        washingMachineButton.setOnClickListener {
            Toast.makeText(this, "세탁기 선택", Toast.LENGTH_SHORT).show()
        }

        val cancelButton: Button = findViewById(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
        }
    }

    // 이미지 처리 후, 서버로 전송하는 함수
    private fun processImage(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. JSON 데이터 생성
//                val userRequestBody = createRequestBodyFromDTO(userDTO)

                // 2. 이미지 축소 및 MultipartBody.Part 생성
                val resizedBitmap = getResizedBitmap(uri, 200, 200) // 200x200 크기로 축소
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val profileImagePart = createMultipartBodyFromBytes(imageBytes)
                Log.d("lsy","profileImagePart 1" + profileImagePart)

                // 3. 서버로 전송
                uploadData(profileImagePart)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "Image processed successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadData(profileImage: MultipartBody.Part?) {
        // 레트로핏 통신 이용해서, 서버에 전달하기전에, 인터셉터 이용해서, 헤더에 토큰 달기.
        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)
        apiService = myApplication.getApiService()

//        val apiService = (applicationContext as MyApplication).networkService
        if (profileImage != null) {
            Log.d("lsy", "profileImage 2: " + profileImage.body.contentLength())
        }

        val call = apiService.predictImage(profileImage)
        call.enqueue(object : Callback<PredictionResult> {
            override fun onResponse(call: Call<PredictionResult>, response: Response<PredictionResult>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductActivity, "서버 전송 성공", Toast.LENGTH_SHORT).show()
                    Log.d("lsy","${response.body()}")
                    Log.d("lsy", "${response.body()?.predictedClassLabel}")
                    Log.d("lsy", "${response.body()?.confidence}")
                    val predictedClassLabel = "${response.body()?.predictedClassLabel}"
                    val confidence = response.body()?.confidence

                    Log.d("lsy","정확도2 :${confidence?.let { formatToPercentage(it) }} ")
                    val result = """
                        결과 : ${predictedClassLabel}
                        정확도 : ${confidence?.let { formatToPercentage(it) }}
                    """.trimIndent()

                    resultView.text = result
                } else {
                    Toast.makeText(this@ProductActivity, "Failed to create user: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                Toast.makeText(this@ProductActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createMultipartBodyFromBytes(imageBytes: ByteArray): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestFile)
    }

    // 사이즈 조절, 썸네일 이미지 압축
    private suspend fun getResizedBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@ProductActivity)
                .asBitmap()
                .load(uri)
                .override(width, height)  // 지정된 크기로 축소
                .submit()

            // Bitmap을 반환
            futureTarget.get()
        }
    }

    // 이미지 타입 , 비트맵 -> 바이트 단위로 변경.
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // 압축 품질을 80%로 설정
        return byteArrayOutputStream.toByteArray()
    }

    fun formatToPercentage(value: Double): String {
        // 값을 100으로 곱해서 퍼센트로 변환
        val percentageValue = value * 100

        // 소수점 둘째 자리까지 포맷
        val formattedValue = String.format("%.2f", percentageValue)

        // 퍼센트 기호 추가
        return "$formattedValue%"
    }
}

