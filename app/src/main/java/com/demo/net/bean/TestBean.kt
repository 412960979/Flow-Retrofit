package com.demo.net.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TestBean(
    val from: String,
    val name: String
) : Parcelable