package com.example.m5.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.m5.R
import com.example.m5.activity.MainActivity
import com.example.m5.databinding.FragmentNeteaseEverythingBinding
import com.example.m5.ui.viewmodel.NetEaseMainViewModel

class NeteaseEverything : Fragment() {
    private lateinit var viewModel: NetEaseMainViewModel
    companion object {
        @Suppress("StaticFieldLeak")
        lateinit var binding: FragmentNeteaseEverythingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_netease_everything, container, false)
        binding = FragmentNeteaseEverythingBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[NetEaseMainViewModel::class.java]

        binding.NetEaseBtnNF.setOnClickListener {
            viewModel.netEasePage.value = 0
        }
        binding.RecommendedBtnNF.setOnClickListener {
            viewModel.netEasePage.value = 1
        }
        binding.RadioBtnNF.setOnClickListener {
//            viewModel.netEasePage.value = 2
        }
        binding.BrowseBtnNF.setOnClickListener {
            viewModel.netEasePage.value = 2
        }
        binding.LibraryNF.setOnClickListener {
//            viewModel.netEasePage.value = 4
        }
        binding.CloudDriveNF.setOnClickListener {
//            viewModel.netEasePage.value = 5
        }
        binding.RecentlyPlayedNF.setOnClickListener {
//            viewModel.netEasePage.value = 6
        }
        binding.SearchNF.setOnClickListener {
            viewModel.netEasePage.value = 3
        }


        return view
    }

    override fun onResume() {
        super.onResume()
        binding.root.visibility = View.VISIBLE

    }
}