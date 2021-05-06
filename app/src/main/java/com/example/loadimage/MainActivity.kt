package com.example.loadimage

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val TAG = "giangtd"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Log.d(TAG, "onCreate: qqqqqqqqq")
//            for (item in getListImageAndroidQ()) {
//                Log.d(TAG, "url image: " + item)
//            }
//
//        } else {
//            Log.d(TAG, "onCreate: <<<<<<<<<")
//            for (item in getListImage()) {
//                Log.d(TAG, "url image: " + item)
//            }
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            for (item in getListAudioAndroidQ()) {
//                Log.d(TAG, "url audio: " + item)
//            }
//        } else {
//            for (item in getListAudio()) {
//                Log.d(TAG, "url audio: " + item)
//            }
//        }

        getListVideo()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getListImageAndroidQ(): ArrayList<Uri> {
        Log.d(TAG, "getListImageAndroidQ: ")
        val listUris = ArrayList<Uri>()
        val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                )
                listUris.add(contentUri)
            }
        }
        Log.d(TAG, "getListImageAndroidQ: list size : " + listUris.size)
        return listUris
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getListImage(): ArrayList<String> {
        val images: ArrayList<String> = ArrayList<String>()
        images.clear()
        val uri: Uri
        val cursor: Cursor?
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        cursor = applicationContext.contentResolver
                .query(uri, projection, null, null, "$orderBy DESC")
        while (cursor!!.moveToNext()) {
            absolutePathOfImage =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            Log.e("Column", absolutePathOfImage)
            images.add(absolutePathOfImage)
        }
        Log.d(TAG, "getListImage: list size : " + images.size)
        return images
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getListAudioAndroidQ(): ArrayList<Uri> {
        Log.d(TAG, "getListImageAndroidQ: ")
        val listUris = ArrayList<Uri>()
        val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media._ID)

        val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        )
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val clID = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    val id = it.getLong(clID)

                    val contentUri = Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id.toString()
                    )
                    listUris.add(contentUri)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }

        Log.d(TAG, "getListImageAndroidQ: list size : " + listUris.size)
        return listUris
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getListAudio(): ArrayList<String> {
        val audios: ArrayList<String> = ArrayList<String>()
        audios.clear()
        val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media._ID)
        val cursor = applicationContext.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val clID = it.getColumnIndex(MediaStore.Audio.Media._ID)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    Log.d(TAG, "queryMediaStore: $filePath")
                    audios.add(filePath)
                    val id = it.getString(clID)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
        Log.d(TAG, "getListAudio: list size : " + audios.size)
        return audios
    }


    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getListVideo(): ArrayList<String> {
        val videos: ArrayList<String> = ArrayList<String>()
        videos.clear()
        val projection = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media._ID)
        val cursor = applicationContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Video.Media.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val clID = it.getColumnIndex(MediaStore.Video.Media._ID)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    Log.d(TAG, "queryMediaStore: $filePath")
                    videos.add(filePath)
                    val id = it.getString(clID)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
        Log.d(TAG, "getListAudio: list size : " + videos.size)
        return videos
    }

}