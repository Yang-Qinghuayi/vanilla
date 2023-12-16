package com.example.m5.ui.frag

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.m5.databinding.FragmentNeteaseMineBinding
import com.example.m5.ui.viewmodel.NetEaseMineViewModel
import com.example.m5.ui.AppConfig
import com.example.m5.ui.activity.QrLoginActivity

class NeteaseMine : Fragment() {

    lateinit var binding: FragmentNeteaseMineBinding
    private lateinit var viewModel: NetEaseMineViewModel
    private var isOncreated: Boolean = false

    companion object {
        var install: NeteaseMine? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNeteaseMineBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        install = this
        viewModel = ViewModelProvider(this)[NetEaseMineViewModel::class.java]
        binding.userPerson.setOnClickListener {
            startActivity(Intent(this.context, QrLoginActivity::class.java))
        }

        //第一次进入这个页面：onCreate，不管isLogined是什么，都需要进行一次判断，或者说，这次判断就是用来决定isLogined的
        viewModel.statusFocusData.observe(this.viewLifecycleOwner) { result ->
            val result = result.getOrNull()
            if (result?.data?.profile == null) {
                //未登录
                AppConfig.isLogined = false
                //其实本来就是false了，这里再写一次只是为了更好明白逻辑
            } else {
                //登录

                //这里的登录只记录信息
                AppConfig.isLogined = true
                result.data.profile.let {
                    AppConfig.let { app ->
                        app.uid = it.userId
                        app.nickname = it.nickname
                        app.avatarUrl = it.avatarUrl
                    }
                }
                //加载昵称图片
                binding.usernameNMA.text = AppConfig.nickname
                NetEaseMineViewModel.loadPicture(AppConfig.avatarUrl!!)
            }
        }

        //首次进来获取保存的cookie进行登陆状态查询(cookie没保存返回null)
        if (viewModel.isCookieSaved()) {
            AppConfig.cookie = viewModel.getSavedCookie()!!
            viewModel.getStatus(System.currentTimeMillis().toString(), viewModel.getSavedCookie()!!)
        } else {
            AppConfig.cookie = ""
            viewModel.getStatus(System.currentTimeMillis().toString(), "")
        }
        isOncreated = true

    }

    override fun onResume() {
        super.onResume()
        //如果经历了onCreate，就不查询，没经历就查询
        if (!isOncreated) {
            viewModel.getStatus(System.currentTimeMillis().toString(), viewModel.getSavedCookie()!!)
        }

        isOncreated = false

        if (AppConfig.isLogined) {

        } else {

        }
    }
}