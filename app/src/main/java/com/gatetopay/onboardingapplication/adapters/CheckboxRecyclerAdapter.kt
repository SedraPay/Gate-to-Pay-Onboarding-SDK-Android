package com.gatetopay.onboardingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingsdk.network.models.StepsItem
import com.gatetopay.onboardingsdk.network.models.UpdateKYCRequestDynamicFieldsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.exeptions.KycException
import com.gatetopay.onboardingapplication.view_holders.BaseHolder
import com.gatetopay.onboardingapplication.view_holders.CheckboxViewHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicNotHandeldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicSpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicTextFieldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicYesNoHolder
import com.gatetopay.onboardingapplication.view_holders.StepViewHolder

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
class CheckboxRecyclerAdapter(val checkedItems:List<String>,val items:List<EnumeratedValues?> ,val onItemCheckedListener: CheckboxViewHolder.OnItemCheckedListener):RecyclerView.Adapter<CheckboxViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckboxViewHolder {


        val v= LayoutInflater.from(parent.context).inflate(R.layout.row_checkbox,parent,false)
        return CheckboxViewHolder(v,onItemCheckedListener)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: CheckboxViewHolder, position: Int) {
        holder.setChecked(checkedItems.contains(items[position]?.key))
        holder.bind(items[position])

    }





}