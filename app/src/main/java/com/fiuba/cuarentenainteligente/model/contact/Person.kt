package com.fiuba.cuarentenainteligente.model.contact

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person(
    val dni: String,
    val name: String,
    val lastName: String,
    val gender: String,
    val birthdate: String
) : Parcelable