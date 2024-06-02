package com.alexander.storyapp.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alexander.storyapp.R
import com.alexander.storyapp.databinding.ActivityUploadBinding
import com.alexander.storyapp.ui.ViewModelFactory
import com.alexander.storyapp.ui.home.HomeActivity
import com.alexander.storyapp.utils.getImageUri
import com.alexander.storyapp.utils.reduceImageFile
import com.alexander.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var binding : ActivityUploadBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory(applicationContext)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private val requestPermissionLauncherLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getCurLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
               getCurLocation()
            }

            else -> {
                showToast("Permission request denied")
            }
        }
    }

    private fun cameraGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!cameraGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding){
            btnCamera.setOnClickListener { launchCamera() }
            btnGallery.setOnClickListener { startGallery() }
            cbLocation.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    getCurLocation()
                }else{
                    lat = null
                    lon = null
                }
            }
            buttonAdd.setOnClickListener {
                getCurLocation()
                binding.buttonAdd.isEnabled = false
                val description = edAddDescription.text.toString()
                if(description.isNotEmpty() && imageUri.toString() != "null"){
                    Log.e("Uri Upload", "Gak kosong")
                    showLoading(true)
                    imageUri?.let{
                        val file = uriToFile(it, this@UploadActivity).reduceImageFile()
                        val requestBody = description.toRequestBody("text/plain".toMediaType())
                        val requestFileImage = file.asRequestBody("image/jpeg".toMediaType())
                        val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestFileImage)
                        val requestLat = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                        val requestLong = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                        with(uploadViewModel){
                            showLoading(true)
                            uploadStory(multipartBody, requestBody, requestLat, requestLong)

                            uploadStatus.observe(this@UploadActivity){ result ->
                                result.onSuccess{
                                    showToast(getString(R.string.upload_success))
                                    showLoading(false)
                                    val intent = Intent(this@UploadActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                                result.onFailure {
                                    binding.buttonAdd.isEnabled = true
                                    showToast(it.message.toString())
                                    showLoading(false)
                                }
                            }

                        }
                    }
                }else{
                    showLoading(false)
                    binding.buttonAdd.isEnabled = true
                    showToast(getString(R.string.upload_error))
                }

            }
        }

    }

    private fun launchCamera(){
        imageUri = getImageUri(this)
        launcherIntentCamera.launch(imageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurLocation(){
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                } else {
                    showToast("Failed to retrieve location")
                }
            }
        }else{
            requestPermissionLauncherLocation.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun showImage() {
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun showLoading(status: Boolean){
        binding.pgLoading.visibility = if(status) View.VISIBLE else View.GONE
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_LOCATION_FINE = Manifest.permission.ACCESS_FINE_LOCATION
        private const val REQUIRED_PERMISSION_LOCATION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION
    }
}