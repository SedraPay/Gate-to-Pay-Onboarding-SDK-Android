package com.gatetopay.onboardingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationInfosItem
import com.gatetopay.onboardingsdk.network.models.StepsItem
import com.gatetopay.onboardingsdk.network.models.UpdateIntegrationFieldsItem
import com.gatetopay.onboardingsdk.network.models.UpdateKYCRequestDynamicFieldsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.exeptions.KycException
import com.gatetopay.onboardingapplication.view_holders.BaseHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicNotHandeldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicSpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicTextFieldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicYesNoHolder
import com.gatetopay.onboardingapplication.view_holders.IntegrationInfoViewHolder

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
class IntegrationInfoRecyclerAdapter(val items:List<IntegrationInfosItem?>):RecyclerView.Adapter<IntegrationInfoViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntegrationInfoViewHolder {


        val v= LayoutInflater.from(parent.context).inflate(R.layout.row_step,parent,false)
        return IntegrationInfoViewHolder(v)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: IntegrationInfoViewHolder, position: Int) {
        holder.bind(items[position])

    }

    @Throws(KycException::class)
    fun getUpdatedItems():List<UpdateIntegrationFieldsItem>{
        val updatedItems:ArrayList<UpdateIntegrationFieldsItem> = ArrayList()
        for (step in items){
            for(intgrationInfo in step?.integrationFields?: emptyList()){
                if(intgrationInfo?.value.isNullOrEmpty()&&intgrationInfo?.isRequired==true){
                    throw KycException("Please fill all required fields-"+intgrationInfo?.fieldName)
                }
                intgrationInfo.validationRegEx?.let{
                    if(intgrationInfo.value?.matches(Regex(intgrationInfo.validationRegEx?:""))!=true){
                        throw KycException("Invalid input format for field-"+intgrationInfo?.fieldName)
                    }
                }

                val updatedItem = UpdateIntegrationFieldsItem(value = intgrationInfo?.value, key = intgrationInfo?.fieldName)
                updatedItems.add(updatedItem)
            }
        }
        return updatedItems
    }





}