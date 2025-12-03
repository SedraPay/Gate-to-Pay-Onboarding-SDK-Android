package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import  com.gatetopay.onboardingapplication.R
import  com.gatetopay.onboardingapplication.adapters.ProductsArrayAdapter
import  com.gatetopay.onboardingapplication.databinding.FragmentSelectNationalityBinding
import  com.gatetopay.onboardingapplication.databinding.FragmentSelectProductBinding
import  com.gatetopay.onboardingapplication.view_models.CheckViewModel


class SelectProductFragment : Fragment() {
    private val binding: FragmentSelectProductBinding by lazy {
        FragmentSelectProductBinding.inflate(
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

        model.requestProducts()

        model.getProducts().observe(viewLifecycleOwner) {
            if (it != null) {
                with(binding) {
                    progressIndicator.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    spinner.adapter = ProductsArrayAdapter(requireContext(), it)
                }
            }
        }

        model.getKycForm().observe(viewLifecycleOwner){
            binding.btnNext.visibility = View.VISIBLE
            binding.progressIndicator.visibility = View.GONE
            Navigation.findNavController(view)
                .navigate(R.id.action_SelectProductFragment_to_MoreEditableKyc)
        }

        model.getFormInfo().observe(viewLifecycleOwner) {
            if (it != null) {
                if ((it.integrationInfos ?: emptyList()).isEmpty()) {

                    if(model.isNeedOCR){
                        Navigation.findNavController(view)
                            .navigate(R.id.action_SelectProductFragment_to_SelectIdTypeFragment)
                    }else {
                        model.requestKycForm(emptyList())


                    }

                } else {
                    Navigation.findNavController(view)
                    .navigate(R.id.action_SelectProductFragment_to_IntegrationInfoFragment)
                }

            }
        }

        with(binding) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                   // model.setSelectedProduct(position)
                }
            }

            btnNext.setOnClickListener {
                model.requestFormInfoByProduct()
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
            }
        }
    }
}