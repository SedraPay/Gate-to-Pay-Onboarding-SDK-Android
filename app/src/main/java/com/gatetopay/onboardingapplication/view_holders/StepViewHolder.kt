package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.StepsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 3/12/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: AbyanUiApp
 * <p>
 * SEDRA
 */
class StepViewHolder(
    view: View,
    val countriesAndCitiesResponse: CountriesAndCitiesResponse?,
    val fileUploadListener: StepFileUploadListener,
    val nationalityListener: (value: String) -> Unit = { _ -> },
    val countryListener: (value: String) -> Unit = { _ -> },
) : ViewHolder(view) {
    val title: TextView
    val label: TextView
    val rvDynamic: RecyclerView

    init {
        title = view.findViewById(R.id.title)
        label = view.findViewById(R.id.label)
        rvDynamic = view.findViewById(R.id.rv_dynamic)
        rvDynamic.setLayoutManager(LinearLayoutManager(view.context))
    }

    fun bind(stepsItem: StepsItem?) {
        title.text = stepsItem?.tag
        //label.text=stepsItem?.fieldLabel

        rvDynamic.setAdapter(
            DynamicFieldsRecyclerAdapter(
                items = stepsItem?.dynamicFields ?: emptyList<DynamicFieldsItem?>(),
                countriesAndCitiesResponse,
                object : FileUploadListener {
                    override fun onSelectFileClicked(position: Int, code: String) {
                        fileUploadListener.onFileUploaded(getAdapterPosition(), position, code)
                    }
                },
                object : ImageUploadListener {
                    override fun onSelectFileClicked(position: Int) {
                        fileUploadListener.onImageUploaded(getAdapterPosition(), position)
                    }
                },nationalityListener,countryListener)
        )
    }

    interface StepFileUploadListener {
        fun onFileUploaded(stepPosition: Int, position: Int, fieldCode: String)
        fun onImageUploaded(stepPosition: Int, position: Int)
    }

}