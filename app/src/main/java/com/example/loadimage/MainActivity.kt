package com.example.loadimage

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

        lifecycleScope.launch {
            for (item in getListImage()) {
                Log.d(TAG, "onCreate: image : " + item)
            }
            for (item in getListAudio()) {
                Log.d(TAG, "onCreate: audio: " + item)
            }
            for (item in getListVideo()) {
                Log.d(TAG, "onCreate: video: " + item)
            }
            for (item in getDocumentList()) {
                Log.d(TAG, "onCreate: doucument " + item)
            }

            for (item in getApkList()) {
                Log.d(TAG, "onCreate: apk: " + item)
            }
            for (item in getDownload()) {
                Log.d(TAG, "onCreate: download: " + item)
            }

            for (item in getZip()) {
                Log.d(TAG, "onCreate: zip: " + item)
            }
            for (item in getApp()) {
                Log.d(TAG, "onCreate: app: " + item)
            }

            for (item in getRecent(100)) {
                Log.d(TAG, "onCreate: recent : " + item)
            }

            for (item in getScreenShots()) {
                Log.d(TAG, "onCreate: screen shot: " + item)
            }

//            val urlIamge = getListImage().get(1)
//            val urlIamge = getListAudio().get(1)
            val urlIamge = getDocumentList().get(0)

            val delete: TextView = findViewById(R.id.delete)
            delete.setOnClickListener {
                Log.d(TAG, "onCreate: url: "+ urlIamge)
                if (deleteImage(urlIamge)){
                    Log.d(TAG, "onCreate: true")
                }else{
                    Log.d(TAG, "onCreate: false")
                }
            }
        }

    }

    /*  @RequiresApi(Build.VERSION_CODES.Q)
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
  //        Log.d(TAG, "getListImageAndroidQ: list size : " + listUris.size)
          return listUris
      }*/

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getListImage(): ArrayList<String> = withContext(Dispatchers.Default) {
        val images: ArrayList<String> = ArrayList<String>()
        images.clear()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val cursor = applicationContext.contentResolver
            .query(uri, projection, null, null, "$orderBy DESC")
        try {
            cursor?.let {
                while (cursor.moveToNext()) {
                    val absolutePathOfImage =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                    Log.e("Column", absolutePathOfImage)
                    images.add(absolutePathOfImage)
                }
//                Log.d(TAG, "getListImage: list size : " + images.size)
            }
        } finally {
            cursor?.close()
        }
        images
    }

    /* fun getListAudioAndroidQ(): ArrayList<Uri> {
         Log.d(TAG, "getListImageAndroidQ: ")
         val listUris = ArrayList<Uri>()
         val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
         val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media._ID)

         val cursor = contentResolver.query(uri, projection, null, null, null)
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
     }*/

    suspend fun getListAudio(): ArrayList<String> = withContext(Dispatchers.Default) {
        val audios: ArrayList<String> = ArrayList<String>()
        audios.clear()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media._ID
        )
        val cursor = applicationContext.contentResolver.query(uri, projection, null, null, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val clID = it.getColumnIndex(MediaStore.Audio.Media._ID)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    audios.add(filePath)
                    val id = it.getString(clID)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "getListAudio: list size : " + audios.size)
        audios
    }

    suspend fun getListVideo(): ArrayList<String> = withContext(Dispatchers.Default) {
        val videos: ArrayList<String> = ArrayList<String>()
        videos.clear()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media._ID
        )
        val cursor = applicationContext.contentResolver.query(uri, projection, null, null, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Video.Media.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val clID = it.getColumnIndex(MediaStore.Video.Media._ID)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    videos.add(filePath)
                    val id = it.getString(clID)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "getListAudio: list size : " + videos.size)
        videos
    }

    suspend fun getDocumentList(): ArrayList<String> = withContext(Dispatchers.Default) {
        val documents = ArrayList<String>()

        val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc")
        val docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx")
        val xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls")
        val xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx")
        val ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt")
        val pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx")
        val txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt")
        val rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx")
        val rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf")
        val html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html")

        //Table
        val table = MediaStore.Files.getContentUri("external")
        //Column
        //Column
        val column = arrayOf(MediaStore.Files.FileColumns.DATA)
        //Where
//        val where = (MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?")
        //args
        val args = arrayOf(pdf, doc, docx, xls, xlsx, ppt, pptx, txt, rtx, rtf, html)

        val clause = StringBuilder()

        args.forEachIndexed { index, elemment ->
            if (index == 0) {
                clause.append(MediaStore.Files.FileColumns.MIME_TYPE + " like ?")
            } else {
                clause.append(" OR " + MediaStore.Files.FileColumns.MIME_TYPE + " like ?")
            }
        }

        val cursor: Cursor? = contentResolver.query(table, column, clause.toString(), args, null)

        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val clDateAdded = it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                val clID = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                var hasRow = it.moveToFirst()


                while (hasRow) {
                    val filePath = it.getString(clData)
                    filePath.endsWith(".txt")  // lấy loại file
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    documents.add(filePath)
//                    val id = it.getString(clID)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "getDocumentList: list size: " + documents.size)
        documents
    }

    suspend fun getApkList(): ArrayList<String> = withContext(Dispatchers.Default) {
        val apks = ArrayList<String>()

        val selectionArgs = arrayOf("%.apk")
        val clause = MediaStore.Files.FileColumns.DATA + " like ?"
        val table = MediaStore.Files.getContentUri("external")
        val column = arrayOf(MediaStore.Files.FileColumns.DATA)

        val cursor: Cursor? = getContentResolver().query(table, column, clause, selectionArgs, null)

        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    filePath.endsWith(".txt")  // lấy loại file
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    apks.add(filePath)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "get apk list size: " + apks.size)
        apks
    }

    suspend fun getDownload(): ArrayList<File> = withContext(Dispatchers.Default) {
        val downloads = ArrayList<File>()
        var arrFile = ArrayList<File>()
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//        val file = File(path)
//        val filesInDirectory = file.listFiles().toList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            file?.let {
                arrFile = it.listFiles().toCollection(ArrayList())
            }
        } else {
            val file =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            arrFile = file.listFiles().toCollection(ArrayList())
        }

        for (i in arrFile.toList()) {
            downloads.add(i)
//            Log.d(TAG, "getDownload: name: " + i.absolutePath)
        }
        downloads
    }

    suspend fun getZip(): ArrayList<String> = withContext(Dispatchers.Default) {
        val archives = ArrayList<String>()
        val selectionArgs = arrayOf(
            "%.r__",
            "%.a__",
            "%.z__",
            "%.zipx",
            "%.jar",
            "%.7z",
            "%.gz",
            "%.tgz",
            "%.bz2",
            "%.bz",
            "%.tbz",
            "%.tbz2",
            "%.xz",
            "%.txz",
            "%.lz",
            "%.tlz",
            "%.tar",
            "%.iso",
            "%.lzh",
            "%.lha",
            "%.z",
            "%.taz",
            "%.001"
        )

        val table = MediaStore.Files.getContentUri("external")
        val column = arrayOf(MediaStore.Files.FileColumns.DATA)
        val clause = StringBuilder()

        selectionArgs.forEachIndexed { index, elemment ->
            if (index == 0) {
                clause.append(MediaStore.Files.FileColumns.DATA + " like ?")
            } else {
                clause.append(" OR " + MediaStore.Files.FileColumns.DATA + " like ?")
            }
        }

        val cursor: Cursor? =
            getContentResolver().query(table, column, clause.toString(), selectionArgs, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    archives.add(filePath)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "get apk list size: " + archives.size)
        archives
    }

    @SuppressLint("QueryPermissionsNeeded")
    suspend fun getApp(): ArrayList<String> = withContext(Dispatchers.Default) {
        val listApp = ArrayList<String>()
        val apps = packageManager.getInstalledApplications(0)
        for (app in apps) {
            listApp.add(app.processName)
//            Log.d(TAG, "getApp: app name: " + app.processName)
            if (app.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0) {
                // It is a system app
            } else {
                // It is installed by the user
            }
        }

//        Log.d(TAG, "getApp: list size: " + listApp.size)
        listApp
    }

    suspend fun getRecent(number: Int): ArrayList<String> = withContext(Dispatchers.Default) {
        var index = 0
        val apks = ArrayList<String>()
        val SORT = MediaStore.MediaColumns.DATE_ADDED + " DESC"
        val table = MediaStore.Files.getContentUri("external")
        val column =
            arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DATE_MODIFIED)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -7)
        val timeInMillis = calendar.timeInMillis / 1000

        val cursor: Cursor? = getContentResolver().query(
            table,
            column,
            "date_modified" + ">?",
            arrayOf("" + timeInMillis),
            SORT
        )

        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var hasRow = it.moveToFirst()

                while (hasRow && index <= number) {
                    index++
                    val filePath = it.getString(clData)
                    filePath.endsWith(".txt")  // lấy loại file
//                    Log.d(TAG, "queryMediaStore: $filePath")
                    apks.add(filePath)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
//        Log.d(TAG, "get apk list size: " + apks.size)
        apks
    }


    suspend fun getScreenShots(): ArrayList<String> = withContext(Dispatchers.Default) {
        var screenShots = ArrayList<String>()
        var arrFile = ArrayList<File>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val screenshots = File(file, "Screenshots")
            arrFile = screenshots.listFiles().toCollection(ArrayList())
        } else {
            val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val screenshots = File(file, "Screenshots")
            var arrFile = screenshots.listFiles().toCollection(ArrayList())
        }
        for (i in arrFile) {
//            Log.d(TAG, "getDownload: name: " + i.absolutePath)
            screenShots.add(i.absolutePath)
        }
        screenShots
    }

     fun deleteImage(url: String): Boolean  {
        val fdelete = File(url)
        if (fdelete.exists()) {
            Log.d(TAG, "deleteImage: esxit")
            galleryAddPic(url)
            return fdelete.delete()
        } else {
            Log.d(TAG, "deleteImage: not esxits")
            return false
        }
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }

    fun renameFile(newName: String){
        val sdcard = Environment.getExternalStorageDirectory()
        val from = File(sdcard, "from.txt")
        val to = File(sdcard, "to.txt")
        from.renameTo(to)
    }

}