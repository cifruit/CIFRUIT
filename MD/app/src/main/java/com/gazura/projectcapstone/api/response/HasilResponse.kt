package com.gazura.projectcapstone.api.response

import com.google.gson.annotations.SerializedName

data class HasilResponse(

	@field:SerializedName("image_name")
	val imageName: String? = null,

	@field:SerializedName("confidence")
	val confidence: String? = null,

	@field:SerializedName("predicted_class")
	val predictedClass: String? = null,

	@field:SerializedName("recommendation")
	val recommendation: String? = null
)
