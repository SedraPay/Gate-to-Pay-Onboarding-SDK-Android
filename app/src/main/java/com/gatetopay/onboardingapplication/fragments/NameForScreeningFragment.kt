package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentNameForScreeningBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel

import  com.gatetopay.onboardingsdk.network.models.ScreeningRequestModel


class NameForScreeningFragment : Fragment() {

    private val binding: FragmentNameForScreeningBinding by lazy {
        FragmentNameForScreeningBinding.inflate(
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

        with(binding) {

            etFirstName.setText(
                model.getScanDoc().value?.extractedFields?.find { it.name == "Full Name" }?.value?.split(
                    " "
                )?.first() ?: ""
            )
            etLastName.setText(
                model.getScanDoc().value?.extractedFields?.find { it.name == "Full Name" }?.value?.split(
                    " "
                )?.last() ?: ""
            )

            btnNext.setOnClickListener {
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
                val screeningRequestModel = ScreeningRequestModel(
                    firstName = binding.etFirstName.text.toString(),
                    secondName = binding.etSecondName.text.toString(),
                    thirdName = binding.etThirdName.text.toString(),
                    lastName = binding.etLastName.text.toString()
                )
                model.checkScreening(screeningRequestModel)
            }
        }


        with(model) {
            getScreeningCheck().observe(viewLifecycleOwner) {
                if (it != null) {


                    binding.btnNext.visibility = View.VISIBLE
                    binding.progressIndicator.visibility = View.GONE

                }
            }
        }
    }
}