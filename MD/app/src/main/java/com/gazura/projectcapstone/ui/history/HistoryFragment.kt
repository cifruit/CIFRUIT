package com.gazura.projectcapstone.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.data.history.HistoryDatabase
import com.gazura.projectcapstone.data.history.HistoryEntity
import com.gazura.projectcapstone.token.SessionManager
import kotlinx.coroutines.launch
import java.util.Locale


class HistoryFragment : Fragment() {

    private lateinit var adapter: HistoryAdapter
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val sessionManager = SessionManager(requireContext())
        email = sessionManager.getEmail() ?: ""

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter { history ->
            openDetailFragment(history)
        }
        recyclerView.adapter = adapter

        loadHistory()
        return view
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            val database = HistoryDatabase.getDatabase(requireContext())
            database.historyDao().getHistoriesByEmail(email).collect { histories ->
                adapter.submitList(histories)
            }
        }
    }

    private fun openDetailFragment(history: HistoryEntity) {
        try {
            val currentLanguage = Locale.getDefault().language
            val bahasaRekomendasi =if (currentLanguage == "jv") {
                when (history.recommendation) {
                    "Buah mentah, simpan hingga matang sebelum dikonsumsi." ->
                        resources.getString(R.string.mentah)
                    "Buah busuk, disarankan untuk dibuang atau digunakan sebagai pupuk kompos." ->
                        resources.getString(R.string.busuk)
                    "Buah matang, segera konsumsi untuk rasa terbaik." ->
                        resources.getString(R.string.matang)
                    else ->history.recommendation
                }
            } else {
                history.recommendation
            }
            val action = HistoryFragmentDirections.actionNavigationHistoryToNavigationDetailHistory(
                id=history.id.toString(),
                predictedClass = history.predictedClass ,
                confidence = history.confidence ,
                recommendation = bahasaRekomendasi,
                imageUri = history.imageUri,
                date=history.tanggal
            )
            findNavController().navigate(action)
        }catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }
}

