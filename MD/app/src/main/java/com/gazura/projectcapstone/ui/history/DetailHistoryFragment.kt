package com.gazura.projectcapstone.ui.history

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.data.history.HistoryDatabase
import com.gazura.projectcapstone.databinding.FragmentDetailHistoryBinding
import kotlinx.coroutines.launch

class DetailHistoryFragment : Fragment() {
    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("id") ?: return
        val imageUri = arguments?.getString("imageUri")
        val predictedClass = arguments?.getString("predictedClass")
        val confidence = arguments?.getString("confidence")
        val recommendation = arguments?.getString("recommendation")
        val tanggal = arguments?.getString("date")

        Glide.with(this)
            .load(Uri.parse(imageUri))
            .error(R.drawable.buahnaga)
            .into(binding.gambarDetail)
        binding.namaBuahDetail.text = predictedClass
        binding.indikasiDetail.text = confidence
        binding.rekomendasiDetail.text = recommendation
        binding.tanggalDetail.text = tanggal

        binding.btnHapus.setOnClickListener {
            showDeleteConfirmationDialog(id.toInt())
        }

        // Set up the toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showDeleteConfirmationDialog(id: Int) {
        val currentContext = context ?: return
        AlertDialog.Builder(currentContext)
            .setTitle(R.string.header_konfirm_hapus_riwayat)
            .setMessage(R.string.konfirm_hapus)
            .setPositiveButton(R.string.y) { _, _ ->
                val database = HistoryDatabase.getDatabase(requireContext())
                val historyDao = database.historyDao()
                lifecycleScope.launch {
                    historyDao.deleteHistoryById(id)
                    Toast.makeText(requireContext(), R.string.berhasil_hapus, Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
            .setNegativeButton(R.string.g) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}