package com.example.a2024110518

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.InputStream

/**
 * 第三个二级 Activity：通过隐式 Intent 调起系统相机拍照，
 * 使用 FileProvider 提供全尺寸照片的输出 URI，拍照成功后显示照片。
 */
class ThirdActivity : AppCompatActivity() {

    private lateinit var ivPhoto: ImageView
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        ivPhoto = findViewById(R.id.ivPhoto)
        findViewById<Button>(R.id.btnCapture).setOnClickListener { capturePhoto() }

        // 相机 App 可能导致本页面被回收，恢复已生成的照片 URI
        savedInstanceState?.getString(STATE_PHOTO_URI)?.let {
            photoUri = Uri.parse(it)
            ivPhoto.setImageBitmap(decodeSampledBitmap(it))
        }
    }

    private fun capturePhoto() {
        // 1. 生成照片将要写入的文件
        val photoFile = File(getExternalFilesDir("photos"), "IMG_${System.currentTimeMillis()}.jpg")

        // 2. 通过 FileProvider 得到 content:// URI
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)

        // 3. 构造隐式 Intent 调起系统相机
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "未找到可用的相机应用", Toast.LENGTH_SHORT).show()
            return
        }

        photoUri = uri
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val uri = photoUri
            if (uri != null) {
                ivPhoto.setImageBitmap(decodeSampledBitmap(uri.toString()))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        photoUri?.let { outState.putString(STATE_PHOTO_URI, it.toString()) }
    }

    /** 从 URI 解码位图，并按倍率降采样，避免大图 OOM。 */
    private fun decodeSampledBitmap(uriString: String): Bitmap? {
        val uri = Uri.parse(uriString)

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        openStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds, 1024, 1024)
        }
        return openStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    private fun openStream(uri: Uri): InputStream? =
        runCatching { contentResolver.openInputStream(uri) }.getOrNull()

    private fun calculateInSampleSize(bounds: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = bounds.outHeight to bounds.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val STATE_PHOTO_URI = "state_photo_uri"
    }
}
