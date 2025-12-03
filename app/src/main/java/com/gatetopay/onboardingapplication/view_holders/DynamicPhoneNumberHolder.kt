package com.gatetopay.onboardingapplication.view_holders

import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter
import com.gatetopay.onboardingapplication.models.CountryModel

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
public class DynamicPhoneNumberHolder(view: View, val viewType: Int ?= null):BaseHolder(view){
    val dynamicTil:TextInputLayout
    val title:TextView
    val countryCodeSpinner: Spinner

    var selectedCountry:CountryModel? = null
    var countries = ArrayList<CountryModel>()

    init {
        dynamicTil = view.findViewById(R.id.dynamicTil)
        title = view.findViewById(R.id.tv_title)
        countryCodeSpinner = view.findViewById(R.id.country_code_spinner)
        val inputStream = view.context.resources.openRawResource(R.raw.country_code)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<CountryModel>>() {}.type
        countries = Gson().fromJson(jsonString, listType)
        val adapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            countries.map { it.emoji+" "+it.dialCode } // display country names
        )
        if(!countries.isNullOrEmpty()){
            selectedCountry = countries[0]
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countryCodeSpinner.adapter = adapter
    }

    override fun <T> bind(t: T?) {
        when(t){
            is DynamicFieldsItem->{

                val dynamicFieldsItem = t
                title.text = dynamicFieldsItem?.fieldLabel

                countryCodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCountry = countries[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }
                dynamicTil.editText?.setText(dynamicFieldsItem?.value)
                if(viewType!=null){
                    when(viewType){
                        DynamicFieldsRecyclerAdapter.ViewTypes.TEXT_FIELD.id->{
                            dynamicTil.editText?.inputType = InputType.TYPE_CLASS_TEXT

                        }
                        DynamicFieldsRecyclerAdapter.ViewTypes.ADDRESS.id->{
                            dynamicTil.editText?.inputType = InputType.TYPE_CLASS_TEXT
                        }
                        DynamicFieldsRecyclerAdapter.ViewTypes.MOBILE.id->{
                            dynamicTil.editText?.inputType = InputType.TYPE_CLASS_PHONE
                        }
                        DynamicFieldsRecyclerAdapter.ViewTypes.EMAIL.id-> {
                            dynamicTil.editText?.inputType =InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    }
                }
                dynamicTil.isEnabled = dynamicFieldsItem?.isReadOnly != true
                dynamicTil.editText?.doOnTextChanged { text, start, before, count ->
                    if(text.isNullOrEmpty()){
                        dynamicFieldsItem?.errorMessage= itemView.context.getString(R.string.kyc_error_message)
                        dynamicTil.error = itemView.context.getString(R.string.this_field_is_required)
                    }else {
                        if (!dynamicFieldsItem?.validationRegEx.isNullOrEmpty()) {
                            val regex = Regex(dynamicFieldsItem.validationRegEx ?: "")
                            var value = selectedCountry?.dialCode+text.toString()
                            if (value.matches(regex)) {
                                dynamicFieldsItem?.value = value
                                dynamicTil.error = null
                                dynamicFieldsItem?.errorMessage = null
                            } else {
                                dynamicTil.error = dynamicFieldsItem?.regExErrorMessage ?: dynamicTil.context.getString(
                                    R.string.invalid_input_format
                                )
                                dynamicFieldsItem?.errorMessage =
                                    itemView.context.getString(R.string.kyc_error_message)
                            }
                        } else {
                            var value = selectedCountry?.dialCode+text.toString()
                            dynamicFieldsItem?.value = value
                            dynamicTil.error = null
                            dynamicFieldsItem?.errorMessage = null
                        }
                    }
                }
            }

            is IntegrationField->{
                val integrationField = t
                countryCodeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCountry = countries[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }

                title.text = integrationField?.fieldName
                dynamicTil.editText?.doOnTextChanged { text, start, before, count ->
                    var value = selectedCountry?.dialCode+text.toString()
                    integrationField?.value = value
                }
            }


        }
    }






}