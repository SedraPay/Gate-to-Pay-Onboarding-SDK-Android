package com.gatetopay.onboardingapplication.view_holders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.adapters.CheckboxRecyclerAdapter
import com.gatetopay.onboardingapplication.adapters.DynamicFieldsRecyclerAdapter
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
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
class DynamicCheckboxHolder(view: View) : BaseHolder(view) {
    val title: TextView
    val rvAnswer: RecyclerView

    init {
        title = view.findViewById(R.id.tv_title)
        rvAnswer = view.findViewById(R.id.rv_answers)
        rvAnswer.setLayoutManager(LinearLayoutManager(view.context))
    }


    override fun <T> bind(t: T?) {
        when (t) {
            is DynamicFieldsItem -> {
                title.text = t?.fieldLabel
                val answerList: ArrayList<String> = ArrayList<String>()
                rvAnswer.setAdapter(
                    CheckboxRecyclerAdapter(
                        checkedItems = t?.value?.split(",")?: emptyList(),
                        items = t?.enumeratedValues ?: emptyList<EnumeratedValues?>(),
                        onItemCheckedListener = object : CheckboxViewHolder.OnItemCheckedListener {
                            override fun onItemChecked(
                                item: EnumeratedValues?,
                                checked: Boolean,
                                adapterPosition: Int
                            ) {
                                if (checked) {
                                    answerList.add(item?.key ?: "")
                                } else {
                                    answerList.remove(
                                        item?.key ?: ""
                                    )
                                }
                                if (answerList.isEmpty()) {
                                    t?.value = ""
                                    Log.d("TAG", "onItemChecked: ${t.value}")

                                } else {
                                    t?.value = answerList.toString()
                                    Log.d("TAG", "onItemChecked: ${t.value}")
                                }

                            }


                        })
                )
            }
        }
    }

}