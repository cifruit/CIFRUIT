package com.gazura.projectcapstone.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gazura.projectcapstone.R

import com.gazura.projectcapstone.data.profile.ProfileDatabase
import com.gazura.projectcapstone.data.profile.ProfileEntity
import com.gazura.projectcapstone.databinding.FragmentProfileBinding
import com.gazura.projectcapstone.databinding.FragmentScanBinding
import com.gazura.projectcapstone.token.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var profileDatabase: ProfileDatabase

    private lateinit var profilePhoto: ImageView
    private lateinit var editIcon: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var themeSwitch: Switch
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(requireContext())
        profileDatabase = ProfileDatabase.getDatabase(requireContext())

        profilePhoto = binding.profilePhoto
        editIcon = binding.editIcon
        nameEditText = binding.editNama
        themeSwitch = binding.themeSwitch
        val email = sessionManager.getEmail() ?: ""
        loadProfile(email)

        themeSwitch.isChecked = requireContext().getSharedPreferences("user_prefs", 0)
            .getBoolean("dark_theme", false)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            requireContext().getSharedPreferences("user_prefs", 0)
                .edit().putBoolean("dark_theme", isChecked).apply()
        }
        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newName = nameEditText.text.toString()
                saveNameToDatabase(sessionManager.getEmail() ?: "", newName)
            }
        }
        val bahasaButton=binding.bahasa
        bahasaButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), bahasaButton)
            popupMenu.menuInflater.inflate(R.menu.menu_bahasa, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.indo -> {
                        showLanguageConfirmationDialog("Bahasa Indonesia", "id")
                        true
                    }
                    R.id.jawa -> {
                        showLanguageConfirmationDialog("Bahasa Jawa", "jv")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        val exitButton=binding.exitButton
        exitButton.setOnClickListener {
            showExitConfirmationDialog()
        }
        editIcon.setOnClickListener {
            openGallery()
        }
        return binding.root
    }
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = requireContext().resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun saveLanguagePreference(languageCode: String) {
        val prefs = requireContext().getSharedPreferences("user_prefs", 0)
        prefs.edit().putString("language", languageCode).apply()
    }

    private fun showLanguageConfirmationDialog(language: String, languageCode: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin mengganti bahasa ke $language?")
            .setPositiveButton("Ya") { _, _ ->
                setAppLocale(languageCode)
                saveLanguagePreference(languageCode)
                Toast.makeText(requireContext(), "Bahasa diubah ke $language", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bahasa, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.logout)
            .setPositiveButton(R.string.y) { _, _ ->
                sessionManager.clearLoginSession()
                requireActivity().finishAffinity()
            }
            .setNegativeButton(R.string.g, null)
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedPhotoUri = uri
                    updateProfilePhoto(uri)
                    savePhotoToDatabase(sessionManager.getEmail() ?: "", uri.toString())
                }
            }
        }

    private fun updateProfilePhoto(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(profilePhoto)
    }

    private fun loadProfile(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val profile = profileDatabase.profileDao().getProfileByEmail(email)
                withContext(Dispatchers.Main) {
                    profile?.let {
                        nameEditText.setText(it.name)
                        if (!it.photo.isNullOrEmpty()) {
                            Glide.with(this@ProfileFragment)
                                .load(it.photo)
                                .placeholder(R.drawable.ic_profile)
                                .into(profilePhoto)
                        }
                        println("Profile loaded: ${it.name}, ${it.photo}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun saveNameToDatabase(email: String, name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val profile = profileDatabase.profileDao().getProfileByEmail(email)
                if (profile != null) {
                    val updatedProfile = profile.copy(name = name)
                    profileDatabase.profileDao().insertOrUpdateProfile(updatedProfile)
                } else {
                    val newProfile = ProfileEntity(email = email, name = name, photo = "")
                    profileDatabase.profileDao().insertOrUpdateProfile(newProfile)
                }
                println("Name saved: $name")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun savePhotoToDatabase(email: String, photoUri: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val profile = profileDatabase.profileDao().getProfileByEmail(email)
                if (profile != null) {
                    val updatedProfile = profile.copy(photo = photoUri)
                    profileDatabase.profileDao().insertOrUpdateProfile(updatedProfile)
                } else {
                    val newProfile = ProfileEntity(email = email, name = "", photo = photoUri)
                    profileDatabase.profileDao().insertOrUpdateProfile(newProfile)
                }
                println("Photo URI saved: $photoUri")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
