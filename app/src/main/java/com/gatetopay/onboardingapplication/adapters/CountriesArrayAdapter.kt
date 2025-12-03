package com.gatetopay.onboardingapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gatetopay.onboardingsdk.network.models.Country
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingsdk.network.models.GetProductsResponseItem


class CountriesArrayAdapter(context: Context, var items: List<Country?>) :
    ArrayAdapter<Country?>(context, R.layout.spinner_item, items) {

    val inflater: LayoutInflater = LayoutInflater.from(context)

     fun getItemID(position: Int): String {
        return items[position]?.id.toString()?: "-1"
    }

    fun getItemValue(position: Int): String {
        return items[position]?.name?: ""
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as TextView).text = getItem(position)!!.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as TextView).text = getItem(position)!!.name
        return view
    }
}