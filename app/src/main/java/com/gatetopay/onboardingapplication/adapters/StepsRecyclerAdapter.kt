package com.gatetopay.onboardingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.StepsItem
import com.gatetopay.onboardingsdk.network.models.UpdateKYCRequestDynamicFieldsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.exeptions.KycException
import com.gatetopay.onboardingapplication.view_holders.BaseHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicNotHandeldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicSpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicTextFieldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicYesNoHolder
import com.gatetopay.onboardingapplication.view_holders.FileUploadListener
import com.gatetopay.onboardingapplication.view_holders.StepViewHolder
import org.json.JSONArray

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
class StepsRecyclerAdapter(
    val items: List<StepsItem?>,
    val countriesAndCitiesResponse: CountriesAndCitiesResponse?,
    val fileUploadListener: StepViewHolder.StepFileUploadListener,
    val nationalityListener: (value: String) -> Unit = { _ -> },
    val countryListener: (value: String) -> Unit = { _ -> }
) : RecyclerView.Adapter<StepViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {


        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_step, parent, false)
        return StepViewHolder(v, countriesAndCitiesResponse, fileUploadListener,nationalityListener,countryListener)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(items[position])

    }

    fun updateItem(stepPosition: Int, position: Int, value: String) {
        items[stepPosition]?.dynamicFields?.get(position)?.value = value
        notifyItemChanged(stepPosition)
    }

    @Throws(KycException::class)
    fun getUpdatedItems(): List<UpdateKYCRequestDynamicFieldsItem> {
        val updatedItems: ArrayList<UpdateKYCRequestDynamicFieldsItem> = ArrayList()
        for (step in items) {
            for (dynamicField in step?.dynamicFields ?: emptyList()) {
                if (dynamicField?.errorMessage != null) {
                    throw KycException(dynamicField?.errorMessage ?: "")
                }
                if (dynamicField?.value.isNullOrEmpty() && dynamicField?.isRequired == true) {
                    throw KycException("Please fill all required fields-" + dynamicField?.fieldLabel)
                }
                if (isJsonArray(dynamicField?.value ?: "")) {
                    val jsonArray = JSONArray(dynamicField?.value)
                    for (i in 0 until jsonArray.length()) {
                        val updatedItem = UpdateKYCRequestDynamicFieldsItem(
                            value = jsonArray.getString(i),
                            fieldId = dynamicField?.id
                        )
                        updatedItems.add(updatedItem)
                    }

                } else {
                    val updatedItem = UpdateKYCRequestDynamicFieldsItem(
                        value = dynamicField?.value,
                        fieldId = dynamicField?.id
                    )
                    updatedItems.add(updatedItem)
                }
            }
        }
        return updatedItems
    }


    fun isJsonArray(str: String): Boolean {
        return try {
            JSONArray(str) // will throw if not valid JSON array
            true
        } catch (e: Exception) {
            false
        }
    }


}