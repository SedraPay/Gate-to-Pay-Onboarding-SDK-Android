package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import  com.gatetopay.onboardingapplication.R
import  com.gatetopay.onboardingapplication.databinding.FragmentSelfieInstructionsBinding
import  com.gatetopay.onboardingapplication.view_models.CheckViewModel

import com.gatetopay.onboardingsdk.LivenessCheckResultContract
import com.gatetopay.onboardingsdk.LivenessConfiguration
import com.gatetopay.onboardingsdk.enums.FaceDetectionStep

class SelfieInstructionsFragment : Fragment() {
    private val binding: FragmentSelfieInstructionsBinding by lazy {
        FragmentSelfieInstructionsBinding.inflate(
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
        model.getLivenessCheck().observe(viewLifecycleOwner) {

            if(it != null) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_selfieInstructionsFragment_to_imagesVerificationSuccessfulFragment)

                binding.btnTakeSelfie.visibility = View.VISIBLE
                binding.progressIndicator.visibility = View.GONE
            }
        }

        //NOTE
        //The LivenessCheck in this SDK relies on Google Services, if you plan on publishing your
        //app on platforms that don't have google services available, you should handle the liveness
        //check in a different way, and just send the path of the resulting selfie image to
        //      SedraCheck.startSelfieMatch()
        //which will match the selfie image to the image on the person's ID document.

        val getLivenessCheckResult =
            registerForActivityResult(LivenessCheckResultContract()) { livenessResult ->
                if (livenessResult != null && livenessResult.isLive && livenessResult.path != null) {
                    binding.btnTakeSelfie.visibility = View.GONE
                    binding.progressIndicator.visibility = View.VISIBLE

                    model.startSelfieMatch(livenessResult.path!!)
                } else {
                    // handle no result returned or isLive = false
                    binding.btnTakeSelfie.visibility = View.VISIBLE
                    binding.progressIndicator.visibility = View.GONE
                }
            }

        binding.btnTakeSelfie.setOnClickListener {
            binding.btnTakeSelfie.visibility = View.GONE
            val steps = ArrayList<FaceDetectionStep>().apply {
                //The order these steps will be displayed to the end-user will always be random,
                //except that the SMILE step will always be the last one.
                add(FaceDetectionStep.BLINK)
                add(FaceDetectionStep.LOOK_LEFT)

                //Adding the same step multiple times will be ignored, and it will be shown to the
                //end-user only once.
                add(FaceDetectionStep.LOOK_RIGHT)
                add(FaceDetectionStep.LOOK_RIGHT)

                //Note that SMILE step will always be added, even if you don't include it, and will
                //always be the last step shown to the user, this is to make sure the captured
                //selfie image is the best possible.
                add(FaceDetectionStep.SMILE)

            }

            val isRandom = binding.rbRandomized.isChecked

            val config = LivenessConfiguration(steps, isRandom)

            getLivenessCheckResult.launch(config)
        }

    }
}