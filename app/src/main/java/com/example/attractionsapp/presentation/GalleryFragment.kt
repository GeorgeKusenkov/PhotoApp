package com.example.attractionsapp.presentation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.attractionsapp.R
import com.example.attractionsapp.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        observePhoto()
    }

    private fun observePhoto() {
        lifecycleScope.launchWhenCreated {
            viewModel.allPhotos
                .collect { photos ->
                    val uris = mutableListOf<Uri>()
                    val date = mutableListOf<String>()
                    photos.forEach {
                        uris.add(Uri.parse(it.url))
                        date.add(it.date)
                    }

                    val gridAdapter = PhotoAdapter(uris, date)
                    binding.gridRecyclerView.adapter = gridAdapter
                }
        }
    }

    private fun setOnClickListeners() {
        binding.takePhotoButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.container, MainFragment())
                addToBackStack("toPhoto")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}