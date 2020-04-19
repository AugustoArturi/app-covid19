package com.fiuba.cuarentenainteligente.view.fragments.contact

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.model.contact.Person
import com.fiuba.cuarentenainteligente.view.activities.contact.ContactActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.fragment_qr_reader.*


class QrReaderFragment : Fragment() {

    companion object {
        val TAG = QrReaderFragment::class.java.simpleName
        const val CAMERA_PERMISSION = 1001
        const val TORCH_ENABLED_SAVED = "torchEnabledSaved"

        fun newInstance(): QrReaderFragment {
            return QrReaderFragment()
        }
    }

    private var barcodeView: DecoratedBarcodeView? = null
    private lateinit var beepManager: BeepManager
    private var torchEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr_reader, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            torchEnabled = it.getBoolean(TORCH_ENABLED_SAVED)
        }
        if (checkPermissions()) {
            startCamera()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION
            )
        }

    }

    private fun startCamera() {
        barcodeView = view?.findViewById(R.id.barcode_scanner)
        val formats: Collection<BarcodeFormat> =
            listOf(BarcodeFormat.QR_CODE, BarcodeFormat.PDF_417)
        barcodeView?.barcodeView?.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView?.decodeSingle {
            if (it?.result?.text != null) {
                val splitedText = it.result.text.split("@")
                if (splitedText.size == 8) {
                    beepManager.playBeepSoundAndVibrate()
                    val person = Person(
                        splitedText[4],
                        splitedText[2],
                        splitedText[1],
                        splitedText[3],
                        splitedText[6]
                    )
                    (activity as ContactActivity).replaceToContactCongratsFragment(person)
                }
            }
        }

        beepManager = BeepManager(activity)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        if (torchEnabled) {
            barcodeView?.setTorchOn()
        } else {
            barcodeView?.setTorchOff()
        }
        torch.setOnClickListener {
            torchEnabled = if (torchEnabled) {
                barcodeView?.setTorchOff()
                false
            } else {
                barcodeView?.setTorchOn()
                true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(TORCH_ENABLED_SAVED, torchEnabled)
    }

    override fun onResume() {
        super.onResume()
        barcodeView?.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView?.pause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startCamera()
                } else {
                    camera_container.visibility = View.GONE
                    camera_error_message.visibility = View.VISIBLE
                }
                return
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

}