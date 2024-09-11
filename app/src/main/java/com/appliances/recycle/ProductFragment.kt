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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appliances.recycle.adapter.ItemAdapter
import com.appliances.recycle.databinding.FragmentProductBinding
import com.appliances.recycle.dto.ItemDTO
import com.appliances.recycle.dto.PredictionResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.appliances.recycle.retrofit.INetworkService
import com.appliances.recycle.retrofit.MyApplication
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

class ProductFragment : Fragment() {

    private lateinit var networkService: INetworkService
    private lateinit var imageView: ImageView
    private lateinit var resultView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private var imageUri: Uri? = null  // Nullable URI
    private var predictionResult: PredictionResult? = null  // 변수를 클래스 멤버로 선언
    private var isUploading = false  // 중복 업로드 방지

        private val REQUEST_PERMISSION = 1001
    private lateinit var cameraImageUri: Uri
    private var actionAfterPermission: (() -> Unit)? = null // 권한 후에 실행할 액션 저장
    private var itemDTOList: MutableList<ItemDTO> = mutableListOf() // 서버에서 받아온 아이템 목록을 저장할 리스트

    // 권한 체크 및 요청
    private fun checkPermissions(action: () -> Unit): Boolean {
        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }

        return if (permissionsNeeded.isNotEmpty()) {
            actionAfterPermission = action // 권한 부여 후 수행할 동작을 저장
            ActivityCompat.requestPermissions(requireActivity(), permissionsNeeded.toTypedArray(), REQUEST_PERMISSION)
            false
        } else {
            true
        }
    }


    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "모든 권한이 부여되었습니다.", Toast.LENGTH_SHORT).show()
                actionAfterPermission?.invoke() // 권한이 부여되면 저장된 액션을 실행
            } else {
                Toast.makeText(requireContext(), "필수 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 이미지 선택 후 처리하는 ActivityResultLauncher
    private val selectImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data ?: cameraImageUri

            // 이미지가 선택되었을 때만 processImage 호출
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .into(imageView)
                processImage(imageUri!!)
            } else {
                Toast.makeText(requireContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 이미지 선택 방법을 묻는 다이얼로그 표시
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")

        val builder = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_image_source, null)
        val listView = view.findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)
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
        if (!checkPermissions { openCamera() }) return

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

        photoFile?.let {
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().applicationContext.packageName}.provider",
                it
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            selectImageLauncher.launch(cameraIntent)
        } ?: run {
            Toast.makeText(requireContext(), "파일 생성 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리 열기
    private fun openGallery() {
        if (!checkPermissions { openGallery() }) return

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }


    // 이미지 파일 생성 (임시 파일 생성, 앱의 외부 저장소 저장)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View 바인딩 설정
        val binding = FragmentProductBinding.inflate(inflater, container, false)
        // 테이블 레이아웃 초기화
        recyclerView = binding.recyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Edge-to-Edge 활성화
        requireActivity().window.decorView

        // View 요소 초기화
        imageView = view.findViewById(R.id.btn_photo)
        resultView = view.findViewById(R.id.predict_result_view)

        itemAdapter = ItemAdapter(itemDTOList) { item -> deleteItem(item) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = itemAdapter

        // 권한 확인
        checkPermissions{}

        // ApplicationContext에서 네트워크 서비스 초기화
        val myApplication = requireActivity().applicationContext as MyApplication
        networkService = myApplication.networkService  // 인증이 필요 없는 API 사용

        // 버튼 클릭 이벤트 설정
        view.findViewById<ImageView>(R.id.btn_photo).setOnClickListener {
            showImageSourceDialog() // 이미지 소스 선택 다이얼로그 호출
        }


//        view.findViewById<Button>(R.id.btn_add_item).setOnClickListener {
//            if (predictionResult?.predictedClassLabel != null) {
//                addMatchingItemsToRecyclerView()
//            } else {
//                Toast.makeText(requireContext(), "먼저 이미지를 등록하세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
//            // 테이블에서 모든 아이템 삭제
//            clearItems()
//            Toast.makeText(requireContext(), "모든 항목이 취소되었습니다.", Toast.LENGTH_SHORT).show()
//        }

        // 아이템 목록 불러오기
        getAllItemsFromServer()
    }
    // 아이템 목록을 서버에서 가져오는 함수
    private fun getAllItemsFromServer() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = networkService.getAllItems().execute()
                if (response.isSuccessful) {
                    itemDTOList = response.body() ?: mutableListOf()
                    withContext(Dispatchers.Main) {
                        itemAdapter.notifyDataSetChanged() // 아이템 목록 갱신
                    }
                } else {
                    Log.e("API", "서버 오류: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "아이템 목록을 불러오는 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // 아이템 목록을 테이블에 추가하는 함수
    private fun addMatchingItemsToRecyclerView() {
        val predictedLabel = predictionResult?.predictedClassLabel
        val matchingItems = itemDTOList.filter { it.iname == predictedLabel }

        if (matchingItems.isNotEmpty()) {
            itemAdapter.submitList(matchingItems)  // 어댑터에 필터링된 목록 전달
        }
    }
    // 테이블에서 아이템을 삭제하는 함수
    private fun deleteItem(item: ItemDTO) {
        itemAdapter.deleteItem(item) // 어댑터에서 아이템 삭제
    }
    private fun clearItems() {
        itemDTOList.clear() // 아이템 목록 초기화
        itemAdapter.notifyDataSetChanged() // RecyclerView 갱신
        Toast.makeText(requireContext(), "모든 항목이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }



    // 이미지 처리 후, 서버로 전송하는 함수
    private fun processImage(uri: Uri) {
        if (isUploading) return // 중복 업로드 방지
        isUploading = true
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 비트맵 처리
                val resizedBitmap = getResizedBitmap(uri, 300, 300)
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val imageName = uri.lastPathSegment?.substringAfterLast("/")?.substringBeforeLast(".") ?: "default_image"
                val profileImagePart = createMultipartBodyFromBytes(imageBytes, imageName)

                // Retrofit 호출
                val call = networkService.predictImage(profileImagePart)
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            classifyImage(uri)
                        } else {
                            Log.e("Upload", "서버 오류: ${response.code()}")
                        }
                        isUploading = false
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("Upload", "업로드 실패: ${t.message}")
                        isUploading = false
                    }
                })
            } catch (e: Exception) {
                isUploading = false
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "이미지 처리 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 이미지 ID를 기반으로 이미지 분류 요청 함수
    private fun classifyImage(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resizedBitmap = getResizedBitmap(uri, 300, 300)
                val imageBytes = bitmapToByteArray(resizedBitmap)
                val imageName = uri.lastPathSegment?.substringAfterLast("/")?.substringBeforeLast(".") ?: "default_image"
                val imagePart = createMultipartBodyFromBytes(imageBytes, imageName)

                val classifyCall = networkService.classifyImage(imagePart)
                classifyCall.enqueue(object : Callback<PredictionResult> {
                    override fun onResponse(call: Call<PredictionResult>, response: Response<PredictionResult>) {
                        if (response.isSuccessful) {
                            predictionResult = response.body()
                            showClassificationResultDialog(predictionResult?.predictedClassLabel ?: "Unknown")
                        } else {
                            val resultText = """
                                분류 결과 : ${predictionResult?.predictedClassLabel ?: "Unknown"}
                                정확도 : ${formatToPercentage(predictionResult?.confidence ?: 0.0)}
                            """.trimIndent()

                            requireActivity().runOnUiThread {
                                resultView.text = resultText
                                Toast.makeText(requireContext(), "분류 완료", Toast.LENGTH_SHORT).show()
                                Log.d("lsy", " 분류 결과 : ${predictionResult?.predictedClassLabel}") }
                        }
                            Log.e("Classify", "분류 서버 오류: ${response.code()}")

                    }

                    override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                        Log.e("Classify", "분류 요청 실패: ${t.message}")
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "분류 실패: 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "이미지 분류 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // 분류 결과 팝업 표시
    private fun showClassificationResultDialog(result: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_classification_result, null)

        val resultTextView = view.findViewById<TextView>(R.id.resultTextView2)
        resultTextView.text = "분류 결과: $result. 이 결과가 맞습니까?"

        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val noButton = view.findViewById<Button>(R.id.noButton)

        yesButton.setOnClickListener {
            addMatchingItemsToRecyclerView()
            dialog.dismiss()
        }

        noButton.setOnClickListener {
            showSelectionButtons()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
    // '아니요' 선택 후 6개의 이미지 버튼을 출력하는 함수
    private fun showSelectionButtons() {
        val selectionDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_item_selection, null)

        // 6개의 이미지 버튼
//        val fridgeButton = view.findViewById<ImageButton>(R.id.btn_refrigerator2)
//        val airConditionerButton = view.findViewById<ImageButton>(R.id.btn_air_conditioner2)
//        val computerButton = view.findViewById<ImageButton>(R.id.btn_computer2)
//        val tvButton = view.findViewById<ImageButton>(R.id.btn_tv2)
//        val ovenButton = view.findViewById<ImageButton>(R.id.btn_oven2)
//        val washingMachineButton = view.findViewById<ImageButton>(R.id.btn_washing_machine2)
//
//        // 각 버튼 클릭 이벤트 설정
//        fridgeButton.setOnClickListener {
//            showSubItemSelectionDialog("냉장고")
//            selectionDialog.dismiss()
//        }
//
//        airConditionerButton.setOnClickListener {
//            showSubItemSelectionDialog("에어컨")
//            selectionDialog.dismiss()
//        }
//
//        computerButton.setOnClickListener {
//            showSubItemSelectionDialog("컴퓨터(부품)")
//            selectionDialog.dismiss()
//        }
//
//        // 나머지 버튼들은 바로 해당 제품의 정보를 가져와 등록
//        tvButton.setOnClickListener {
//            fetchAndDisplayItem("TV")
//            selectionDialog.dismiss()
//        }
//
//        ovenButton.setOnClickListener {
//            fetchAndDisplayItem("전자레인지")
//            selectionDialog.dismiss()
//        }
//
//        washingMachineButton.setOnClickListener {
//            fetchAndDisplayItem("세탁기")
//            selectionDialog.dismiss()
//        }

        selectionDialog.setContentView(view)
        selectionDialog.show()
    }

    // 하위 항목을 선택할 수 있는 다이얼로그
    private fun showSubItemSelectionDialog(itemCategory: String) {
        val subItemDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_sub_item_selection, null)
        val listView = view.findViewById<ListView>(R.id.listView)

        // 예시 하위 항목 데이터 설정
        val subItems = when (itemCategory) {
            "냉장고" -> listOf("양문형 냉장고", "일반 냉장고", "미니 냉장고")
            "에어컨" -> listOf("스탠드 에어컨", "벽걸이 에어컨", "창문형 에어컨")
            "컴퓨터(부품)" -> listOf("CPU", "GPU", "RAM")
            else -> emptyList()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subItems)
        listView.adapter = adapter

        // 하위 항목 선택 시
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedSubItem = subItems[position]
            fetchAndDisplayItem(selectedSubItem)  // 선택된 하위 항목에 맞는 제품 정보 가져오기
            subItemDialog.dismiss()
        }

        subItemDialog.setContentView(view)
        subItemDialog.show()
    }

    // 제품 정보 가져오기
    private fun fetchAndDisplayItem(iname: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = networkService.getAllItems().execute()
                if (response.isSuccessful) {
                    val itemDTOList = response.body() ?: return@launch
                    val matchingItems = itemDTOList.filter { it.iname == iname }
                    if (matchingItems.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            itemAdapter.submitList(matchingItems)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "아이템 목록을 불러오는 중 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createMultipartBodyFromBytes(imageBytes: ByteArray, imageName: String): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        return MultipartBody.Part.createFormData("image", "$imageName.jpg", requestFile)
    }

    // 사이즈 조절, 썸네일 이미지 압축
    private suspend fun getResizedBitmap(uri: Uri, width: Int, height: Int): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
                val futureTarget: FutureTarget<Bitmap> = Glide.with(requireActivity())
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

    // 이미지 타입 , 비트맵 -> 바이트 단위로 변경
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
