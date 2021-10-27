package com.lavyshyk.photoapp.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.lavyshyk.photoapp.R
import com.lavyshyk.photoapp.model.FolderPhotoDto


class GridAdapter(
    private val context: Context,
    private val list: MutableList<FolderPhotoDto.ImageDto>,
    private val isChosen: Boolean,
) : BaseAdapter() {

    private lateinit var mImageView: ImageView
    private lateinit var mCheckBox: CheckBox

    override fun getCount(): Int = list.size

    override fun getItem(i: Int): FolderPhotoDto.ImageDto = list[i]

    override fun getItemId(i: Int): Long = 0


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(context.applicationContext)
                .inflate(R.layout.item_image_grid, null)
        } else {
            view = convertView
        }

        mCheckBox = view.findViewById(R.id.mCBoxImageGrid)
        if (isChosen) {
            mCheckBox.visibility = View.VISIBLE
        } else {
            mCheckBox.visibility = View.GONE
        }
        mCheckBox.setOnCheckedChangeListener { _, isChosen ->
            list[position].isSelect = isChosen
            notifyDataSetChanged()
        }

        mImageView = view.findViewById(R.id.mIvItemGrid)
        val image = getItem(position).imageUri
        if (image.toString().contains("content")) {
            mImageView.setImageURI(image)
        } else {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("images").child(image.toString())
            Glide.with(this.context)
                .load(storageReference)
                .into(mImageView)
        }
        return view
    }
}