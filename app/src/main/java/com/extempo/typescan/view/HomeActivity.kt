package com.extempo.typescan.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.extempo.camera.view.CameraActivity
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.R
import com.extempo.typescan.adapter.DocumentListAdapter
import com.extempo.typescan.adapter.DocumentPagedListAdapter
import com.extempo.typescan.databinding.ActivityHomeBinding
import com.extempo.typescan.utilities.InjectorUtils
import com.extempo.typescan.utilities.ModuleLoader
import com.extempo.typescan.viewmodel.DocumentListViewModel
import com.extempo.typescan.viewmodel.HomeActivityViewModel
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeActivityViewModel
    private val adapter = DocumentPagedListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpticalCharacterDetector.loadModel(this)
        initializeUI()
        setListeners()
    }

    private fun initializeUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val factory = InjectorUtils.provideHomeActivityViewModelFactory(this)
        homeViewModel = ViewModelProviders.of(this, factory).get(HomeActivityViewModel::class.java)

        val documentListRecyclerView: RecyclerView = findViewById(R.id.home_doc_recycler_view)
        documentListRecyclerView.adapter = adapter
        documentListRecyclerView.layoutManager = LinearLayoutManager(this)
        homeViewModel.getAlldocumentItems()?.observe(this, Observer { list ->
            adapter.submitList(list)
        })
    }

    private fun setListeners() {
        openCameraButton.setOnClickListener {
            val cameraActivity = Intent(this, CameraActivity::class.java)
            startActivityForResult(cameraActivity, CAMERA_ACTIVITY)
        }

        runInferenceButton.setOnClickListener {
            homeViewModel.capturedImageBitmap?.let {
                OpticalCharacterDetector.findAlphabets(it,
                    object : InferenceListener {
                        override fun started(bitmap: Bitmap) {
                            // show Activity Indicator
                        }

                        @SuppressLint("RestrictedApi")
                        override fun finished(bitmap: Bitmap) {
                            binding.croppedImageBitmap = bitmap
                            // stop activity Indicator
                            openCameraButton.visibility = View.VISIBLE
                            runInferenceButton.visibility = View.GONE
                        }
                    })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                if (data?.hasExtra(MediaStore.EXTRA_OUTPUT)!!) {
                    val result = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT) as Uri
                    Glide.with(this)
                        .asBitmap()
                        .load(result)
                        .into(object : CustomTarget<Bitmap>(){
                            @SuppressLint("RestrictedApi")
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                binding.croppedImageBitmap = resource
                                homeViewModel.capturedImageUri = result
                                homeViewModel.capturedImageBitmap = resource
                                runInferenceButton.visibility = View.VISIBLE
                                openCameraButton.visibility = View.GONE
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
            if (resultCode == RESULT_ERROR) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        init {
            ModuleLoader.load()
        }

        const val CAMERA_ACTIVITY = 70

        @JvmStatic
        @BindingAdapter("bind:imageBitmap")
        fun loadImage(iv: ImageView, bitmap: Bitmap?) {
            iv.setImageBitmap(bitmap)
        }
    }
}