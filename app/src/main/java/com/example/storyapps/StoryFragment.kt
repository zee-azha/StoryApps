package com.example.storyapps

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapps.databinding.FragmentStoryBinding
import com.example.storyapps.utils.ListAdapter
import com.example.storyapps.utils.LoadingStateAdapter
import com.example.storyapps.viewModel.StoriesViewModel
import com.example.storyapps.viewModel.ViewModelFactory


class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var storyAdapter: ListAdapter

    private val viewModel: StoriesViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStory.layoutManager = LinearLayoutManager(requireContext())

        binding.swipe.setOnRefreshListener {
            getAllStories()
            storyAdapter.refresh()
            binding.swipe.isRefreshing = true
        }
        getAllStories()
        setListener()

    }


    private fun getAllStories() {
        val token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN).toString()
        storyAdapter = ListAdapter()
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                storyAdapter.retry()
            }
        )
        viewModel.getStory(token).observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle,it)
        }
    }

    private fun setListener(){
        binding.apply {
            fabAdd.setOnClickListener {
                startActivity(Intent(requireContext(),ActivityAddStory::class.java))
            }
            swipe.setOnRefreshListener {
                getAllStories()
                storyAdapter.refresh()
                swipe.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllStories()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}