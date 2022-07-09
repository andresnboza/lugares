package com.example.lugares.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lugar (
    var id: String,
    val nombre: String
): Parcelable {
    constructor(): this("", "")
}