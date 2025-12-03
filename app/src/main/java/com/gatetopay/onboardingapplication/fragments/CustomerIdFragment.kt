package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentCustomerIdBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class CustomerIdFragment : Fragment() {
    private val binding: FragmentCustomerIdBinding by lazy {
        FragmentCustomerIdBinding.inflate(
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

        binding.btnNext.setOnClickListener {
            binding.tilCustomerId.error = null
            if (binding.etCustomerId.text.toString().isEmpty()) {
                binding.tilCustomerId.error = "This field is mandatory"
            } else {
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
                model.closeJourney()
            }

        }

        model.getCloseJourney().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.btnNext.visibility = View.VISIBLE
                binding.progressIndicator.visibility = View.GONE

                Navigation.findNavController(view)
                    .navigate(R.id.action_customerIdFragment_to_kycSubissionSuccessfulFragment)
            }
        }
    }
}