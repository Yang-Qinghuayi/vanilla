package com.example.m5.ui.activity

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.m5.R
import com.example.m5.databinding.ActivityQrLoginBinding
import com.example.m5.ui.AppConfig
import com.example.m5.ui.viewmodel.QrLoginActivityViewModel
import com.example.m5.util.setStatusBarTextColor
import com.example.m5.util.transparentStatusBar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class QrLoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[QrLoginActivityViewModel::class.java] }

    private lateinit var binding: ActivityQrLoginBinding

    companion object {
        var install: QrLoginActivity? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolRed)
        setContentView(R.layout.activity_qr_login)

        binding = ActivityQrLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //透明状态栏以及文字颜色设定
        transparentStatusBar(window)
        setStatusBarTextColor(window, false)
        install = this

        //绑定
        //获取key
        viewModel.keyFocusData.observe(this) { result ->
            val result = result.getOrNull()
            if (result != null) {
                if (result.code == "200") {
                    val key = result.unikey
                    viewModel.key = result.unikey
                    viewModel.getCode(key, System.currentTimeMillis().toString())
                }
            }

        }

        //获取二维码
        viewModel.codeFocusData.observe(this) { result ->
            val result = result.getOrNull()
            if (result != null) {
                Log.d("hucheng", "${result.qrurl} + ${result.qrimg}")
                binding.qrCodeImage.setImageBitmap(viewModel.base642Bitmap(result.qrimg))
                viewModel.bitmap = viewModel.base642Bitmap(result.qrimg)
                viewModel.getCodeStatus(viewModel.key)

            }
        }

        //监听扫码状态
        viewModel.codeStatusFocusData.observe(this, Observer { result ->
//            val cookie = result
//            AppConfig.cookie = result?.cookie!!
//            Log.d("hucheng", "cookie : ${AppConfig.cookie}")
//            AppConfig.isLogined = true
//            Log.d("hucheng", "cookie: ${AppConfig.cookie}")
//
//            //保存cookie
//            viewModel.saveCookie(AppConfig.cookie!!)
//            finish()
            val cookieNow = result.getOrNull()
            Log.d("hucheng", "返回的cookie：$cookieNow")
            AppConfig.isChanged = false
            Log.d("hucheng", "Appconfig cookie: ${AppConfig.cookie}")
            viewModel.saveCookie(AppConfig.cookie)
            finish()
        })


        //开始时候获取
        viewModel.getKey(System.currentTimeMillis().toString())


        //点击保存图片
        binding.saveQrCode.setOnClickListener {
            val picName = "NeteaseLoginQRCode.jpg"
            try {
                val temp = ByteArrayOutputStream()
                viewModel.bitmap.compress(Bitmap.CompressFormat.JPEG, 0, temp)
                val byin = ByteArrayInputStream(temp.toByteArray())
                viewModel.insert2Album(byin, picName)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()

        }


    }
}
