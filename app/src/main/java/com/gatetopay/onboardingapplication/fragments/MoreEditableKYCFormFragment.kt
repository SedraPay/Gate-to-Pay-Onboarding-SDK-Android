package com.gatetopay.onboardingapplication.fragments

import android.content.Context
import android.net.Uri
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
import com.google.android.material.snackbar.Snackbar
import com.gatetopay.onboardingsdk.network.models.IdType
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.StepsRecyclerAdapter
import com.gatetopay.onboardingapplication.databinding.FragmentKycFormBinding
import com.gatetopay.onboardingapplication.exeptions.KycException
import com.gatetopay.onboardingapplication.view_holders.StepViewHolder
import com.gatetopay.onboardingapplication.view_models.CheckViewModel
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri


class MoreEditableKYCFormFragment : Fragment(), StepViewHolder.StepFileUploadListener {
    val model: CheckViewModel by activityViewModels()


    private val binding: FragmentKycFormBinding by lazy {
        FragmentKycFormBinding.inflate(
            layoutInflater
        )
    }

    var steps: StepsRecyclerAdapter? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = getFileFromUri(requireContext(), uri)
            val absPath = file?.absolutePath
            model.uploadImageFile(absPath?.toUri() ?: Uri.EMPTY)
        }
    }

    var fileName = ""
    private val fileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = getFileFromUri(requireContext(), uri)
            val absPath = file?.absolutePath
            fileName = file?.name?:""
            model.uploadFile(absPath?.toUri() ?: Uri.EMPTY, code)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        model.getUpdateKYCResult().observe(viewLifecycleOwner, Observer {


            binding.btnNext.visibility = View.VISIBLE
            binding.progressIndicator.visibility = View.GONE
            Navigation.findNavController(view)
                .navigate(R.id.action_MoreEditableKyc_to_kycSubissionSuccessfulFragment)


        })

        model.uploadListener.observe(viewLifecycleOwner) {
            steps?.updateItem(stepPosition, position, it)
        }

        model.fileUploadListener.observe(viewLifecycleOwner) {
            steps?.updateItem(stepPosition, position, "$it,$fileName")
        }



        with(binding) {

            steps = StepsRecyclerAdapter(
                items = model.getKycForm().value?.steps ?: emptyList(),
                model.getCountries().value,
                this@MoreEditableKYCFormFragment
            )
            rvSteps.setLayoutManager(LinearLayoutManager(context))
            rvSteps.setAdapter(steps)


        }


        binding.btnNext.setOnClickListener {
            try {
                val updatedItems = steps?.getUpdatedItems()
                model.updateKyc(updatedItems ?: emptyList())
                binding.btnNext.visibility = View.GONE
                binding.progressIndicator.visibility = View.VISIBLE
            } catch (e: KycException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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


    fun getFileFromUri(context: Context, uri: Uri): File? {
        return when (uri.scheme) {
            "file" -> File(uri.path!!) // direct file:// URI
            "content" -> {
                // Copy content to temp file
                try {
                    val fileName = "picked_${System.currentTimeMillis()}.jpg"
                    val file = File(context.cacheDir, fileName)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    file
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            else -> null
        }
    }


}

