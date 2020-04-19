package com.fiuba.cuarentenainteligente.view.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fiuba.cuarentenainteligente.R
import com.fiuba.cuarentenainteligente.model.contact.Person
import com.fiuba.cuarentenainteligente.model.location.LocationModel
import kotlinx.android.synthetic.main.fragment_contact_congrats.*

class ContactCongrats : Fragment() {
    companion object {

        const val PERSON_RESULT_ARG = "personResultArg"
        const val LOCATION_MODEL_ARG = "locationModelArg"

        fun newInstance(
            person: Person,
            latestLocation: LocationModel?
        ): ContactCongrats {
            val fragment = ContactCongrats()
            val bundle = Bundle().apply {
                putParcelable(PERSON_RESULT_ARG, person)
                putParcelable(LOCATION_MODEL_ARG, latestLocation)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_congrats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val person = arguments?.getParcelable(PERSON_RESULT_ARG) as Person?
        val location = arguments?.getParcelable(LOCATION_MODEL_ARG) as LocationModel?
        congrats_text.text = "${person},\n\n\n${location}"
    }


}