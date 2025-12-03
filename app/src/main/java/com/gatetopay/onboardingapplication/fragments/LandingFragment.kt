package com.gatetopay.onboardingapplication.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentLandingBinding
import com.gatetopay.onboardingapplication.models.CheckConfigration
import com.gatetopay.onboardingapplication.view_models.CheckViewModel
import kotlin.random.Random


class LandingFragment : Fragment() {

    private val binding: FragmentLandingBinding by lazy {
        FragmentLandingBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: CheckViewModel by activityViewModels()

        model.getSedraCheckJourney().observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = View.GONE

            if (it != null && it.isNotEmpty()) {

                //The services of SedraCheck are flexable and usually don't rely on each other that
                // much, this is why the landing page has a few Switches to allow you to select
                // which features you want to include in the current journey.
                //This will control the navigation path.


                Navigation.findNavController(view)
                    .navigate(R.id.action_landingFragment_to_editableKYCFormFragment)

            }
        }


        val configration: CheckConfigration? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent?.extras?.getSerializable("config", CheckConfigration::class.java)
        } else {
            requireActivity().intent?.extras?.getSerializable("config") as CheckConfigration?
        }
        model.isNeedOCR = configration?.needOCR ?: false
        if(configration?.customerID.isNullOrEmpty()){
            model.customerId = Random(System.currentTimeMillis()).nextInt().toString()
        }else {
            model.customerId =
                configration?.customerID?:""
        }
        model.startJourney(
            configration?.subscriptionKey?:"",
            configration?.baseUrl?:"",
            requireActivity(),
            configration?.nationalID?:"",
            configration?.riskID?:"",

        )



    }





}