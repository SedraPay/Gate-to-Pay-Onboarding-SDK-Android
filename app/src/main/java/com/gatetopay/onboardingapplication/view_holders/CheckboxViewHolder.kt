package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues

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
class CheckboxViewHolder(view: View,val onItemCheckedListener: OnItemCheckedListener):ViewHolder(view) {
    val checkbox: CheckBox


    init {
        checkbox = view.findViewById(R.id.checkbox)

    }

    fun bind(enumeratedValues: EnumeratedValues?){


        checkbox.text=enumeratedValues?.value
        checkbox.setOnCheckedChangeListener({
            _, isChecked ->
            onItemCheckedListener.onItemChecked(enumeratedValues,isChecked,adapterPosition)
        })
    }

    //setChecked
    fun setChecked(checked: Boolean){
        checkbox.isChecked=checked
    }

    fun interface OnItemCheckedListener {
        fun onItemChecked(item: EnumeratedValues?,checked: Boolean, adapterPosition: Int)
    }
}