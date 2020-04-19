package com.fiuba.cuarentenainteligente.view.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.view.activities.contact.ContactActivity
import kotlinx.android.synthetic.main.fragment_contact_selector.*

class ContactSelectorFragment : Fragment() {
    companion object {

        fun newInstance(): ContactSelectorFragment {
            return ContactSelectorFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_selector, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        qr_contact_button.setOnClickListener {
            (activity as ContactActivity).replaceToQrFragment()
        }
        manual_contact_button.setOnClickListener {

        }
    }
}