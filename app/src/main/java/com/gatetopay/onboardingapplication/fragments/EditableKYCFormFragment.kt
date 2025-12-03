package com.gatetopay.onboardingapplication.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gatetopay.onboardingsdk.ScanResult
import com.gatetopay.onboardingsdk.ScannerResultContract
import com.gatetopay.onboardingsdk.network.models.DataExtractionModel
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IdType
import com.gatetopay.onboardingsdk.network.models.StepsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter
import com.gatetopay.onboardingapplication.adapters.StepsRecyclerAdapter
import com.gatetopay.onboardingapplication.databinding.FragmentKycFormBinding
import com.gatetopay.onboardingapplication.exeptions.KycException
import com.gatetopay.onboardingapplication.models.CheckConfigration
import com.gatetopay.onboardingapplication.view_holders.FileUploadListener
import com.gatetopay.onboardingapplication.view_holders.StepViewHolder
import com.gatetopay.onboardingapplication.view_models.CheckViewModel


class EditableKYCFormFragment : Fragment(), StepViewHolder.StepFileUploadListener {

    private val binding: FragmentKycFormBinding by lazy {
        FragmentKycFormBinding.inflate(
            layoutInflater
        )
    }

    lateinit var getScannerResult: ActivityResultLauncher<IdType>
    lateinit var model: CheckViewModel
    var idType: IdType = IdType(numberOfImages = 2, currentImage = 1)
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            model.uploadImageFile(it)
        }
    }

    private val fileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            model.uploadFile(it, code)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: CheckViewModel by activityViewModels()
        var steps: StepsRecyclerAdapter? = null

        val configration: CheckConfigration? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().intent?.extras?.getSerializable(
                    "config",
                    CheckConfigration::class.java
                )
            } else {
                requireActivity().intent?.extras?.getSerializable("config") as CheckConfigration?
            }


        val scans = ArrayList<ScanResult>()

        model.getScanDoc().observe(viewLifecycleOwner) { scanModel: DataExtractionModel? ->
            if (scanModel != null) {

                /*   Navigation.findNavController(view)
                       .navigate(R.id.action_kycFormFragment_to_selfieInstructionsFragment)*/

                binding.btnNext.visibility = View.VISIBLE
                binding.progressIndicator.visibility = View.GONE


            }
        }

        model.uploadListener.observe(viewLifecycleOwner) {
            steps?.updateItem(stepPosition, position, it)
        }

        model.fileUploadListener.observe(viewLifecycleOwner) {
            steps?.updateItem(stepPosition, position, it)
        }

        getScannerResult = registerForActivityResult(ScannerResultContract()) { imagePath ->
            if (imagePath != null) {
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE

                scans.add(ScanResult(imagePath, idType!!))

                //do a check and call it again if you need to get the back side
                if (scans.size < (idType?.numberOfImages ?: 1)) {
                    val current = idType?.currentImage?.let { it } ?: 0
                    idType?.currentImage = current + 1
                    //start scanner again
                    //you can also put an intermediate screen or show a popup up dialog first, to
                    //inform your user that they will need to scan the back of their ID
                    getScannerResult.launch(idType!!)
                } else {
                    //else send the list of paths to the view model
                    model.extractDataFromDocuments(scans)
                }
            } else {
                // handle no result returned
            }
        }


        model.requestRiskKyc(
        )
        model.getRiskKycForm().observe(viewLifecycleOwner, Observer {
            with(binding) {
                steps = StepsRecyclerAdapter(
                    items = it?.steps ?: emptyList(),
                    model.getCountries().value,
                    this@EditableKYCFormFragment,
                    {
                        nationality->
                        model.setSelectedNationalityName(nationality)
                    },
                    {
                        residance->
                        model.setSelectedResidenceName(residance)
                    }
                )
                rvSteps.setLayoutManager(LinearLayoutManager(context))
                rvSteps.setAdapter(steps)


            }


        })
        model.getUpdateRiskKYCResult().observe(viewLifecycleOwner, Observer {
            if (it != null) {

                // getScannerResult.launch(idType)
                Navigation.findNavController(view)
                    .navigate(R.id.action_editableKYCFormFragment_to_selectIdTypeFragment)

            }
        })





        with(binding) {

            btnNext.setOnClickListener {

                try {
                    model.updateRiskKyc(steps?.getUpdatedItems() ?: emptyList())
                } catch (e: KycException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
            }
        }
    }

    var stepPosition = -1
    var position = -1
    var code = ""


    override fun onFileUploaded(stepPosition: Int, position: Int, fieldCode: String) {
        this.stepPosition = stepPosition
        this.position = position
        this.code = fieldCode
        fileLauncher.launch("image/*")
    }

    override fun onImageUploaded(stepPosition: Int, position: Int) {
        this.stepPosition = stepPosition
        this.position = position
        galleryLauncher.launch("image/*")
    }

}
