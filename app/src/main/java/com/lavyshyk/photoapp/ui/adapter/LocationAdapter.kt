package com.lavyshyk.photoapp.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.lavyshyk.photoapp.*
import com.lavyshyk.photoapp.base.BaseAdapter
import com.lavyshyk.photoapp.model.FolderPhotoDto


class LocationAdapter : BaseAdapter<FolderPhotoDto>() {
    lateinit var mGridAdapter: GridAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderPhotoHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderPhotoHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FolderPhotoHolder) {
            val item = mDataList[position]
            val list = item.listImage
            val isChosen = item.isChosen
            mGridAdapter = GridAdapter(holder.itemView.context, list, isChosen)
            holder.mGridView.adapter = mGridAdapter

            holder.mGridView.setOnItemLongClickListener { adapterView, view, i, l ->
                mDataList.forEach { it.isChosen = true }
                mClickFunction?.invoke(DELETE_IMAGE, item)
                return@setOnItemLongClickListener true
            }
            holder.mGridView.setOnItemClickListener { _, _, i, _ ->
                val uri = mDataList[position].listImage[i].imageUri
                mClickOnImageFunction?.invoke(FULL_SCREEN_IMAGE, item, uri)
                return@setOnItemClickListener
            }
            val tv = holder.mTVNameFolder
            val et = holder.mETNameFolder
            tv.text = item.nameFolderPhoto

            et.setText(item.nameFolderPhoto, TextView.BufferType.NORMAL)
            et.doAfterTextChanged {
                var text = it.toString()
                if (text == "") {
                    text = "Name"
                }
                tv.text = text
                item.nameFolderPhoto = text
            }

            et.onFocusChangeListener = View.OnFocusChangeListener { _, isFocus ->
                if (!isFocus) {
                    mChangeResultFunc?.invoke(item)
                }
            }

            holder.mIbAddImage.setOnClickListener {
                mClickFunction?.invoke(GET_IMAGE, item)
            }
        }
    }

    inner class FolderPhotoHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mETNameFolder: AppCompatEditText = view.findViewById(R.id.mETNameChildFolder)
        var mGridView: GridView = view.findViewById(R.id.mGridView)
        var mIbAddImage: AppCompatImageButton = view.findViewById(R.id.mIBAddImage)
        var mTVNameFolder: AppCompatTextView = view.findViewById(R.id.mTVNameChildFolder)

    }

    fun repopulateItem(item: FolderPhotoDto) {
        val index = mDataList.indexOf(item)
        if (mDataList.size > 0) {
            mDataList[index] = item
        } else {
            mDataList.add(item)
        }
        notifyItemChanged(mDataList.indexOf(item))
    }
}
