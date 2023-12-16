package com.example.m5.ui.viewmodel

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.m5.MusicApplication
import com.example.m5.logic.Repository
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

class QrLoginActivityViewModel: ViewModel() {

    private val keyliveData = MutableLiveData<String>()
    private val codeliveData = MutableLiveData<Pair<String, String>>()
    private val codeStatusliveData = MutableLiveData<String>()



    lateinit var key: String
//    private lateinit var qrCode: String
    lateinit var bitmap: Bitmap


    val keyFocusData = Transformations.switchMap(keyliveData){timestamp->
        Repository.getKey(timestamp)
    }

    val codeFocusData = Transformations.switchMap(codeliveData){pair->
        Repository.getCode(pair.first, pair.second)
    }

    val codeStatusFocusData = Transformations.switchMap(codeStatusliveData){key->
        Repository.getCodeStatue(key)
    }



    fun getKey(timestamp: String){
        keyliveData.value = timestamp
    }

    fun getCode(key: String, timestamp: String){
        codeliveData.value = Pair(key, timestamp)
    }

    fun getCodeStatus(key: String){
        codeStatusliveData.value = key
    }


    //图像解码
    @RequiresApi(Build.VERSION_CODES.O)
    fun base642Bitmap(base64: String): Bitmap{
        val parts = base64.split(",")
        val decode = Base64.getDecoder().decode(parts[1])
        val mBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)

        return mBitmap
    }

    //插入图片到相册
    fun insert2Album(inputStream: InputStream, fileName: String){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // RELATIVE_PATH 字段表示相对路径-------->(1)
            contentValues.put(
                MediaStore.Images.ImageColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
        } else {
            val dstPath = (Environment.getExternalStorageDirectory()
                .toString() + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator + fileName)
            // DATA字段在Android 10.0 之后已经废弃
            contentValues.put(MediaStore.Images.ImageColumns.DATA, dstPath)
        }

        //插入相册------->(2)
        val uri = MusicApplication.context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        write2File(uri, inputStream)

        MusicApplication.context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

    }

    //写文件
    fun write2File(uri: Uri?, inputStream: InputStream){
        if (uri == null || inputStream == null)
            return
        try {

            val outputStream: OutputStream? = MusicApplication.context.contentResolver.openOutputStream(uri)
            val buffer = ByteArray(1024)
            var len = 0
            do{
                len = inputStream.read(buffer)
                if (len != -1){
                    outputStream?.write(buffer, 0, len)
                    outputStream?.flush()
                }
            }while (len != -1)
            inputStream.close()
            outputStream?.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }



    //持久化层
    fun saveCookie(cookie: String) = Repository.saveCookie(cookie)


}