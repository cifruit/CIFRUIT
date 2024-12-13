package com.gazura.projectcapstone.ui.home.deskripsi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gazura.projectcapstone.databinding.FragmentImageDescriptionBinding

class ImageDescriptionFragment : Fragment() {

    private var _binding: FragmentImageDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        arguments?.let {
            val titleResId = it.getInt("titleResId")
            val descriptionResId = it.getInt("descriptionResId")
            val imageResId = it.getInt("imageResId")

            binding.title.text = getString(titleResId)
            binding.description.text = getString(descriptionResId)
            binding.fruitImage.setImageResource(imageResId)
        }

        binding.shareButton.setOnClickListener {
            shareDescription()
        }
    }

    private fun shareDescription() {
        val descriptionText = binding.description.text.toString()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, descriptionText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}