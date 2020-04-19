package com.fiuba.cuarentenainteligente.view.activities.contact

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.model.contact.Person
import com.fiuba.cuarentenainteligente.model.location.LocationModel
import com.fiuba.cuarentenainteligente.utils.GpsUtils
import com.fiuba.cuarentenainteligente.utils.GpsUtils.Companion.GPS_REQUEST
import com.fiuba.cuarentenainteligente.utils.GpsUtils.Companion.LOCATION_REQUEST
import com.fiuba.cuarentenainteligente.view.fragments.contact.ContactCongrats
import com.fiuba.cuarentenainteligente.view.fragments.contact.ContactSelectorFragment
import com.fiuba.cuarentenainteligente.view.fragments.contact.QrReaderFragment
import com.fiuba.cuarentenainteligente.viewmodel.location.LocationViewModel

class ContactActivity : AppCompatActivity() {
    private lateinit var locationViewModel: LocationViewModel
    private var isGPSEnabled = false
    private var latestLocation: LocationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@ContactActivity.isGPSEnabled = isGPSEnable
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.contact_container, ContactSelectorFragment.newInstance())
            }
        }

    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    invokeLocationAction()
                }
                return
            }

        }
    }

    private fun invokeLocationAction() {
        if (checkPermissions()) {
            startLocationUpdate()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_REQUEST
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.location_permission_request_title)
            builder.setMessage(R.string.location_permission_request_message)

            builder.setPositiveButton(R.string.acept) { _, _ ->
                requestPermissions()
            }

            builder.setOnDismissListener {
                requestPermissions()
            }
            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        } else {
            return true
        }
    }

    private fun startLocationUpdate() {
        locationViewModel.getLocationData().observe(this, Observer {
            it?.let {
                latestLocation = it
            }
        })

    }

    fun replaceToQrFragment() {
        supportFragmentManager.commit {
            addToBackStack(QrReaderFragment.TAG)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.contact_container, QrReaderFragment.newInstance())
        }
    }

    fun replaceToContactCongratsFragment(person: Person) {
        supportFragmentManager.commit {
            addToBackStack(QrReaderFragment.TAG)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            locationViewModel.getLocationData().removeObservers(this@ContactActivity)
            replace(R.id.contact_container, ContactCongrats.newInstance(person, latestLocation))
        }
    }

    override fun onBackPressed() {
        var mustFinish = false
        //workaround to finalize the congrats flow
        if (supportFragmentManager.fragments.size > 0) {
            supportFragmentManager.fragments.forEach {
                if (it is ContactCongrats) {
                    mustFinish = true
                }
            }
        }
        if (mustFinish) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}