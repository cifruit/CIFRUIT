package com.gazura.projectcapstone.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gazura.projectcapstone.R
import com.gazura.projectcapstone.api.response.HasilResponse
import com.gazura.projectcapstone.api.retrofit.ApiConfig
import com.gazura.projectcapstone.camera.CameraXActivity
import com.gazura.projectcapstone.camera.CameraXActivity.Companion.CAMERAX_RESULT
import com.gazura.projectcapstone.data.history.HistoryDatabase
import com.gazura.projectcapstone.data.history.HistoryEntity
import com.gazura.projectcapstone.databinding.FragmentScanBinding
import com.gazura.projectcapstone.token.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var teks :String = ""
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCameraX() }
        binding.scanButton.setOnClickListener { navigateToResult() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCameraX() {
        if (allPermissionsGranted()) {
            val intent = Intent(requireContext(), CameraXActivity::class.java)
            launcherIntentCameraX.launch(intent)
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraXActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        } else {
            Log.e("CameraX", "Failed to capture image")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }
    private fun createFilePart(): MultipartBody.Part? {
        return currentImageUri?.let { uri ->
            val contentResolver = requireContext().contentResolver
            val type = contentResolver.getType(uri)
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("image", ".jpg", requireContext().cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val requestFile = tempFile.asRequestBody(type?.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
        }
    }
    fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }

    private fun navigateToResult() {
        val filePart = createFilePart()
        if (filePart != null) {
            binding.scanningAnimationView.visibility = View.VISIBLE
            binding.scanningAnimationView.playAnimation()
            lifecycleScope.launch {
                kotlinx.coroutines.delay(3000)
                try {
                    val response = ApiConfig.getApiService().Predict(filePart)
                    if (response != null) {
                        val hasil = response.confidence.toString()
                        val pisah = hasil.removeSuffix("%")

                        if (pisah.toDouble() < 60) {
                            binding.scanningAnimationView.visibility = View.GONE
                            binding.scanningAnimationView.pauseAnimation()
                            showAlertDialog()
                        } else {
                            val currentLanguage = Locale.getDefault().language
                            val recommendation = if (currentLanguage == "jv") {
                                when (response.recommendation) {
                                    "Buah mentah, simpan hingga matang sebelum dikonsumsi." ->
                                        getString(R.string.mentah)
                                    "Buah busuk, disarankan untuk dibuang atau digunakan sebagai pupuk kompos." ->
                                        getString(R.string.busuk)
                                    "Buah matang, segera konsumsi untuk rasa terbaik." ->
                                        getString(R.string.matang)
                                    else -> response.recommendation
                                }
                            } else {
                                response.recommendation
                            }

                            val action = ScanFragmentDirections.actionScanFragmentToResultFragment(
                                imageName = response.imageName ?: "",
                                predictedClass = response.predictedClass ?: "",
                                confidence = response.confidence ?: "",
                                recommendation = recommendation.toString(),
                                imageUri = currentImageUri.toString()
                            )
                            saveToHistory(response)
                            binding.scanningAnimationView.visibility = View.GONE
                            binding.scanningAnimationView.pauseAnimation()
                            findNavController().navigate(action)
                        }
                    }

                } catch (e: Exception) {
                    binding.scanningAnimationView.visibility = View.GONE
                    binding.scanningAnimationView.pauseAnimation()
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveToHistory(response: HasilResponse) {
        val sessionManager = SessionManager(requireContext())
        val email = sessionManager.getEmail() ?: ""
        val tanggal = Date()
        val formatTanggal = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val tanggalFormatted = formatTanggal.format(tanggal)
        val history = HistoryEntity(
            email = email,
            imageUri = currentImageUri.toString(),
            predictedClass = response.predictedClass ?: "",
            confidence = response.confidence ?: "",
            recommendation = response.recommendation ?: "",
            tanggal = tanggalFormatted
        )

        lifecycleScope.launch {
            val database = HistoryDatabase.getDatabase(requireContext())
            database.historyDao().insertHistory(history)
            Toast.makeText(requireContext(), R.string.riwayat_berhasil, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.header_gagal_pindai)
        builder.setMessage(R.string.teks_gagal)
        builder.setPositiveButton("OK") { dialog, _ ->
            currentImageUri = null
            binding.previewImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_holder))
            Toast.makeText(requireContext(), R.string.toast_gagal, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}