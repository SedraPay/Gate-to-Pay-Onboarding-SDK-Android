package com.gatetopay.onboardingapplication.view_holders

import android.text.InputType
import android.view.View
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter

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
public class DynamicTextFieldHolder(view: View,val viewType: Int ?= null):BaseHolder(view){
    val dynamicTil:TextInputLayout
    val title:TextView

    init {
        dynamicTil = view.findViewById(R.id.dynamicTil)
        title = view.findViewById(R.id.tv_title)
    }

    override fun <T> bind(t: T?) {
        when(t){
            is DynamicFieldsItem->{
                val dynamicFieldsItem = t
                title.text = dynamicFieldsItem?.fieldLabel
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

                            if (text.toString().matches(regex)) {
                                dynamicFieldsItem?.value = text.toString()
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
                            dynamicFieldsItem?.value = text.toString()
                            dynamicTil.error = null
                            dynamicFieldsItem?.errorMessage = null
                        }
                    }
                }
            }

            is IntegrationField->{
                val integrationField = t
                title.text = integrationField?.fieldName
                dynamicTil.editText?.doOnTextChanged { text, start, before, count ->
                    integrationField?.value = text.toString()
                }
            }


        }
    }


}