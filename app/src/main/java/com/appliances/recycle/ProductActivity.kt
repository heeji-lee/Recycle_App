package com.appliances.recycle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.FileProvider
import com.appliances.recycle.databinding.ActivityProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class ProductActivity : BaseActivity() { // BaseActivity를 상속

    private lateinit var imageView: ImageView
    private lateinit var resultView: TextView
    private var imageUri: Uri? = null
    private val cameraRequestCode = 1
    private lateinit var cameraImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        enableEdgeToEdge()

        val binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.btnPhoto
        resultView = binding.predictResultView

        binding.btnPhoto.setOnClickListener {
            showImageSourceDialog()
            imageUri?.let { uri -> processImage(uri) }
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

    // 이미지 처리 및 서버 전송 함수 (간략화)
    private fun processImage(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 이미지 처리 로직
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "Image processed successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
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
        builder.setContentView(view)
        builder.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                cameraImageUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                startActivityForResult(cameraIntent, cameraRequestCode)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, cameraRequestCode)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
