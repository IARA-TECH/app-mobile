package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

class UserPhotoResponse (
   @SerializedName("urlBlob")
   val urlBlob: String,

   @SerializedName("userId")
   val userId: String
)


