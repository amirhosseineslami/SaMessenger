package com.mintab.samessenger.worker

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.net.URI

class GetFileTypeExtension() {
    companion object{
        public fun getFileType(context: Context, uri: Uri): String? {
            val mimeTypeMap = MimeTypeMap.getSingleton()
            return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(uri))
        }
    }
}