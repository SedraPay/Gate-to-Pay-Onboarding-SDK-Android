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
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
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
class DynamicSpinnerHolder(view: View,val listener:(name:String,value:String)->Unit = {_,_->}):BaseHolder(view) {
    val title:TextView
    val spinner:Spinner
    init {
        title = view.findViewById(R.id.tv_title)
        spinner = view.findViewById(R.id.sp_answers)
    }


    override fun <T> bind(t: T?) {
        when(t){
            is DynamicFieldsItem -> {
                val dynamicFieldsItem = t
                val enumratedAdapter = EnumratedArrayAdapter(spinner.context,dynamicFieldsItem?.enumeratedValues?: ArrayList<EnumeratedValues>())
                title.text = dynamicFieldsItem?.fieldLabel?:""
                spinner.adapter= enumratedAdapter
                spinner.isEnabled = dynamicFieldsItem?.isReadOnly != true
                val index = dynamicFieldsItem?.enumeratedValues
                    ?.indexOfFirst { it?.key == dynamicFieldsItem.value } ?: 0
                spinner.setSelection(index)
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                        dynamicFieldsItem?.value = enumratedAdapter.getItemID(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

            }
            is IntegrationField ->{
                val dynamicFieldsItem = t
                val enumratedAdapter = IntegrationEnumratedArrayAdapter(spinner.context,dynamicFieldsItem?.enumeratedValues?: ArrayList<EnumeratedValues>())
                title.text = dynamicFieldsItem?.fieldName?:""
                spinner.adapter= enumratedAdapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        dynamicFieldsItem?.value = enumratedAdapter.getItemID(p2)
                        listener(dynamicFieldsItem?.fieldName?:"",enumratedAdapter.getItemID(p2))
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }
            }
        }
    }


}