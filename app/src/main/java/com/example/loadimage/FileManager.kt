package com.example.loadimage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
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

    fun checkPathIsSDCard(path: String): Boolean {
        return !path.contains("/storage/emulated/0/")
    }

    fun deleteFile(context: Context, url: String, uri: Uri?): Boolean {
        if (checkPathIsSDCard(url)) {
            val documentFile: DocumentFile? = getDocumentSDCardFile(context, uri, url)
            return if (documentFile != null && documentFile.exists()) {
                documentFile.delete()
            } else false
        } else {
            val fdelete = File(url)
            Log.d(TAG, "deleteImage: file delete: " + fdelete.absolutePath)
            return if (fdelete.exists()) {
                Log.d(TAG, "deleteImage: esxit")
                if (fdelete.delete()) {
                    galleryAddPic(context, url)
                    true
                } else {
                    false
                }
            } else {
                Log.d(TAG, "deleteImage: not esxits")
                false
            }
        }
    }

    private fun galleryAddPic(context: Context, filePath: String) {
        val file = File(filePath)
        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()),
            null, null
        )
    }

    fun getDocumentSDCardFile(context: Context, rootDir: Uri?, path: String?): DocumentFile? {
        val file = File(path)
        if (file.exists()) {
            var pickedDir = DocumentFile.fromTreeUri(context, rootDir!!)
            try {
                val modeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(rootDir, modeFlags)
            } catch (e: SecurityException) {
                e.printStackTrace()
                return null
            }
            val parts = file.path.split("/").toTypedArray()
            if (parts.size == 3) {
                if (pickedDir != null) {
                    return pickedDir.findFile(file.name)
                }
            }
            var filename: String? = ""
            for (i in 3 until parts.size) {
                val part = parts[i]
                if (TextUtils.isEmpty(part)) {
                    break
                }
                if (pickedDir != null) {
                    pickedDir = pickedDir.findFile(part)
                    filename = part
                }
            }
            return if (TextUtils.equals(file.name, filename)) {
                pickedDir
            } else {
                null
            }
        }
        return null
    }

    fun renameFile(context: Context, url: String, newName: String, uri: Uri): Boolean {
        if (checkPathIsSDCard(url)) {
            val documentFile: DocumentFile? = getDocumentSDCardFile(context, uri, url)
            return if (documentFile != null && documentFile.exists()) {
                documentFile.renameTo(newName)
            } else false
        } else {
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
    }

    fun copyFile(
        context: Context,
        inputPath: String,
        outputPath: String,
        uri: Uri
    ): Boolean {

        val format = inputPath.substring(inputPath.lastIndexOf("."), inputPath.length)
        val newName =
            inputPath.substring(
                inputPath.lastIndexOf("/") + 1,
                inputPath.lastIndexOf(".")
            ) + "-copy" + format

        var input: InputStream? = null
        var out: OutputStream? = null
        var error: String? = null
        val pickedDir = DocumentFile.fromTreeUri(context, uri)

        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            Log.d(TAG, "copyFile: out: " + outputPath + newName)
            input = FileInputStream(inputPath)

            if (checkPathIsSDCard(outputPath)) {
                val newFile = pickedDir?.createFile(mime(inputPath)!!, newName)

                out = context.getContentResolver().openOutputStream(newFile!!.uri)
            } else {
                out = FileOutputStream(outputPath + newName)
            }
            Log.d(TAG, "copyFile: out: " + outputPath + newName)

            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out?.write(buffer, 0, read)
            }
            input.close()
            input = null


            // write the output file (You have now copied the file)
            out?.flush()
            out?.close()
            out = null

            galleryAddPic(context, outputPath)

            return true
        } catch (fnfe1: FileNotFoundException) {
            
            Log.e("tag", fnfe1.message!!)
        } catch (e: Exception) {
            Log.e("tag", e.message!!)
        } finally {

        }
        return false
    }

    fun mime(URI: String?): String? {
        val type: String?
        val extention = MimeTypeMap.getFileExtensionFromUrl(URI)
        if (extention != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention)
        }
        return null
    }

    fun moveFile(context: Context, inputPath: String, outputPath: String, uri: Uri): Boolean {
        val format = inputPath.substring(inputPath.lastIndexOf("."), inputPath.length)
        val newName =
            inputPath.substring(inputPath.lastIndexOf("/")+1, inputPath.length)

        var input: InputStream? = null
        var out: OutputStream? = null
        var error: String? = null
        val pickedDir = DocumentFile.fromTreeUri(context, uri)

        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            input = FileInputStream(inputPath)

            if (checkPathIsSDCard(outputPath)) {
                val newFile = pickedDir?.createFile(mime(inputPath)!!, newName)

                out = context.getContentResolver().openOutputStream(newFile!!.uri)
            } else {
                out = FileOutputStream(outputPath + newName)
            }
            Log.d(TAG, "copyFile: out: " + outputPath + newName)

            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out?.write(buffer, 0, read)
            }
            input.close()
            input = null

            // write the output file (You have now copied the file)
            out?.flush()
            out?.close()
            out = null

            deleteFile(context, inputPath, uri)
            return true
        } catch (fnfe1: FileNotFoundException) {
            Log.d(TAG, "moveFile: errorrrrrr")
            Log.e("giangtd", fnfe1.message!!)
        } catch (e: Exception) {
            Log.d(TAG, "moveFile: errorrrrrr")
            Log.e("giangtd", e.message!!)
        } finally {

        }
        return false
    }


   /* class ExDirItem {
        var usb = false
        var intCard = false
        var fileDir: String? = null
        var rootDir: String? = null
    }

    fun getDirList(): ArrayList<ExDirItem> {
        var str: String
        val currentTimeMillis = System.currentTimeMillis()
        val arrayList: ArrayList<ExDirItem> = exDirList
        if (arrayList != null && currentTimeMillis - lastListTime < 5000) {
            return arrayList
        }
        lastListTime = currentTimeMillis
        exDirList = ArrayList()
        var fileArr: Array<File?>? = null
        try {
            fileArr = App.ctx().getExternalFilesDirs(null)
        } catch (unused: NullPointerException) {
            unused.printStackTrace()
        }
        if (fileArr == null) {
            return exDirList
        }
        for (i in fileArr.indices) {
            try {
                val file = fileArr[i]
                if (file != null) {
                    str = Environment.getExternalStorageState(file)
                    if (str == "mounted" || str == "mounted_ro" || str == "shared") {
                        val exDirItem = ExDirItem()
                        val absolutePath = file.absolutePath
                        exDirItem.fileDir = absolutePath
                        exDirItem.usb = false
                        if (Build.VERSION.SDK_INT >= 24) {
                            val ctx: App = App.ctx()
                            exDirItem.usb = ctx.getSystemService(StorageManager::class.java)
                                .getStorageVolume(File(exDirItem.fileDir)).getDescription(ctx)
                                .toUpperCase().contains("USB")
                        }
                        exDirItem.intCard = i == 0
                        val indexOf = absolutePath.indexOf("/Android/")
                        if (indexOf != -1) {
                            exDirItem.rootDir = absolutePath.substring(0, indexOf)
                            exDirList.add(exDirItem)
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return exDirList
    }

    fun getExtCardPath(isFileDir: Boolean): String? {
        val dirList = getDirList()
        if (dirList.size() < 2) {
            return null
        }
        return if (isFileDir) dirList[1].fileDir else dirList[1].rootDir
    }*/


}