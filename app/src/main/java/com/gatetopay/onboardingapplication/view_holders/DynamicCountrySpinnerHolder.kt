package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.Country
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.CountriesArrayAdapter
import com.gatetopay.onboardingapplication.adapters.EnumratedArrayAdapter
import com.gatetopay.onboardingapplication.adapters.IntegrationEnumratedArrayAdapter

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 3/5/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: AbyanUiApp
 * <p>
 * SEDRA
 */

class DynamicCountrySpinnerHolder(
    view: View,
    val countriesAndCitiesResponse: CountriesAndCitiesResponse?,
    val nationalityListener: (value: String) -> Unit = { _ -> },
    val countryListener: (value: String) -> Unit = { _ -> }
) : BaseHolder(view) {
    val title: TextView
    val spinner: Spinner

    init {
        title = view.findViewById(R.id.tv_title)
        spinner = view.findViewById(R.id.sp_answers)
    }

    override fun <T> bind(t: T?) {
        when (t) {
            is DynamicFieldsItem -> {
                val dynamicFieldsItem = t
                val countryAdapter = CountriesArrayAdapter(
                    spinner.context,
                    countriesAndCitiesResponse?.countries ?: ArrayList<Country>()
                )
                title.text = dynamicFieldsItem?.fieldLabel ?: ""
                spinner.adapter = countryAdapter
                spinner.isEnabled = dynamicFieldsItem?.isReadOnly != true
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        when (dynamicFieldsItem.code) {
                            "NATIONALITY" -> {
                                nationalityListener(countryAdapter.getItemValue(p2))
                            }

                            "COUNTRY,CITY" ->
                                countryListener(countryAdapter.getItemValue(p2))

                        }
                        dynamicFieldsItem?.value = countryAdapter.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

            }

            is IntegrationField -> {
                val dynamicFieldsItem = t
                val countryAdapter = CountriesArrayAdapter(
                    spinner.context,
                    countriesAndCitiesResponse?.countries ?: ArrayList<Country>()
                )
                title.text = dynamicFieldsItem?.fieldName ?: ""
                spinner.adapter = countryAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        dynamicFieldsItem?.value = countryAdapter.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            }
        }
    }


}