package com.example.fetchmydataapp.userInterface.navigation

import android.net.Uri

object Routes{
    const val HOME = "home"
    const val VIEWFILES = "view_files"
    const val UPLOAD = "upload?path={path}"
    const val DOWNLOAD = "download"

    fun upload(path: String = "") = "upload?path=${Uri.encode(path)}"
}
