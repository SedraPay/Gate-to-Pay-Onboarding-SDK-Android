package com.gatetopay.onboardingapplication.view_holders

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingsdk.network.models.IntegrationField
import com.gatetopay.onboardingapplication.R
import java.text.SimpleDateFormat
import java.util.Calendar

/**
#     #     #   #
#           #  #
# #     #
 * Created by Suhaib Kamal  on 3/18/25
 * skamal@blessedtreeit.com
 * <p>
 * Project Name: AbyanUiApp
 * <p>
 * SEDRA
 */
class DynamicImageHolder(val view: View , val listener: ImageUploadListener) : BaseHolder(view) {

    val btnDate:MaterialButton
    val fragmentManager: FragmentManager?

    init{
        btnDate = view.findViewById(R.id.btn_date)
         fragmentManager = (view.context as? AppCompatActivity)?.supportFragmentManager
    }



    override fun <T> bind(t: T?) {
       when(t){
           is DynamicFieldsItem -> {

               val dynamicFieldsItem = t
               if(!dynamicFieldsItem?.value.isNullOrEmpty()) {
                   btnDate.text = dynamicFieldsItem?.value
               }else{
                   btnDate.text = dynamicFieldsItem.fieldLabel
               }
               btnDate.isEnabled = dynamicFieldsItem?.isReadOnly != true
               btnDate.setOnClickListener{

                  listener.onSelectFileClicked(getAdapterPosition())

               }

           }
           is IntegrationField -> {
               val integrationField = t
               if(!integrationField?.value.isNullOrEmpty()) {
                   btnDate.text = integrationField?.value
               }else{
                   btnDate.text = integrationField.fieldName
               }
               btnDate.setOnClickListener{

                   val datePicker = MaterialDatePicker.Builder.datePicker()
                       .setTitleText(t.fieldName)
                       .build()
                   datePicker.addOnPositiveButtonClickListener {
                           selcetion->
                       val cal = Calendar.getInstance()
                       cal.timeInMillis = selcetion
                       btnDate.text = SimpleDateFormat.getInstance().format(cal.timeInMillis)
                       integrationField?.value = SimpleDateFormat.getInstance().format(cal.timeInMillis)

                   }
                   datePicker.addOnNegativeButtonClickListener {

                   }
                   datePicker.addOnCancelListener {

                   }
                   datePicker.addOnDismissListener {

                   }

                   if(fragmentManager!= null) {
                       datePicker.show(fragmentManager, "DATE_PICKER")
                   }

               }
           }
       }
    }

}

interface ImageUploadListener{
    fun onSelectFileClicked(position: Int)
}



