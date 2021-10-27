package com.lavyshyk.photoapp.model


import android.net.Uri

data class FolderPhotoDto(
    var idFolderDocument: String,
    var nameFolderPhoto: String,
    var listImage: MutableList<ImageDto> = mutableListOf(),
    var isChosen: Boolean
) {
    data class ImageDto(
        var imageUri: Uri,
        var nameDocument: String,
        var isSelect: Boolean
    )
}

fun FolderPhotoDto.setImageToListImages(image: Uri) =
    this.also {
        it.listImage.add(FolderPhotoDto.ImageDto( image, "",false))

    }

fun FolderPhotoDto.ImageDto.setRefNameDocument(nameDoc: String, fileName: Uri){
    this.nameDocument = nameDoc
    this.imageUri = fileName
}


