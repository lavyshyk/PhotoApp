package com.lavyshyk.photoapp.base

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import java.util.prefs.NodeChangeListener

abstract class BaseAdapter<ItemType> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     var mDataList: MutableList<ItemType> = mutableListOf()

    protected var mClickFunction: ((String, ItemType) -> Unit?)? = null
    protected var mClickOnImageFunction: ((String, ItemType, Uri) -> Unit?)? = null
    protected var mChangeResultFunc: ((ItemType) -> Unit)? = null

    fun setResultChanges(changeResultFunc: (ItemType) -> Unit) {
        mChangeResultFunc = changeResultFunc
    }

    fun setItemClick(clickListener: (String, ItemType) -> Unit) {
        mClickFunction = clickListener
    }

    fun setItemImageClick(clickListener: (String, ItemType, Uri) -> Unit) {
        mClickOnImageFunction = clickListener
    }

    override fun getItemCount(): Int = mDataList.size

    open fun repopulate(list: MutableList<ItemType>) {
        mDataList.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }
    open fun refresh(){
      notifyDataSetChanged()
    }

    open fun addList(list: MutableList<ItemType>) {
        mDataList.addAll(list)
        notifyDataSetChanged()
    }


    open fun addItem(item: ItemType) {
        mDataList.add(item)
        notifyItemChanged(mDataList.size - 1)
    }


    open fun clear() {
        mDataList.clear()
        notifyDataSetChanged()
    }

}