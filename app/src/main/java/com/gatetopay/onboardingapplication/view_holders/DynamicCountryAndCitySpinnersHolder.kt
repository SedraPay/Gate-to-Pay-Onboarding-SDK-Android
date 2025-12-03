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
import com.gatetopay.onboardingsdk.network.models.City
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.Country
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.CitiesArrayAdapter
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
class DynamicCountryAndCitySpinnersHolder(
    view: View,
    val countriesAndCitiesResponse: CountriesAndCitiesResponse?,
    val countryListener:(value:String)->Unit = {_->}
) : BaseHolder(view) {
    val title: TextView
    val title2: TextView
    val spinner: Spinner
    val spinner2: Spinner
    var selectedCountry: Country? = null
    var selectedCity: City? = null
    var citiesAdapter: CitiesArrayAdapter? = null

    init {
        title = view.findViewById(R.id.tv_title)
        title2 = view.findViewById(R.id.tv_title_2)
        spinner = view.findViewById(R.id.sp_answers)
        spinner2 = view.findViewById(R.id.sp_answers_2)
    }

    override fun <T> bind(t: T?) {
        when (t) {
            is DynamicFieldsItem -> {
                val dynamicFieldsItem = t
                val countryAdapter = CountriesArrayAdapter(
                    spinner.context,
                    countriesAndCitiesResponse?.countries ?: ArrayList<Country>()
                )
                val titles = dynamicFieldsItem?.fieldLabel?.split(",")?.map { it.trim() }
            title.text = titles?.get(0) ?:""
                title2.text = titles?.get(1) ?:""
                spinner.adapter = countryAdapter
                spinner.isEnabled = dynamicFieldsItem?.isReadOnly != true
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCountry = countriesAndCitiesResponse?.countries?.get(p2)
                        countryListener(countryAdapter.getItemValue(p2))
                        citiesAdapter = CitiesArrayAdapter(
                            spinner2.context,
                            selectedCountry?.cities ?: ArrayList<City>()
                        )
                        spinner2.adapter = citiesAdapter
                        dynamicFieldsItem?.value = countryAdapter.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

                spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCity = selectedCountry?.cities?.get(p2)
                        dynamicFieldsItem?.value =
                            dynamicFieldsItem?.value + "," + citiesAdapter?.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }

            }

            is IntegrationField -> {
                val dynamicFieldsItem = t
                val enumratedAdapter = IntegrationEnumratedArrayAdapter(
                    spinner.context,
                    dynamicFieldsItem?.enumeratedValues ?: ArrayList<EnumeratedValues>()
                )
                title.text = dynamicFieldsItem?.fieldName ?: ""
                spinner.adapter = enumratedAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        dynamicFieldsItem?.value = enumratedAdapter.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            }
        }
    }


}