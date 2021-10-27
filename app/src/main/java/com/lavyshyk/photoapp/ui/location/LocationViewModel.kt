package com.lavyshyk.photoapp.ui.location


import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lavyshyk.photoapp.*
import com.lavyshyk.photoapp.base.BaseViewModel
import com.lavyshyk.photoapp.model.FolderPhotoDto
import com.lavyshyk.photoapp.model.LocationFolder
import com.lavyshyk.photoapp.model.Outcome
import com.lavyshyk.photoapp.model.setRefNameDocument
import com.lavyshyk.photoapp.utils.*


class LocationViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel(savedStateHandle) {

    private var mFirestore: FirebaseFirestore
    private var mStorage: FirebaseStorage
    private var mStorageReference: StorageReference
    private var mStorageReferenceImages: StorageReference
    private var mRefLocationCollection: CollectionReference
    private var mRefLocationCollectionFolders: CollectionReference
    var mLocationFolder = LocationFolder(FOLDER_NAME, "")
    var mListData =
        savedStateHandle.getLiveData<Outcome<MutableList<FolderPhotoDto>>>(LIST_OF_PHOTO_FOLDERS)
    val mImageFromGallery = savedStateHandle.getLiveData<Outcome<FolderPhotoDto>>(KEY_GET_IMAGE_FROM_GALLERY)
    val mLocationFolderName = savedStateHandle.getLiveData<String>(LOCATION_FOLDER_NAME)
    val mLocationName = savedStateHandle.getLiveData<String>(LOCATION_NAME)

    init {
        //Firestore
        mFirestore = FirebaseFirestore.getInstance()
        mRefLocationCollection = mFirestore.collection("location")
        mRefLocationCollectionFolders =
            mRefLocationCollection.document(LOCATION_1).collection(FOLDERS)
        //Storage
        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage.reference
        mStorageReferenceImages = mStorageReference.child("images")
    }

    /**
     * add for refresh ui and add to Firestore and Storage
     */
    fun setImageFromGallery(folderPhotoDto: FolderPhotoDto) {
        putNewImageToStorage(folderPhotoDto)
    }

    fun  getLocationName(name: String) {
        mLocationFolderName.value = name
        mLocationFolder.nameLocationFolder = name
        updateNameLocation(mLocationFolder)  // add Firestore
    }

    /**
     * update location name + set id reference to document
     */
    private fun updateNameLocation(locationFolder: LocationFolder) {
        val locationDoc =
            mRefLocationCollection.document(LOCATION_1) //  LOCATION_1  id document  for test
        locationFolder.idLocFolder = locationDoc.id
        locationDoc.set(locationFolder)
            .addOnSuccessListener { Log.e(TAG, "SUCCESS ADD LOCATION NAME") }
            .addOnFailureListener { Log.e(TAG, "FAILURE ADD LOCATION NAME + $it") }
    }

    fun addNewFolder(folderPhotoDto: FolderPhotoDto) {
        val document = mRefLocationCollectionFolders.document()
        folderPhotoDto.idFolderDocument = document.id
        document.set(folderPhotoDto.transformToFolderPhotoFB())
            .addOnFailureListener { Log.e(TAG, "FAILURE ADD NEW FOLDER")}
            .addOnSuccessListener {Log.e(TAG, "SUCCESS ADD NEW FOLDER") }
    }

    /**
     * update folder name and save to Firestore
     */
    fun updateNameFolder(folderPhotoDto: FolderPhotoDto) {
        val idFolderDocument = folderPhotoDto.idFolderDocument
        mRefLocationCollectionFolders.document(idFolderDocument)
            .set(folderPhotoDto.transformToFolderPhotoFB())
            .addOnSuccessListener { Log.e(TAG, "SUCCESS ADD NEW FOLDER NAME") }
            .addOnFailureListener { Log.e(TAG, "FAILURE ADD NEW FOLDER NAME") }
    }

    fun getLocationDoc() {
        mRefLocationCollection.document("location1")
            .get().addOnSuccessListener { snapshot ->
                val locationFolder = snapshot.data?.get("nameLocationFolder").toString()
                mLocationName.value = locationFolder
            }
            .addOnFailureListener { Log.e(TAG, "FAILURE GET LOCATION DOCUMENT") }
    }

