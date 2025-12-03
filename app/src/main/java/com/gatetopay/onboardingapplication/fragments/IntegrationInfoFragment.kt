package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.databinding.FragmentSelectIdTypeBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel
import com.gatetopay.onboardingsdk.ScanResult
import com.gatetopay.onboardingsdk.ScannerResultContract
import com.gatetopay.onboardingsdk.network.models.DataExtractionModel
import com.gatetopay.onboardingsdk.network.models.IdType
import com.gatetopay.onboardingapplication.adapters.IntegrationInfoRecyclerAdapter
import com.gatetopay.onboardingapplication.databinding.FragmentIntegrationInfoBinding
import com.gatetopay.onboardingapplication.exeptions.KycException


class IntegrationInfoFragment : Fragment() {

    private val binding: FragmentIntegrationInfoBinding by lazy {
        FragmentIntegrationInfoBinding.inflate(
            layoutInflater
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    var info: IntegrationInfoRecyclerAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: CheckViewModel by activityViewModels()


        //model.requestFormInfoByProduct()


        model.getKycForm().observe(viewLifecycleOwner) {
            binding.btnNext.visibility = View.VISIBLE
            binding.progressIndicator.visibility = View.GONE
            Navigation.findNavController(view)
                .navigate(R.id.action_IntegrationInfoFragment_to_moreEditableKyc)
        }


        model.getFormInfo().observe(viewLifecycleOwner) {
            if (it != null) {
                with(binding) {
                    info =
                        IntegrationInfoRecyclerAdapter(items = it?.integrationInfos ?: emptyList())
                    rvIdTypes.setLayoutManager(LinearLayoutManager(context))
                    rvIdTypes.setAdapter(info)
                }
            }
        }



        with(binding) {

            btnNext.setOnClickListener {
                if (checkbox.isChecked) {
                    try {
                        model.setUpdatedInfo(info?.getUpdatedItems() ?: emptyList())
                        if (model.isNeedOCR) {
                            Navigation.findNavController(view)
                                .navigate(R.id.action_IntegrationInfoFragment_to_selectIdTypeFragment)
                        } else {
                            binding.btnNext.visibility = View.GONE
                            binding.progressIndicator.visibility = View.VISIBLE
                            model.requestKycForm(model.updateIntegrationFieldsItems ?: emptyList())
                        }

                    } catch (e: KycException) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
    }
}
