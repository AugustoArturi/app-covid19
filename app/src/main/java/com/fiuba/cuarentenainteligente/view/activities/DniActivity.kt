package com.fiuba.cuarentenainteligente.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.model.User
import com.fiuba.cuarentenainteligente.model.UserRegister
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_dni.*
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class DniActivity : AppCompatActivity() {

    private lateinit var mAuthDatabase: FirebaseDatabase

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dni)
        setLocation()

        btnSend.setOnClickListener {
            if (validation())
                sendDataToDB()
        }

        btnRegister.setOnClickListener {
            if (validation2())
                registerDataToDB()
        }
    }

    private fun setLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        //findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        tv_location.text = address.get(0).getAddressLine(0)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
           // findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            //findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
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
