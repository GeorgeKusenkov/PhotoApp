package com.example.attractionsapp.presentation

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.attractionsapp.R
import com.example.attractionsapp.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

private const val FILENAME_FORMAT = "dd.MM.yyyy HH:mm:ss"

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(requireContext())
    }
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        if (map.values.all { it })
            startCamera()
        else
            Toast.makeText(context, "permission is not Granted", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        checkPermissions()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
        binding.toGalleryButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.container, GalleryFragment())
                addToBackStack("gallery")
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                requireActivity(),
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun takePhoto() {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModel.onAddButton(name, outputFileResults.savedUri.toString())
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Photo FAILED: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    exception.printStackTrace()
                }
            }
        )
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all {permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            startCamera()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}