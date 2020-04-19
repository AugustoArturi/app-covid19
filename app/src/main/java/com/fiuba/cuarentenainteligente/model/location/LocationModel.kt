package com.fiuba.cuarentenainteligente.model.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class LocationModel(
    val longitude: Double,
    val latitude: Double,
    val accuracy: Float,
    val time: Date
) : Parcelable