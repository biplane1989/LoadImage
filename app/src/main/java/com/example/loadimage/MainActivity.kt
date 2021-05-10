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
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val TAG = "giangtd"
    private val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)

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

                val path = "/storage/emulated/0/file test/orange1.txt"
                val pathDelete = "/storage/9ABF-DA6C/orange1.txt"

                val delete: TextView = findViewById(R.id.delete)
                delete.setOnClickListener {
                    Log.d(TAG, "onCreate: url: " + path)
                    if (FileManager.deleteFile(this@MainActivity, pathDelete)) {
                        Log.d(TAG, "onCreate: true")
                    } else {
                        Log.d(TAG, "onCreate: false")
                    }

                }

                val rename: TextView = findViewById(R.id.rename)
                rename.setOnClickListener {
                    Log.d(TAG, "onCreate: url: " + path)

                    if (FileManager.renameFile(this@MainActivity, path, "orange1.txt")) {
                        Log.d(TAG, "onCreate: true")
                    } else {
                        Log.d(TAG, "onCreate: false")
                    }
                }

                val copy: TextView = findViewById(R.id.copy)
                copy.setOnClickListener {
                    Log.d(TAG, "onCreate: old: ")
                    if (FileManager.copyFile(this@MainActivity, path)) {
                        Log.d(TAG, "onCreate: true")
                    } else {
                        Log.d(TAG, "onCreate: false")
                    }
                }

                val path1 = "/storage/emulated/0/FileTest2/123text.txt"
                val path2 = "/storage/9ABF-DA6C/"

                val move: TextView = findViewById(R.id.move)
                move.setOnClickListener {
                    if (FileManager.moveFile(path1, path2)) {
                        Log.d(TAG, "onCreate: truee")
                    } else {
                        Log.d(TAG, "onCreate: falsee")
                    }
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

        val args =
            arrayOf(
                "%.pdf",
                "%.doc",
                "%.docx",
                "%.xls",
                "%.xlsx",
                "%.ppt",
                "%.pptx",
                "%.txt",
                "%.rtx",
                "%.rtf",
                "%.html"
            )
//        val clause = MediaStore.Files.FileColumns.DATA + " like ?"
//        val table = MediaStore.Files.getContentUri("external")
//        val column = arrayOf(MediaStore.Files.FileColumns.DATA)
//
//        val cursor: Cursor? = getContentResolver().query(table, column, clause, selectionArgs, null)

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
//        val args = arrayOf(pdf, doc, docx, xls, xlsx, ppt, pptx, txt, rtx, rtf, html)

        val clause = StringBuilder()

        args.forEachIndexed { index, elemment ->
            if (index == 0) {
                clause.append(MediaStore.Files.FileColumns.DATA + " like ?")
            } else {
                clause.append(" OR " + MediaStore.Files.FileColumns.DATA + " like ?")
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//            file?.let {
//                arrFile = it.listFiles().toCollection(ArrayList())
//            }
//        } else {
        val file =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        arrFile = file.listFiles().toCollection(ArrayList())
//        }

        for (i in arrFile.toList()) {
            if (!i.isDirectory) {
                downloads.add(i)
            }
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
        var arrFilePic = ArrayList<File>()
        var arrFileDCMI = ArrayList<File>()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val file = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            val screenshots = File(file, "Screenshots")
//            screenshots.listFiles()?.let {
//                arrFilePic = it.toCollection(ArrayList())
//            }
//        } else {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//            var screenShotPath = arrayOf(externalFilesDir)
        val screenshotsPic = File(file, "Screenshots")
        val screenshotsDCMI = File(file2, "Screenshots")

        Log.d(TAG, "getScreenShots: url: " + file.absolutePath)
        Log.d(TAG, "getScreenShots: url: " + file2?.absolutePath)
        Log.d(TAG, "getScreenShots: " + screenshotsPic.exists())
        if (screenshotsPic.exists()) {
            screenshotsPic.listFiles()?.let {
                arrFilePic = it.toCollection(ArrayList())
            }
        }
        if (screenshotsDCMI.exists()) {
            screenshotsDCMI.listFiles()?.let {
                arrFileDCMI = it.toCollection(ArrayList())
            }
        }
//        }
        for (i in arrFilePic) {
            if (!i.isDirectory) {
                screenShots.add(i.absolutePath)
            }
        }
        for (item in arrFileDCMI) {
            if (!item.isDirectory) {
                screenShots.add(item.absolutePath)
            }
        }
        screenShots
    }

    fun deleteImage(url: String): Boolean {
        val fdelete = File(url)
        Log.d(TAG, "deleteImage: file delete: " + fdelete.absolutePath)
        if (fdelete.exists()) {
            Log.d(TAG, "deleteImage: esxit")
            galleryAddPic(url)
            return fdelete.delete()
        } else {
            Log.d(TAG, "deleteImage: not esxits")
            return false
        }
    }

    private fun galleryAddPic(filePath: String) {
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val f = File(imagePath)
//        val contentUri = Uri.fromFile(f)
//        mediaScanIntent.data = contentUri
//        sendBroadcast(mediaScanIntent)

        val file = File(filePath)
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()),
            null, null
        )
    }

    fun renameFile(url: String, newName: String): Boolean {
        val oldFile = File(url)
        Log.d(TAG, "renameFile: old url: " + url)
        val newPath = url.substring(0, url.lastIndexOf("/")) + "/" + newName

        Log.d(TAG, "renameFile: new url: " + newPath)
        val newFile = File(newPath)
        if (oldFile.renameTo(newFile)) {
            galleryAddPic(newPath)
            return true
        }
        return false
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
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    lateinit var fileSD: File
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