package com.gatetopay.onboardingapplication.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentKycSubmissionSuccessfulBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class KycSubmissionSuccessfulFragment : Fragment() {
    private val binding: FragmentKycSubmissionSuccessfulBinding by lazy {
        FragmentKycSubmissionSuccessfulBinding.inflate(
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

        model.getCloseJourney().observe(viewLifecycleOwner) {
            if (it != null) {
                val intent = Intent()
                intent.putExtra("GUID", model.getSedraCheckJourney().value)
                requireActivity().setResult(Activity.RESULT_OK, intent)
                requireActivity().finish()
            }
        }
        binding.btnNext.setOnClickListener {
            model.closeJourney()

        }
    }
}