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
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
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
class DynamicYesNoHolder(view: View,val viewTypes: DynamicFieldsRecyclerAdapter.ViewTypes):BaseHolder(view) {
    val radioGroup:RadioGroup
    val title: TextView

    init {
        title = view.findViewById(R.id.tv_title)
        radioGroup = view.findViewById(R.id.rg_answers)

    }

    override fun <T> bind(t: T?) {
       when(t){
           is DynamicFieldsItem->{
               val dynamicFieldsItem = t
               title.text = dynamicFieldsItem?.fieldLabel?:""
               radioGroup.isEnabled = dynamicFieldsItem?.isReadOnly != true
               radioGroup.orientation = if (viewTypes==DynamicFieldsRecyclerAdapter.ViewTypes.YES_NO) RadioGroup.HORIZONTAL else RadioGroup.VERTICAL
               for (valueItem in dynamicFieldsItem?.enumeratedValues?: ArrayList<EnumeratedValues>()) {
                   val rb = RadioButton(radioGroup.context)
                   if(radioGroup.orientation == RadioGroup.HORIZONTAL){
                       rb.layoutParams = RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT,1f)

                   }else{
                       rb.layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,0 ,1f)

                   }


                   rb.id = valueItem?.key?.toInt()?:0
                   rb.text = valueItem?.value
                   if(dynamicFieldsItem?.value == valueItem?.key){
                       rb.isChecked = true
                   }else{
                       rb.isChecked = false
                   }
                   radioGroup.addView(rb)
               }
               radioGroup.setOnCheckedChangeListener { _, checkedId ->
                   dynamicFieldsItem?.value = checkedId.toString()
               }

           }


       }
    }


}