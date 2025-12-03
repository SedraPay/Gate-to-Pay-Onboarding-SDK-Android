package com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.OcrResultsAdapter
import com.gatetopay.onboardingapplication.databinding.FragmentOcrResultsBinding
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class OcrResultsFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: OcrResultsAdapter

    private val binding: FragmentOcrResultsBinding by lazy {
        FragmentOcrResultsBinding.inflate(
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
            linearLayoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = linearLayoutManager

            adapter = OcrResultsAdapter(
                model.getScanDoc().value?.extractedFields ?: arrayListOf(),
                model.getScanDoc().value?.validationResult?.validationChecks ?: arrayListOf()
            )

            recyclerView.adapter = adapter

            imageView.setOnClickListener {
                model.resetDataExtraction()
                Navigation.findNavController(view)
                    .navigate(R.id.action_OcrResultFragment_to_selectIdTypeFragment)
            }

            model.selectedProduct.observe(viewLifecycleOwner, Observer {
                if(it != null){
                    if(!model.isLivenessEnabled) {
                        model.requestKycForm(emptyList())
                    }else{
                        Navigation.findNavController(view)
                            .navigate(R.id.action_OcrResultFragment_to_selfieInstructionsFragment)
                    }
                }
            })
            model.getKycForm().observe(viewLifecycleOwner){
                binding.progressIndicator.visibility = View.INVISIBLE
                binding.btnSubmit.visibility = View.VISIBLE

                Navigation.findNavController(view)
                    .navigate(R.id.action_OcrResultFragment_to_moreEditableKyc)

            }
            btnSubmit.setOnClickListener {

                //The services of SedraCheck are flexable and usually don't rely on each other that
                // much, this is why the landing page has a few Switches to allow you to select
                // which features you want to include in the current journey.
                //This will control the navigation path.

                binding.btnSubmit.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
                model.getProductByIdType()

            }
            tvResults.text =
                "result: ${model.getScanDoc().value?.validationResult?.result} "
        }
    }
}