package com.example.m5.ui.frag

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.m5.databinding.ActivityNcBinding
import com.example.m5.ui.adapter.PlayListsAdapter
import com.example.m5.ui.adapter.ViewPagerAdapter
import com.example.m5.ui.viewmodel.MusicActivityViewModel

class NeteaseBrowse : Fragment() {

    lateinit var binding: ActivityNcBinding
    private lateinit var viewModel: MusicActivityViewModel
    private lateinit var adapterPlayList: PlayListsAdapter

    companion object {
        var instance: NeteaseBrowse? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityNcBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instance = this
        viewModel = ViewModelProvider(this)[MusicActivityViewModel::class.java]

        val layoutManagerPlayLists = GridLayoutManager(this.context,2, GridLayoutManager.HORIZONTAL,false)
        binding.recyclerViewPlaylist.layoutManager = layoutManagerPlayLists
        adapterPlayList = PlayListsAdapter(viewModel.playLists,this.requireContext())
        binding.recyclerViewPlaylist.adapter = adapterPlayList

        //adapter的装饰
        val decoration: RecyclerView.ItemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = 0
                outRect.left = 38
                outRect.top = 10
                outRect.bottom = 10
            }
        }
        binding.recyclerViewPlaylist.addItemDecoration(decoration)

        viewModel.refresh()
        viewModel.mainLiveData.observe(this.viewLifecycleOwner) { result->
            val mainNcResponse = result.getOrNull()
            if(mainNcResponse != null){
                viewModel.playLists.clear()
                viewModel.playLists.addAll(mainNcResponse.playLists)

                adapterPlayList.notifyDataSetChanged()

            }
        }
    }

}