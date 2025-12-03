package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingsdk.network.models.IntegrationInfosItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter
import com.gatetopay.onboardingapplication.adapters.InfoFieldRecyclerAdapter

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
class IntegrationInfoViewHolder(view: View):ViewHolder(view) {
    val title:TextView
    val label:TextView
    val rvDynamic:RecyclerView

    init {
        title = view.findViewById(R.id.title)
        label = view.findViewById(R.id.label)
        rvDynamic = view.findViewById(R.id.rv_dynamic)
        rvDynamic.setLayoutManager(LinearLayoutManager(view.context))
    }

    fun bind(stepsItem: IntegrationInfosItem?){
        title.text =stepsItem?.integrationName
        label.visibility=View.GONE

        rvDynamic.setAdapter(InfoFieldRecyclerAdapter(items = stepsItem?.integrationFields?: emptyList<IntegrationField?>()))
    }

}