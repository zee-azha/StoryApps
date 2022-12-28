package com.example.storyapps

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.storyapps.databinding.ActivityAddStoryBinding
import com.example.storyapps.utils.Resource
import com.example.storyapps.utils.reduceFileImage
import com.example.storyapps.utils.rotateBitmap
import com.example.storyapps.utils.uriToFile
import com.example.storyapps.viewModel.AddStoryViewModel
import com.example.storyapps.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ActivityAddStory : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: AddStoryViewModel by viewModels { factory }
    private var getFile: File? = null
    private var location: Location? = null
    private var token: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.add_story)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        factory = ViewModelFactory.getInstance(this)
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect {
                    if (!it.isNullOrEmpty()) token = it
                }
            }
        }
        binding.apply {
            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            imagePreview.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> setButtonEnable() }
            btnCamera.setOnClickListener {
                startCamera()
            }
            btnGallery.setOnClickListener {
                startGallery()

            }
            btnUpload.setOnClickListener {
                uploadStory()
            }
            switchLocation.setOnCheckedChangeListener { _, isCheck ->
                if (isCheck){
                    getLocation()
                }else{
                    this@ActivityAddStory.location = null
                }
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it!= null){
                    this.location = it

                }else{
                    Toast.makeText(this,getString(R.string.please_activate_location_message),Toast.LENGTH_SHORT).show()
                    binding.switchLocation.isChecked = false
                }
            }
        }
        else{
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }
    private val requestPermissionLauncher= registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        when {
            isGranted[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLocation()
            }
            else -> {
                Snackbar.make(
                    binding.root,
                    getString(R.string.location_permission_denied),
                    Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.location_permission_denied_action)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()
                binding.switchLocation.isChecked = false
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile?.path),
                isBackCamera
            )
            binding.imagePreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@ActivityAddStory)
            getFile = myFile
            binding.imagePreview.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        val file = reduceFileImage(getFile as File)
        val description =
            binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        var lat: RequestBody? = null
        var lon: RequestBody? = null
        if (location !=null) {
            lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
            lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
        }
        lifecycleScope.launchWhenCreated {
            launch {

                viewModel.uploadStory(token, imageMultipart, description,lat,lon).collect {
                    when(it){
                        is Resource.Success ->{
                            Toast.makeText(this@ActivityAddStory, getString(R.string.upload_story), Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            showLoading(false)
                        }
                        is  Resource.Failure ->{
                            Toast.makeText(this@ActivityAddStory, resources.getString(R.string.failed_upload_story), Toast.LENGTH_SHORT).show()
                            showLoading(false)
                        }
                        is Resource.Loading ->{
                            showLoading(true)
                        }
                    }

                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setButtonEnable() {
        binding.apply {
            val imageView = getFile
            val description = etDescription.text
            btnUpload.isEnabled = imageView != null && description.toString().isNotEmpty()
        }
    }


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


}
