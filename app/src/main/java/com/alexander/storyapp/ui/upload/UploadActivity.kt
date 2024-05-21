package com.alexander.storyapp.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var binding : ActivityUploadBinding

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

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        with(binding){
            btnCamera.setOnClickListener { launchCamera() }
            btnGallery.setOnClickListener { startGallery() }
            buttonAdd.setOnClickListener {
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
                        with(uploadViewModel){
                            showLoading(true)
                            uploadStory(multipartBody, requestBody)

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
    }
}