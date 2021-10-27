package com.lavyshyk.photoapp.ui.fullscreen

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.lavyshyk.photoapp.KEY_BUNDLE_NAVIGATE_URI_IMAGE
import com.lavyshyk.photoapp.R
import com.lavyshyk.photoapp.databinding.FragmentFiullscreenFragmentBinding

class FullScreenImageView : Fragment(R.layout.fragment_fiullscreen_fragment) {

    private var binding: FragmentFiullscreenFragmentBinding? = null
    lateinit var mImageView: AppCompatImageView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFiullscreenFragmentBinding.bind(view)
        val uri =  arguments?.getParcelable<Uri>(KEY_BUNDLE_NAVIGATE_URI_IMAGE)

        binding?.mIvFullScreen?.let { mImageView = it }

        val mUri = uri
        if (mUri.toString().contains("content")) {
            mImageView.setImageURI(mUri)
        } else {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("images").child(mUri.toString())
            this.context?.let {
                Glide.with(it)
                    .load(storageReference)
                    .into(mImageView)
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}