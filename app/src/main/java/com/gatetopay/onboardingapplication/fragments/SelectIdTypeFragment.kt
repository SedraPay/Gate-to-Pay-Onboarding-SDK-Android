package  com.gatetopay.onboardingapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.gatetopay.onboardingsdk.ScanResult
import com.gatetopay.onboardingsdk.ScannerResultContract
import com.gatetopay.onboardingsdk.network.models.DataExtractionModel
import com.gatetopay.onboardingsdk.network.models.IdType
import  com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.EnumratedArrayAdapter
import com.gatetopay.onboardingapplication.adapters.IdTypesArrayAdapter
import  com.gatetopay.onboardingapplication.adapters.ProductsArrayAdapter
import  com.gatetopay.onboardingapplication.databinding.FragmentSelectIdTypeBinding
import  com.gatetopay.onboardingapplication.databinding.FragmentSelectNationalityBinding
import  com.gatetopay.onboardingapplication.view_models.CheckViewModel
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues


class SelectIdTypeFragment : Fragment() {
    private val binding: FragmentSelectIdTypeBinding by lazy {
        FragmentSelectIdTypeBinding.inflate(
            layoutInflater
        )
    }
    lateinit var  getScannerResult: ActivityResultLauncher<IdType>
    var idTypesArrayAdapter : IdTypesArrayAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: CheckViewModel by activityViewModels()

        val scans = ArrayList<ScanResult>()

        model.getIdTypesByNationality()

        model.idTypes.observe(viewLifecycleOwner) { idTypes ->
            if (idTypes.isNotEmpty()) {
                idTypesArrayAdapter = IdTypesArrayAdapter(requireContext(),idTypes)
                binding.spinner.adapter = idTypesArrayAdapter
            }
        }

        getScannerResult = registerForActivityResult(ScannerResultContract()) { imagePath -> if (imagePath != null) {
            binding.btnNext.visibility = View.GONE
            binding.title.visibility = View.GONE
            binding.graphic.visibility = View.GONE
            binding.hint.visibility = View.GONE
            binding.cardNationality.visibility=View.GONE
            binding.progressIndicator.visibility = View.VISIBLE

            scans.add(ScanResult(imagePath, model.idType!!))

            //do a check and call it again if you need to get the back side
            if(scans.size < (model.idType?.numberOfImages ?: 1)) {
                val current = model.idType?.currentImage?.let { it }?:0
                model.idType?.currentImage=  current+1
                //start scanner again
                //you can also put an intermediate screen or show a popup up dialog first, to
                //inform your user that they will need to scan the back of their ID
                getScannerResult.launch(model.idType!!)
            }else {
                //else send the list of paths to the view model
                model.extractDataFromDocuments(scans)
            }
        } else {
            // handle no result returned
            binding.btnNext.visibility = View.VISIBLE
            binding.title.visibility = View.VISIBLE
            binding.graphic.visibility = View.VISIBLE
            binding.hint.visibility = View.VISIBLE
            binding.cardNationality.visibility=View.VISIBLE
            binding.progressIndicator.visibility = View.GONE
        }
        }
        model.getKycForm().observe(viewLifecycleOwner){
            binding.btnNext.visibility = View.VISIBLE
            binding.title.visibility = View.VISIBLE
            binding.graphic.visibility = View.VISIBLE
            binding.hint.visibility = View.VISIBLE
            binding.cardNationality.visibility=View.VISIBLE
            binding.progressIndicator.visibility = View.GONE

            Navigation.findNavController(view)
                .navigate(R.id.action_SelectIdTypeFragment_to_moreKycFragment)

        }

        model.selectedProduct.observe(viewLifecycleOwner){
            if(it != null){

                    model.requestKycForm(emptyList())

            }
        }

        model.getScanDoc().observe(viewLifecycleOwner) { scanModel: DataExtractionModel? ->
            if (scanModel != null) {

                Navigation.findNavController(view)
                    .navigate(R.id.action_SelectIdTypeFragment_to_ocrResultFragment)

                binding.btnNext.visibility = View.VISIBLE
                binding.title.visibility = View.VISIBLE
                binding.graphic.visibility = View.VISIBLE
                binding.hint.visibility = View.VISIBLE
                binding.cardNationality.visibility=View.VISIBLE
                binding.progressIndicator.visibility = View.GONE


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


                    val typeId=idTypesArrayAdapter?.getItemId(position)
                    when (idTypesArrayAdapter?.getItemId(position)) {
                        1L -> {
                            model.setIdType(0,typeId?.toInt()?:0)
                        }

                        2L -> {
                            model.setIdType(1,typeId?.toInt()?:0)
                        }

                        3L -> {
                            model.setIdType(1,typeId?.toInt()?:0)
                        }

                        4L -> {
                            model.setIdType(1,typeId?.toInt()?:0)
                        }

                        5L -> {
                            model.setIdType(1,typeId?.toInt()?:0)
                        }

                        6L -> {
                            model.setIdType(0,typeId?.toInt()?:0)
                        }


                    }
                }
            }

            btnNext.setOnClickListener {
                if(model.isDocumentVerificationEnabled) {
                    getScannerResult.launch(
                        model.idType ?: IdType(
                            numberOfImages = 2,
                            currentImage = 1
                        )
                    )
                    binding.btnNext.visibility = View.GONE
                    binding.title.visibility = View.GONE
                    binding.graphic.visibility = View.GONE
                    binding.hint.visibility = View.GONE
                    binding.cardNationality.visibility = View.GONE
                    binding.progressIndicator.visibility = View.VISIBLE
                }else{
                    if(model.isLivenessEnabled){

                        Navigation.findNavController(view)
                            .navigate(R.id.action_SelectIdTypeFragment_to_selfieInstructionsFragment)
                    }else{
                        binding.btnNext.visibility = View.GONE
                        binding.title.visibility = View.GONE
                        binding.graphic.visibility = View.GONE
                        binding.hint.visibility = View.GONE
                        binding.cardNationality.visibility = View.GONE
                        binding.progressIndicator.visibility = View.VISIBLE
                        model.getProductByIdType()
                    }
                }
            }
        }
    }
}