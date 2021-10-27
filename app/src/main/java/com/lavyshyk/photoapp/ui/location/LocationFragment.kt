package com.lavyshyk.photoapp.ui.location


import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lavyshyk.photoapp.*
import com.lavyshyk.photoapp.databinding.FragmentLocationBinding
import com.lavyshyk.photoapp.model.FolderPhotoDto
import com.lavyshyk.photoapp.model.Outcome
import com.lavyshyk.photoapp.model.setImageToListImages
import com.lavyshyk.photoapp.ui.adapter.LocationAdapter


class LocationFragment : Fragment(R.layout.fragment_location) {

    private var binding: FragmentLocationBinding? = null
    var adapterFolderPhoto: LocationAdapter = LocationAdapter()
    private val mViewModel = LocationViewModel(SavedStateHandle())
    private lateinit var mCurrentFolderPhotoDto: FolderPhotoDto
    private val bundle = Bundle()
    private lateinit var mProcess: FrameLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLocationBinding.bind(view)

        initUI()
        initEvent()

        mViewModel.getLocationDoc()  // init request to Firestore - get location(name)
        mViewModel.getListFolders(false)  // init request to Firestore and Storage - get all data

        mViewModel.mImageFromGallery.observe(viewLifecycleOwner) {
            when (it) {
                is Outcome.Progress -> {
                    if (it.loading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }
                is Outcome.Next -> {
                    hideProgress()
                }
                is Outcome.Failure -> {
                    showError(it.t.message.toString())
                }
                is Outcome.Success -> {
                    hideProgress()
                    adapterFolderPhoto.repopulateItem(it.data)
                }
            }
        }

        mViewModel.mLocationName.observe(viewLifecycleOwner) {
            val name = SpannableStringBuilder(it)
            binding?.mETFolderName?.text = name
        }
        mViewModel.mListData.observe(viewLifecycleOwner) {
            when (it) {
                is Outcome.Progress -> {
                    if (it.loading) {
                        showProgress()
                    } else {
                        hideProgress()
                    }
                }
                is Outcome.Next -> {
                    hideProgress()
                }
                is Outcome.Failure -> {
                    showError(it.t.message.toString())
                }
                is Outcome.Success -> {
                    hideProgress()
                    if (it.data.size > 0 && it.data[0].isChosen) {
                        binding?.mButtonDelete?.visibility = View.VISIBLE
                    }
                    adapterFolderPhoto.repopulate(it.data)
                }
            }
        }
    }

    /**
     * get image from gallery  result: URI
     */
    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                val folderPhotoDto =
                    mCurrentFolderPhotoDto.setImageToListImages(result) //item where add image
                mViewModel.setImageFromGallery(folderPhotoDto)  //add for refresh ui and add ti firestore
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initUI() {
        binding?.mRVListLocation?.layoutManager = LinearLayoutManager(context)
        binding?.mRVListLocation?.adapter = adapterFolderPhoto
        binding?.mPBarList?.let { mProcess = it }

    }

    private fun initEvent() {

        //SwipeRefresh
        binding?.mSRLocations?.setOnRefreshListener {
            mViewModel.getListFolders(true)
        }

        //edit location folder
        binding?.mETFolderName?.doAfterTextChanged {
            val name = it?.append()
            mViewModel.getLocationName(name.toString())
        }

        //Fab -  add folder
        binding?.mFABLocation?.setOnClickListener {
            val newFolder =
                FolderPhotoDto(DEFAULT_STRING, NAME, mutableListOf(), false)
            mViewModel.addNewFolder(newFolder) // add to firestore
            adapterFolderPhoto.addItem(newFolder)  // add to LocationAdapter
        }

        /**
         * click listener item adapter's  $GET_IMAGE - add image from gallery; $DELETE_IMAGE -
         * longClickListener - set Delete mode
         */

        adapterFolderPhoto.setItemClick { key, item ->
            when (key) {
                GET_IMAGE -> {
                    mCurrentFolderPhotoDto = item
                    activityLauncher.launch("image/*")
                }
                DELETE_IMAGE -> {
                    binding?.mButtonDelete?.visibility = View.VISIBLE
                    adapterFolderPhoto.refresh()

                }
            }
        }

        /**
         * click listener on each image for to get image on fullScreen
         */
        adapterFolderPhoto.setItemImageClick { key, _, uri ->
            when (key) {
                FULL_SCREEN_IMAGE -> {
                    bundle.putParcelable(KEY_BUNDLE_NAVIGATE_URI_IMAGE, uri)
                    findNavController().navigate(
                        R.id.action_locationFragment_to_fullScreenFragment,
                        bundle
                    )
                }
            }
        }

        /**
         * click listener for delete images if are selected  and refresh adapter
         */
        binding?.mButtonDelete?.setOnClickListener {
            val listAdapter = adapterFolderPhoto.mDataList
            adapterFolderPhoto.mDataList.map { it.isChosen = false }
            mViewModel.deleteImageDocFromFBAndStorage(listAdapter)
            binding?.mButtonDelete?.visibility = View.GONE // close delete mode
        }

        /**
         * edit listener folder name
         */
        adapterFolderPhoto.setResultChanges { folderPhoto ->
            mViewModel.updateNameFolder(folderPhoto)
        }
    }

    fun showProgress() {
        mProcess.visibility = View.VISIBLE
    }

    fun hideProgress() {
        mProcess.visibility = View.GONE
        binding?.mSRLocations?.isRefreshing = false
    }

    fun showError(error: String) {
        binding?.let { Snackbar.make(it.root, error, Snackbar.LENGTH_SHORT).show() }
    }
}

