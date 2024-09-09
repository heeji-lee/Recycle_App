package com.appliances.recycle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.appliances.recycle.databinding.ActivityProductBinding
import com.appliances.recycle.dto.PredictionResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sylovestp.firebasetest.testspringrestapp.retrofitN.INetworkService
import com.sylovestp.firebasetest.testspringrestapp.retrofitN.MyApplication
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
    private lateinit var networkService: INetworkService
    private lateinit var imageView: ImageView
    private lateinit var resultView: TextView
    private var imageUri: Uri? = null  // Nullable URI

    private val cameraRequestCode = 1
    private val REQUEST_PERMISSION = 1001
    private lateinit var cameraImageUri: Uri

    // 권한 체크 및 요청
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 31 이상: READ_MEDIA_IMAGES 권한을 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_PERMISSION)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // API 29 이상: WRITE_EXTERNAL_STORAGE 권한 필요하지 않음, READ_EXTERNAL_STORAGE만 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            }
        } else {
            // API 28 이하: WRITE_EXTERNAL_STORAGE 권한을 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            }
        }

        // 카메라 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한이 부여되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


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
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
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
        } ?: run {
            Toast.makeText(this, "파일 생성 실패", Toast.LENGTH_SHORT).show()
        }
    }
//    private fun openCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (cameraIntent.resolveActivity(packageManager) != null) {
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    null
//            }
//            photoFile?.let {
//                cameraImageUri = FileProvider.getUriForFile(
//                    this,
//                    "${applicationContext.packageName}.provider",
//                    it
//                )
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
//                selectImageLauncher.launch(cameraIntent)
//            }
//            }
//        } else {
//            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

    // 갤러리 열기
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        } else {
            Toast.makeText(this, "저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
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
            // 이미지 로드
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.btnPhoto
        resultView = binding.predictResultView


        checkPermissions()
        val myApplication = applicationContext as MyApplication
        networkService = myApplication.networkService  // 인증이 필요 없는 API 사용

        // 예시: 네트워크 요청 수행

        binding.btnPhoto.setOnClickListener {
            showImageSourceDialog() // 다이얼로그
            Toast.makeText(this@ProductActivity, " ${imageUri}", Toast.LENGTH_SHORT).show()
            imageUri?.let { it1 -> processImage(it1) }
        }


        // 툴바와 바텀 네비게이션 설정
//        setupToolbar()
//        setupBottomNavigation()

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
        if (uri == null) {
            Toast.makeText(this, "이미지 URI가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. JSON 데이터 생성
//                val userRequestBody = createRequestBodyFromDTO(userDTO)

                // 2. 이미지 축소 및 MultipartBody.Part 생성
                val resizedBitmap = getResizedBitmap(uri, 200, 200) // 200x200 크기로 축소
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val profileImagePart = createMultipartBodyFromBytes(imageBytes)
                Log.d("lsy","profileImagePart 1" + profileImagePart)

//                // 3. 서버로 전송
//                uploadData(profileImagePart)
//
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ProductActivity, "Image processed successfully", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ProductActivity, "Error processing image", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
                // Retrofit 호출


                    val call = networkService.predictImage(profileImagePart)

                Log.d("lsy", "이미지가 들어가고 있니?" + profileImagePart)
                call.enqueue(object : Callback<String> {  // 이미지 ID를 String으로 받음
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            val imageId = response.body() ?: ""

                            Log.d("Upload", "이미지 ID: $imageId")

                            // 3. 이미지 ID로 분류 요청
                            classifyImage(uri)
                        } else {
                            Log.e("Upload", "서버 오류: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("Upload", "업로드 실패: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "이미지 처리 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // 이미지 ID를 기반으로 이미지 분류 요청 함수
    private fun classifyImage(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. 이미지 파일을 비트맵으로 변환 후, MultipartBody.Part로 전송 준비
                val resizedBitmap = getResizedBitmap(uri, 200, 200)
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val imagePart = createMultipartBodyFromBytes(imageBytes)

                // 2. Retrofit을 통해 이미지 전송 및 분류 요청
                val classifyCall = networkService.classifyImage(imagePart)
                classifyCall.enqueue(object : Callback<PredictionResult> {
                    override fun onResponse(call: Call<PredictionResult>, response: Response<PredictionResult>) {
                        if (response.isSuccessful) {
                            // 서버로부터 받은 분류 결과 처리
                            val predictionResult = response.body()
                            val predictedClassLabel = predictionResult?.predictedClassLabel ?: "Unknown"
                            val confidence = predictionResult?.confidence ?: 0.0

                            val resultText = """
                            분류 결과 : $predictedClassLabel
                            정확도 : ${formatToPercentage(confidence)}
                        """.trimIndent()

                            runOnUiThread {
                                resultView.text = resultText
                                Toast.makeText(this@ProductActivity, "분류 완료: $predictedClassLabel", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("Classify", "분류 서버 오류: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                        Log.e("Classify", "분류 요청 실패: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "이미지 분류 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


//    private fun uploadData(profileImage: MultipartBody.Part?) {
//        // 레트로핏 통신 이용해서, 서버에 전달하기전에, 인터셉터 이용해서, 헤더에 토큰 달기.
//        val myApplication = applicationContext as MyApplication
//        myApplication.initialize(this)
//        apiService = myApplication.getApiService()
//
////        val apiService = (applicationContext as MyApplication).networkService
//        if (profileImage != null) {
//            Log.d("lsy", "profileImage 2: " + profileImage.body.contentLength())
//        }
//
//        val call = apiService.predictImage(profileImage)
//        call.enqueue(object : Callback<PredictionResult> {
//            override fun onResponse(call: Call<PredictionResult>, response: Response<PredictionResult>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@ProductActivity, "서버 전송 성공", Toast.LENGTH_SHORT).show()
//                    Log.d("lsy","${response.body()}")
//                    Log.d("lsy", "${response.body()?.predictedClassLabel}")
//                    Log.d("lsy", "${response.body()?.confidence}")
//                    val predictedClassLabel = "${response.body()?.predictedClassLabel}"
//                    val confidence = response.body()?.confidence
//
//                    Log.d("lsy","정확도2 :${confidence?.let { formatToPercentage(it) }} ")
//                    val result = """
//                        결과 : ${predictedClassLabel}
//                        정확도 : ${confidence?.let { formatToPercentage(it) }}
//                    """.trimIndent()
//
//                    resultView.text = result
//                } else {
//                    Toast.makeText(this@ProductActivity, "Failed to create user: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
//                Toast.makeText(this@ProductActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun createMultipartBodyFromBytes(imageBytes: ByteArray): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestFile)
    }

    // 사이즈 조절, 썸네일 이미지 압축
    private suspend fun getResizedBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@ProductActivity)
                .asBitmap()
                .load(uri)
                .override(width, height)  // 지정된 크기로 축소
                .submit()

            // Bitmap을 반환
            futureTarget.get()
        } catch (e: Exception) {
            Log.e("ImageProcessing", "Error resizing image: ${e.message}")
            throw e  // 오류 발생 시 예외를 던짐
        }
    }
}

    // 이미지 타입 , 비트맵 -> 바이트 단위로 변경.
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // 압축 품질을 80%로 설정
        return byteArrayOutputStream.toByteArray()
    }

    private fun formatToPercentage(value: Double): String {
        val percentageValue = value * 100
        return String.format("%.2f", percentageValue) + "%"
    }
}

