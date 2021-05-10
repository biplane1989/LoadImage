package com.example.loadimage

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

object FileManager {

    val TAG = "giangtd"

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getListImage(context: Context): ArrayList<String> =
        withContext(Dispatchers.Default) {
            val images: ArrayList<String> = ArrayList<String>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val orderBy = MediaStore.Images.Media.DATE_TAKEN
            val cursor = context.contentResolver
                .query(uri, projection, null, null, "$orderBy DESC")
            try {
                cursor?.let {
                    var hasRow = it.moveToFirst()
                    while (hasRow) {
                        val absolutePathOfImage =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                        images.add(absolutePathOfImage)
                        hasRow = it.moveToNext()
                    }
                }
            } finally {
                cursor?.close()
            }
            images
        }

    suspend fun getListAudio(context: Context): ArrayList<String> =
        withContext(Dispatchers.Default) {
            val audios: ArrayList<String> = ArrayList<String>()

            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media._ID
            )
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            try {
                cursor?.let {
                    val clData = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                    val clDateAdded = it.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                    val clID = it.getColumnIndex(MediaStore.Audio.Media._ID)
                    var hasRow = it.moveToFirst()

                    while (hasRow) {
                        val filePath = it.getString(clData)
                        audios.add(filePath)
                        val id = it.getString(clID)

                        hasRow = it.moveToNext()
                    }
                }
            } finally {
                cursor?.close()
            }
            audios
        }

    suspend fun getListVideo(context: Context): ArrayList<String> =
        withContext(Dispatchers.Default) {
            val videos: ArrayList<String> = ArrayList<String>()

            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media._ID
            )
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            try {
                cursor?.let {
                    val clData = it.getColumnIndex(MediaStore.Video.Media.DATA)
                    val clDateAdded = it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                    val clID = it.getColumnIndex(MediaStore.Video.Media._ID)
                    var hasRow = it.moveToFirst()

                    while (hasRow) {
                        val filePath = it.getString(clData)
                        videos.add(filePath)
                        val id = it.getString(clID)
                        hasRow = it.moveToNext()
                    }
                }
            } finally {
                cursor?.close()
            }
            videos
        }

    suspend fun getListDocument(context: Context): ArrayList<String> =
        withContext(Dispatchers.Default) {
            val documents = ArrayList<String>()

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

            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(MediaStore.Files.FileColumns.DATA)

            val selection = StringBuilder()

            args.forEachIndexed { index, elemment ->
                if (index == 0) {
                    selection.append(MediaStore.Files.FileColumns.DATA + " like ?")
                } else {
                    selection.append(" OR " + MediaStore.Files.FileColumns.DATA + " like ?")
                }
            }

            val cursor: Cursor? =
                context.contentResolver.query(uri, projection, selection.toString(), args, null)

            try {
                cursor?.let {
                    val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val clDateAdded = it.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                    val clID = it.getColumnIndex(MediaStore.Files.FileColumns._ID)
                    var hasRow = it.moveToFirst()

                    while (hasRow) {
                        val filePath = it.getString(clData)
                        filePath.endsWith(".txt")  // lấy loại file
                        documents.add(filePath)
//                    val id = it.getString(clID)
                        hasRow = it.moveToNext()
                    }
                }
            } finally {
                cursor?.close()
            }
            documents
        }

    suspend fun getListApk(context: Context): ArrayList<String> = withContext(Dispatchers.Default) {
        val apks = ArrayList<String>()

        val selectionArgs = arrayOf("%.apk")
        val selection = MediaStore.Files.FileColumns.DATA + " like ?"
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)

        val cursor: Cursor? =
            context.getContentResolver().query(uri, projection, selection, selectionArgs, null)

        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    filePath.endsWith(".txt")  // lấy loại file

                    apks.add(filePath)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
        apks
    }

    suspend fun getListDownload(): ArrayList<File> = withContext(Dispatchers.Default) {
        val downloads = ArrayList<File>()

        val file =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val arrFile = file.listFiles().toCollection(ArrayList())

        for (i in arrFile.toList()) {
            if (!i.isDirectory) {
                downloads.add(i)
            }
        }
        downloads
    }

    suspend fun getListZip(context: Context): ArrayList<String> = withContext(Dispatchers.Default) {
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

        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = StringBuilder()

        selectionArgs.forEachIndexed { index, elemment ->
            if (index == 0) {
                selection.append(MediaStore.Files.FileColumns.DATA + " like ?")
            } else {
                selection.append(" OR " + MediaStore.Files.FileColumns.DATA + " like ?")
            }
        }

        val cursor: Cursor? =
            context.getContentResolver()
                .query(uri, projection, selection.toString(), selectionArgs, null)
        try {
            cursor?.let {
                val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var hasRow = it.moveToFirst()

                while (hasRow) {
                    val filePath = it.getString(clData)
                    archives.add(filePath)
                    hasRow = it.moveToNext()
                }
            }
        } finally {
            cursor?.close()
        }
        archives
    }

    @SuppressLint("QueryPermissionsNeeded")
    suspend fun getListApp(context: Context): ArrayList<String> = withContext(Dispatchers.Default) {
        val listApp = ArrayList<String>()
        val apps = context.packageManager.getInstalledApplications(0)
        for (app in apps) {
            listApp.add(app.processName)

            if (app.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0) {
                // It is a system app
            } else {
                // It is installed by the user
            }
        }
        listApp
    }

    suspend fun getListRecent(context: Context, number: Int): ArrayList<String> =
        withContext(Dispatchers.Default) {
            var index = 0
            val apks = ArrayList<String>()
            val sort = MediaStore.MediaColumns.DATE_ADDED + " DESC"
            val uri = MediaStore.Files.getContentUri("external")
            val projection =
                arrayOf(
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_MODIFIED
                )

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -7)
            val timeInMillis = calendar.timeInMillis / 1000

            val cursor: Cursor? = context.getContentResolver().query(
                uri,
                projection,
                "date_modified" + ">?",
                arrayOf("" + timeInMillis),
                sort
            )

            try {
                cursor?.let {
                    val clData = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    var hasRow = it.moveToFirst()

                    while (hasRow && index <= number) {
                        index++
                        val filePath = it.getString(clData)
//                    filePath.endsWith(".txt")  // lấy loại file
                        apks.add(filePath)
                        hasRow = it.moveToNext()
                    }
                }
            } finally {
                cursor?.close()
            }
            apks
        }

    suspend fun getListScreenShots(): ArrayList<String> = withContext(Dispatchers.Default) {
        val screenShots = ArrayList<String>()
        var arrFilePic = ArrayList<File>()
        var arrFileDCMI = ArrayList<File>()

        val filePic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val fileDCMI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val screenshotsPic = File(filePic, "Screenshots")
        val screenshotsDCMI = File(fileDCMI, "Screenshots")

        Log.d(TAG, "getScreenShots: url: " + filePic.absolutePath)
        Log.d(TAG, "getScreenShots: url: " + fileDCMI?.absolutePath)
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

    fun deleteFile(context: Context, url: String): Boolean {
        val fdelete = File(url)
        Log.d(TAG, "deleteImage: file delete: " + fdelete.absolutePath)
        if (fdelete.exists()) {
            Log.d(TAG, "deleteImage: esxit")
            if (fdelete.delete()) {
                galleryAddPic(context, url)
                return true
            } else {
                return false
            }
        } else {
            Log.d(TAG, "deleteImage: not esxits")
            return false
        }
    }

    private fun galleryAddPic(context: Context, filePath: String) {
        val file = File(filePath)
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            null, null
        )
    }

    fun renameFile(context: Context, url: String, newName: String): Boolean {
        val oldFile = File(url)
        Log.d(TAG, "renameFile: old url: " + url)
        val newPath = url.substring(0, url.lastIndexOf("/")) + "/" + newName

        Log.d(TAG, "renameFile: new url: " + newPath)
        val newFile = File(newPath)
        if (oldFile.renameTo(newFile)) {
            galleryAddPic(context, newPath)
            return true
        }
        return false
    }

    fun copyFile(context: Context, path: String): Boolean {
        val format = path.substring(path.lastIndexOf("."), path.length)
//        val newPath = path.substring(0, path.lastIndexOf(".")) + "copy" + format
        val fileName =
            path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) + "-copy" + format
        val newPath = path.substring(0, path.lastIndexOf("/")) + "/"
        Log.d(TAG, "copyFile: old path: " + path + "     name: " + fileName)
        if (copyNewFile(path, newPath)) {
            galleryAddPic(context, newPath)
            return true
        }
        return false
    }

    private fun copyNewFile(inputPath: String, outputPath: String): Boolean {

        val format = inputPath.substring(inputPath.lastIndexOf("."), inputPath.length)
        val newPath = inputPath.substring(0, inputPath.lastIndexOf(".")) + "copy" + format
        val newName =
            inputPath.substring(
                inputPath.lastIndexOf("/") + 1,
                inputPath.lastIndexOf(".")
            ) + " - copy" + format

        var input: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            input = FileInputStream(inputPath)
            out = FileOutputStream(outputPath + newName)
            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            input.close()
            input = null

            // write the output file (You have now copied the file)
            out.flush()
            out.close()
            out = null

            return true
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message!!)
        } catch (e: Exception) {
            Log.e("tag", e.message!!)
        }
        return false
    }

    fun moveFile(inputPath: String, outputPath: String): Boolean {

        val fileName = inputPath.substring(inputPath.lastIndexOf("/"), inputPath.length)

        var input: InputStream? = null
        var out: OutputStream? = null
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            input = FileInputStream(inputPath)
            out = FileOutputStream(outputPath + fileName)
            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            input.close()
            input = null

            // write the output file
            out.flush()
            out.close()
            out = null

            // delete the original file
            File(inputPath).delete()

            return true
        } catch (fnfe1: FileNotFoundException) {
            Log.e("tag", fnfe1.message!!)
        } catch (e: java.lang.Exception) {
            Log.e("tag", e.message!!)
        }
        return false
    }

}