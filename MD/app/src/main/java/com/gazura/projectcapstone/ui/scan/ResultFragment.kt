package com.gazura.projectcapstone.ui.scan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: ResultFragmentArgs by navArgs()
        binding.namaBuah.text = args.predictedClass
        binding.indikasi.text = args.confidence
        binding.rekomendasi.text = args.recommendation

        val imageUri = args.imageUri
        if (imageUri.isNotEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .error(R.drawable.buahnaga)
                .into(binding.gambar)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
