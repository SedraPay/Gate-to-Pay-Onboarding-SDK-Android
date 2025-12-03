package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R

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
class DynamicNotHandeldHolder(view: View):BaseHolder(view) {

    val title: TextView
    val type:TextView
    init {
        title = view.findViewById(R.id.tv_title)
        type = view.findViewById(R.id.tv_type)

    }

    override fun <T> bind(t: T?) {
        when(t){
            is DynamicFieldsItem->{
                val dynamicFieldsItem = t
                title.text = dynamicFieldsItem?.fieldLabel?:""
                type.text = dynamicFieldsItem?.dataType.toString()?:""
            }
            is IntegrationField ->{
                val integrationField = t
                title.text = integrationField?.fieldName?:""
                type.text = integrationField?.dateTypeEnum.toString()?:""
            }
        }
    }


}