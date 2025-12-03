package com.gatetopay.onboardingapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.view_holders.BaseHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicDateHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicNotHandeldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicSpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicTextFieldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicYesNoHolder

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
class InfoFieldRecyclerAdapter(val items:List<IntegrationField?>):RecyclerView.Adapter<BaseHolder>()  {

    var civilNumberField:IntegrationField?
    var itemsToShow:ArrayList<IntegrationField?>
    var index = 0


    init {
        civilNumberField = items.firstOrNull { it?.fieldName == "Civil Number" }
        itemsToShow = ArrayList<IntegrationField?>(items)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        when(viewType){
            DynamicFieldsRecyclerAdapter.ViewTypes.TEXT_FIELD.id->{
                val v = LayoutInflater.from(parent.context).inflate(R.layout.dynamic_textfield_row,parent,false)
                return DynamicTextFieldHolder(v)
            }
            DynamicFieldsRecyclerAdapter.ViewTypes.DROPDOWN.id->{
                val v = LayoutInflater.from(parent.context).inflate(R.layout.dynamic_spinner_row,parent,false)
                return DynamicSpinnerHolder(v,listener)
            }

            DynamicFieldsRecyclerAdapter.ViewTypes.DATE_TIME.id->{
                val v = LayoutInflater.from(parent.context).inflate(R.layout.dynamic_date_picker_row,parent,false)
                return DynamicDateHolder(v)
            }
        }

        val v= LayoutInflater.from(parent.context).inflate(R.layout.dynamic_not_handled_row,parent,false)
        return DynamicNotHandeldHolder(v)
    }

    override fun getItemCount(): Int {

        return itemsToShow.size
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        holder.bind(itemsToShow[position])

    }

    override fun getItemViewType(position: Int): Int {
        return itemsToShow[position]?.fieldTypes?:0
    }

    var isAdded = true
    val listener = fun (name:String, value:String){

        if(name=="ID Type"){
            when(value){
                "1"->{
                    if (!isAdded) {
                        addCivilNumberField()
                        isAdded = true
                    }
                }
                "2"->{
                    if(isAdded) {
                        hideCivilNumberField()
                        isAdded = false
                    }
                }
                "3"->{
                    if(isAdded) {
                        hideCivilNumberField()
                        isAdded = false
                    }
                }
                "4"->{
                    if(isAdded) {
                        hideCivilNumberField()
                        isAdded = false
                    }
                }
                "5"->{
                    if(isAdded) {
                        hideCivilNumberField()
                        isAdded = false
                    }
                }

                "6"->{
                    if (!isAdded) {
                        addCivilNumberField()
                        isAdded = true
                    }
                }
            }
        }

    }

    fun hideCivilNumberField(){
        civilNumberField?.let {
            index = itemsToShow.indexOf(it)
            itemsToShow.remove(it)
            notifyItemRemoved(index)
        }
    }

    fun addCivilNumberField(){
        itemsToShow.add(index,civilNumberField)
        notifyItemInserted(index)
    }


}