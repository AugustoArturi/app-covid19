package com.fiuba.cuarentenainteligente

object Moi {



    private var current_dni: String = ""


    fun setCurrentDni(dni: String) {
        current_dni = dni
    }

    fun getCurrentDni():String{
        return current_dni
    }

}