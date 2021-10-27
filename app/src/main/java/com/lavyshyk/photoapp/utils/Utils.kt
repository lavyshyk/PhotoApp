package com.lavyshyk.photoapp.utils

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import coil.ImageLoader
import coil.api.load
import coil.decode.Decoder
import coil.decode.SvgDecoder
import coil.request.LoadRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.lavyshyk.photoapp.model.FolderPhotoDto
import com.lavyshyk.photoapp.model.FolderPhotoFB
import com.lavyshyk.photoapp.model.ImageFB
import java.util.*


fun DocumentSnapshot.transformToFolderPhotoDto() =
    FolderPhotoDto(
       // this.data?.get("idFolder").toString().toInt(),
        this.data?.get("idFolderDocumentFB").toString(),
        this.data?.get("nameFolderPhotoFB").toString(),
        mutableListOf(),
        this.data?.get("chosenFB") as Boolean,
    )

fun DocumentSnapshot.transformToImageDto() =
    FolderPhotoDto.ImageDto(
        //this.data?.get("idFolderFB").toString().toInt(),
        this.data?.get("imageUriFB").toString().toUri(),
        this.data?.get("nameDocument").toString(),
        false,
        )


fun FolderPhotoDto.transformToFolderPhotoFB() =
    FolderPhotoFB(
      //  this.idFolder,
        this.idFolderDocument,
        this.nameFolderPhoto,
        this.isChosen
    )

fun FolderPhotoDto.ImageDto.transformToImageFB(reference: String, nameDocument: String) =
    ImageFB(
       // this.idFolder,
        reference,
        nameDocument
    )

