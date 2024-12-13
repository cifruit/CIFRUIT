package com.gazura.projectcapstone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pisangCardView.setOnClickListener { navigateToDescription(R.string.pisang_title, R.string.pisang_description, R.drawable.deskripsi_pisang) }
        binding.jerukCardView.setOnClickListener { navigateToDescription(R.string.jeruk_title, R.string.jeruk_description, R.drawable.deskripsi_jeruk) }
        binding.pepayaCardView.setOnClickListener { navigateToDescription(R.string.pepaya_title, R.string.pepaya_description, R.drawable.deskripsi_pepaya) }
        binding.buahnagaCardView.setOnClickListener { navigateToDescription(R.string.buahnaga_title, R.string.buahnaga_description, R.drawable.deskripsi_buahnaga) }
        binding.rambutanCardView.setOnClickListener { navigateToDescription(R.string.rambutan_title, R.string.rambutan_description, R.drawable.deskripsi_rambutan) }
    }

    private fun navigateToDescription(titleResId: Int, descriptionResId: Int, imageResId: Int) {
        val bundle = Bundle().apply {
            putInt("titleResId", titleResId)
            putInt("descriptionResId", descriptionResId)
            putInt("imageResId", imageResId)
        }
        findNavController().navigate(R.id.action_homeFragment_to_imageDescriptionFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}