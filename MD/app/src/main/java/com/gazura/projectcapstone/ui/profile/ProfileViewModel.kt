import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _profilePhotoUri = MutableLiveData<Uri?>()
    val profilePhotoUri: LiveData<Uri?> get() = _profilePhotoUri

    fun setProfilePhotoUri(uri: Uri?) {
        _profilePhotoUri.value = uri
    }
}