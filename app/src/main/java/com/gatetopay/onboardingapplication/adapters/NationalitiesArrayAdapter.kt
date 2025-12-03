package com.gatetopay.onboardingapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gatetopay.onboardingapplication.R

import com.gatetopay.onboardingsdk.network.models.Nationality



class NationalitiesArrayAdapter(context: Context, var items: List<Nationality>) :
    ArrayAdapter<Nationality>(context, R.layout.spinner_item, items) {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemId(position: Int): Long {
        return items[position].nationalityId?.toLong() ?: -1
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