    /**
     * get list $FolderPhoto for recyclerView
     */
    fun getListFolders(isRefresh : Boolean = false) {
        val listOut = mutableListOf<FolderPhotoDto>()
        mListData.loading(!isRefresh)
        mRefLocationCollectionFolders
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot?.let {
                    snapshot.documents.map { documentSnapshot ->
                        val folder = documentSnapshot.transformToFolderPhotoDto()
                        listOut.add(folder)
                    }
                }
                if (listOut.size != 0) {
                    getReferenceFromFirestore(listOut)
                } else {
                    mListData.success(listOut)
                }

            }
            .addOnFailureListener {
                mListData.failed(it)
                Log.e(TAG, "FAILURE GET DATA from Firestore + $it")
            }
    }

    /**
     * get list references for $FolderPhoto.listImage
     */
    fun getReferenceFromFirestore(list: MutableList<FolderPhotoDto>) {
        mListData.loading(true)
        list.map { folder ->
            mRefLocationCollectionFolders
                .document(folder.idFolderDocument)
                .collection(IMAGES)
                .get()
                .addOnSuccessListener {
                    it.documents.map { snapDoc ->
                        val image = snapDoc.transformToImageDto()
                        folder.listImage.add(image)
                    }
                    mListData.success(list)
                }
                .addOnFailureListener {
                    mListData.failed(it)
                    Log.e(TAG, "FAILURE GET DATA  from Storage + $it")
                }
        }
    }

    /**
     * add image to firestore(reference to Storage) and upload image in Storage
     */
    fun putNewImageToStorage(folderPhotoDto: FolderPhotoDto) {
        mImageFromGallery.loading(true)
        val docRefOnImage = mRefLocationCollectionFolders
            .document(folderPhotoDto.idFolderDocument)
            .collection(IMAGES)
            .document()

        val imageDto = folderPhotoDto.listImage.last() // last added image from gallery
        var uri = imageDto.imageUri
        val fileName = System.currentTimeMillis().toString() + ".jpeg"

        val storageReferenceCurrent =
            mStorageReferenceImages.child(fileName)// reference to Storage/child folder "images"

        //save to Firestore fileName

        docRefOnImage.set(imageDto.transformToImageFB(fileName, docRefOnImage.id))
            .addOnFailureListener {
                mImageFromGallery.failed(it)
                Log.e(TAG, "FAILURE ADD REFERENCE for Image to Firestore ")
            }
            .addOnSuccessListener {
                folderPhotoDto.listImage.removeLast()
                imageDto.setRefNameDocument(docRefOnImage.id, fileName.toUri())
                folderPhotoDto.listImage.add(imageDto)
                Log.e(TAG, "SUCCESS ADD REFERENCE for Image to Firestore ")
            }
        // save to Storage
        storageReferenceCurrent.putFile(uri)
            .addOnSuccessListener {
                mImageFromGallery.success(folderPhotoDto)
            }
            .addOnFailureListener {
                mImageFromGallery.failed(it)
                Log.e(TAG, "FAILURE IMAGE TO STORAGE + $it") }
    }

    fun deleteImageDocFromFBAndStorage(list: MutableList<FolderPhotoDto>) {
        list.forEach { folderPhotoDto ->
            folderPhotoDto.listImage.forEach { image ->
                if (image.isSelect && image.nameDocument.isNotEmpty()) {
                    mRefLocationCollectionFolders
                        .document(folderPhotoDto.idFolderDocument)
                        .collection(IMAGES)
                        .document(image.nameDocument)
                        .delete()
                        .addOnSuccessListener {
                            mStorageReferenceImages
                                .child(image.imageUri.toString())
                                .delete()
                                .addOnSuccessListener {Log.e(TAG, "SUCCESS DELETE IMAGE") }
                                .addOnFailureListener {Log.e(TAG, "FAILURE  DELETE IMAGE") }
                        }
                        .addOnFailureListener {Log.e(TAG, "FAILURE  DELETE IMAGE DOCUMENT") }
                }
            }
        }
        getListFolders(false)
    }


}


