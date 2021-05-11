package com.example.loadimage

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val TAG = "giangtd"
    private val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)

        if (!checkPermission()) {
            requestPermission()
        } else {
            lifecycleScope.launch {
//                for (item in FileManager.getListImage(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: image : " + item)
//                }
//                for (item in FileManager.getListAudio(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: audio: " + item)
//                }
//                for (item in FileManager.getListVideo(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: video: " + item)
//                }
                for (item in FileManager.getListDocument(this@MainActivity)) {
                    Log.d(TAG, "onCreate: doucument " + item)
                }

//                for (item in FileManager.getListApk(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: apk: " + item)
//                }
//                for (item in FileManager.getListDownload()) {
//                    Log.d(TAG, "onCreate: download: " + item)
//                }
//
//                for (item in FileManager.getListZip(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: zip: " + item)
//                }
//                for (item in FileManager.getListApp(this@MainActivity)) {
//                    Log.d(TAG, "onCreate: app: " + item)
//                }
//
//                for (item in FileManager.getListRecent(this@MainActivity, 100)) {
//                    Log.d(TAG, "onCreate: recent : " + item)
//                }
//
//                for (item in FileManager.getListScreenShots()) {
//                    Log.d(TAG, "onCreate: screen shot: " + item)
//                }

//            val urlIamge = getListImage().get(1)
//            val urlIamge = getListAudio().get(1)
//                val urlIamge = getDocumentList().get(0)

                var uri =
                    "content://com.android.externalstorage.documents/tree/9ABF-DA6C%3A".toUri()
//                var uri = "content://com.android.externalstorage.documents/tree/9ABF-DA6C%3AMusic".toUri()

//                val path = "/storage/emulated/0/FileTest2/tomato.txt"
                val path = "/storage/9ABF-DA6C/Music/apple/tt.txt"
                val pathDelete = "/storage/9ABF-DA6C/oranges.txt"
//                val outPath = "/storage/emulated/0/FileTest2/"
                val outPath = "/storage/9ABF-DA6C/"


                val delete: TextView = findViewById(R.id.delete)

                delete.setOnClickListener {
//                    Log.d(TAG, "onCreate: url: " + pathDelete)
//                    if (FileManager.deleteFile(this@MainActivity, path, uri)) {
//                        Log.d(TAG, "onCreate: true")
//                    } else {
//                        Log.d(TAG, "onCreate: false")
//                    }

                    val file = File("/storage/9ABF-DA6C/Music/apple")
//                    if (SDCardUtils.checkWritableRootPath(this@MainActivity, path)) {

                    if (file.exists()){
                        Log.d(TAG, "onCreate: yeeeeeeeee")
                    }else{
                        Log.d(TAG, "onCreate: nooooooooo")
                    }


//                    if (SDCardUtils.checkWritableRootPath(this@MainActivity, path)) {
//                        Log.d(TAG, "onCreate: yesssssssssss")
//                    } else {
//                        Log.d(TAG, "onCreate: noooooooooooo")
//                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
//
//                    }
                }


                val rename: TextView = findViewById(R.id.rename)
                rename.setOnClickListener {
                    Log.d(TAG, "onCreate: url: " + path)

                    if (FileManager.renameFile(this@MainActivity, path, "tomato.txt", uri)) {
                        Log.d(TAG, "onCreate: true")
                    } else {
                        Log.d(TAG, "onCreate: false")
                    }

                }

                val copy: TextView = findViewById(R.id.copy)
                copy.setOnClickListener {
                    Log.d(TAG, "onCreate: old: ")
                    if (FileManager.copyFile(this@MainActivity, path, outPath, uri)) {
                        Log.d(TAG, "onCreate: true")
                    } else {
                        Log.d(TAG, "onCreate: false")
                    }
                }

                val path1 = "/storage/emulated/0/FileTest2/123text.txt"
                val path2 = "/storage/9ABF-DA6C/"

                val move: TextView = findViewById(R.id.move)
                move.setOnClickListener {
                    if (FileManager.moveFile(this@MainActivity, path, outPath, uri)) {
                        Log.d(TAG, "onCreate: truee")
                    } else {
                        Log.d(TAG, "onCreate: falsee")
                    }
                }
            }
        }

    }


    // xin quyen

    val PERMISSION_REQUEST_CODE = 2296
    val SDCARD_ROOT_CODE = 1999

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    //    lateinit var uri: Uri

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return
//            uri = directoryUri
            Log.d(TAG, "onActivityResult: uri: " + directoryUri)
            contentResolver.takePersistableUriPermission(
                directoryUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun getRootPath(context: Context?, sdcardRootUri: Uri): File? {
        val pathSegments = sdcardRootUri.pathSegments
        val tokens = pathSegments[pathSegments.size - 1].split(":").toTypedArray()
        for (f in ContextCompat.getExternalFilesDirs(this, null)) {
            val path = f.absolutePath.substring(0, f.absolutePath.indexOf("/Android/"))
            if (path.contains(tokens[0])) {
                return File(path)
            }
        }
        return null
    }

}