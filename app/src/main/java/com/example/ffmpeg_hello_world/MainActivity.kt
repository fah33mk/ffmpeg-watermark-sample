package com.example.ffmpeg_hello_world

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import com.example.ffmpeg_hello_world.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val storageHelper = SimpleStorageHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.selectVideo.setOnClickListener {
            storageHelper.openFilePicker(22)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (android.os.Environment.isExternalStorageManager().not())
                storageHelper.storage.requestFullStorageAccess()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                1
            )
        }
        storageHelper.onFileSelected = { _, files ->
            val path = files[0].getAbsolutePath(this)
            val newPath = path.substring(
                0,
                path.lastIndexOf(".")
            ) + "-Copy" + path.substring(path.lastIndexOf("."))
            val loading = Loading().show(this, true, "Please wait...")
            FFMpegWatermarkUtil.addWatermark(this, path, newPath) {
                loading.cancel()
                openVideo(newPath)
            }
        }
    }

    private fun openVideo(newPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(newPath), "video/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Handle case where no video player app is available
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        storageHelper.onRequestPermissionsResult(
            requestCode,
            permissions as Array<String>, grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        storageHelper.storage.onActivityResult(requestCode, resultCode, data)
    }
}