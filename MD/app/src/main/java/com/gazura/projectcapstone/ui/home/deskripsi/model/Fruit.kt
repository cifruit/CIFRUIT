package com.gazura.projectcapstone.ui.home.deskripsi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fruit(
    val name: String,
    val description: String,
    val imageResId: Int
) : Parcelable