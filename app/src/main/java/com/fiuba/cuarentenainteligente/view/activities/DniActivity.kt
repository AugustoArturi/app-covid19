package com.fiuba.cuarentenainteligente.view.activities

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.model.User
import com.fiuba.cuarentenainteligente.model.UserRegister
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_dni.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class DniActivity : AppCompatActivity() {

    private lateinit var mAuthDatabase: FirebaseDatabase
    private var tokenPush : String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dni)


        btnSend.setOnClickListener {
            if (validation())
                sendDataToDB()
        }

        btnRegister.setOnClickListener {
            if (validation2())
                registerDataToDB()
        }
    }

    private fun validation2(): Boolean {
        if (etDniRegister.text.toString().isEmpty()) {
            etDniRegister.error = "Ingresa tu dni"
            etDniRegister.requestFocus()
            return false
        }

        if (etState.text.toString().isEmpty()) {
            etState.error = "Ingresa tu estado"
            etState.requestFocus()
            return false
        }

        return true
    }

    private fun registerDataToDB() {
        val dni = etDniRegister.text.toString()
        val state = etState.text.toString()
        val token =  FirebaseInstanceId.getInstance().getToken()!!
        val registerdata =
            UserRegister(token, dni, state)
        mAuthDatabase = FirebaseDatabase.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/register/$dni")
        ref.setValue(registerdata)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Contacto registrado a la BD", Toast.LENGTH_SHORT).show()
            }
        etState.text.clear()
    }


    private fun validation(): Boolean {
        if (etMyDni.text.toString().isEmpty()) {
            etMyDni.error = "Ingresa tu dni"
            etMyDni.requestFocus()
            return false
        }

        if (etMeetDni.text.toString().isEmpty()) {
            etMeetDni.error = "Ingresa el dni de contacto"
            etMeetDni.requestFocus()
            return false
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendDataToDB() {
        val dni = etMyDni.text.toString()
        val dniMeet = etMeetDni.text.toString()
        val time = DateTimeFormatter.ofPattern("dd-MM-yyyy ss:mm:HH").withZone(ZoneOffset.UTC).format(Instant.now())
        val userdata =
            User(dni, dniMeet, time)
        val random = randomAlphaNumericString(20)
        mAuthDatabase = FirebaseDatabase.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/interactions/$random")
        ref.setValue(userdata)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Contacto agregado a la BD", Toast.LENGTH_SHORT).show()

            }
        etMeetDni.text.clear()


    }

    fun randomAlphaNumericString(desiredStrLength: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..desiredStrLength)
            .map{ kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    // Read from the database
  /*  ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = dataSnapshot.getValue<String>()
            Log.d(TAG, "Value is: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    })*/

}
