package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentImagesVerificationSuccessfulBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class ImagesVerificationSuccessfulFragment : Fragment() {
    private val binding: FragmentImagesVerificationSuccessfulBinding by lazy {
        FragmentImagesVerificationSuccessfulBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.progressIndicator.visibility = View.INVISIBLE
        binding.btnNext.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model: CheckViewModel by activityViewModels()

        model.getKycForm().observe(viewLifecycleOwner){
            binding.progressIndicator.visibility = View.INVISIBLE
            binding.btnNext.visibility = View.VISIBLE

                Navigation.findNavController(view)
                    .navigate(R.id.action_imagesVerificationSuccessfulFragment_to_moreEditableKyc)

        }

        binding.btnNext.setOnClickListener {
            binding.btnNext.visibility = View.GONE
            binding.progressIndicator.visibility = View.VISIBLE
            model.requestKycForm( emptyList())



        }
    }
}