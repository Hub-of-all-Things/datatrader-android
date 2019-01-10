package com.hubofallthings.dataplugs.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.hubofallthings.dataplugs.R
import com.hubofallthings.dataplugs.services.Preference
import kotlinx.android.synthetic.main.image_preview.*

class ImagePreviewActivity : AppCompatActivity() , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_preview)
        setImage()
        closeImageView.setOnClickListener(this)
        val customPhotoView = findViewById<PhotoView>(R.id.photo_view)
        customPhotoView.setOnSingleFlingListener { _, _, _, _ ->
            finish()
            overridePendingTransition(0,0)
            true
        }
    }

    override fun onPause() {
        setImage()
        super.onPause()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.closeImageView -> {
                finish()
            }
        }
    }
    private fun setImage(){
        val imageView = findViewById<PhotoView>(R.id.photo_view)
        val myPreference = Preference(this)
        val imagePreview = myPreference.getImageUrl()
        Glide.with(this).load(imagePreview).into(imageView)
    }
}