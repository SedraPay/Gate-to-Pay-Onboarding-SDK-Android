package com.gatetopay.onboardingapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.DynamicFieldsItem
import com.gatetopay.onboardingapplication.R
import com.gatetopay.onboardingapplication.view_holders.BaseHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicCheckboxHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicCountryAndCitySpinnersHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicCountrySpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicDateHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicDocumentHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicImageHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicNotHandeldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicPhoneNumberHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicSpinnerHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicTextFieldHolder
import com.gatetopay.onboardingapplication.view_holders.DynamicYesNoHolder
import com.gatetopay.onboardingapplication.view_holders.FileUploadListener
import com.gatetopay.onboardingapplication.view_holders.ImageUploadListener

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
class DynamicFieldsRecyclerAdapter(
    val items: List<DynamicFieldsItem?>,
    val countriesAndCitiesResponse: CountriesAndCitiesResponse?,
    val fileUploadListener: FileUploadListener,
    val imageUploadListener: ImageUploadListener,
    val nationalityListener: (value: String) -> Unit = { _ -> },
    val countryListener: (value: String) -> Unit = { _ -> },
) : RecyclerView.Adapter<BaseHolder>() {


    enum class ViewTypes(val id: Int) {
        TEXT_FIELD(1),
        DROPDOWN(2),
        CHECKBOX(3),
        RADIO_BUTTON(4),
        DATE_TIME(5),
        BOOLEAN(6),
        FILE(7),
        IMAGE(8),
        COUNTRY(9),
        CITY(10),
        TABLE(11),
        TEXT_AREA(12),
        EMAIL(13),
        MOBILE(14),
        NUMBER(15),
        ADDRESS(16),
        TEXT_EDITOR(17),
        YES_NO(18),
        COUNTRY_AND_CITY(19)

    }

    fun onFileUploaded(position: Int, value: String) {
        items[position]?.value = value
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        when (viewType) {
            ViewTypes.TEXT_FIELD.id, ViewTypes.ADDRESS.id, ViewTypes.EMAIL.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_textfield_row, parent, false)
                return DynamicTextFieldHolder(v, viewType)
            }

            ViewTypes.MOBILE.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_phone_number_row, parent, false)
                return DynamicPhoneNumberHolder(v, viewType)
            }

            ViewTypes.TEXT_AREA.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_textfield_row, parent, false)
                return DynamicTextFieldHolder(v, viewType)
            }

            ViewTypes.TEXT_EDITOR.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_textfield_row, parent, false)
                return DynamicTextFieldHolder(v, viewType)
            }

            ViewTypes.CHECKBOX.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_check_box_row, parent, false)
                return DynamicCheckboxHolder(v)
            }

            ViewTypes.NUMBER.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_textfield_row, parent, false)
                return DynamicTextFieldHolder(v, viewType)
            }

            ViewTypes.YES_NO.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_yes_no_row, parent, false)
                return DynamicYesNoHolder(v, ViewTypes.YES_NO)
            }

            ViewTypes.DATE_TIME.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_date_picker_row, parent, false)
                return DynamicDateHolder(v)
            }

            ViewTypes.FILE.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_image_picker_row, parent, false)
                return DynamicDocumentHolder(v, fileUploadListener)
            }

            ViewTypes.IMAGE.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_image_picker_row, parent, false)
                return DynamicImageHolder(v, imageUploadListener)
            }

            ViewTypes.RADIO_BUTTON.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_yes_no_row, parent, false)
                return DynamicYesNoHolder(v, ViewTypes.RADIO_BUTTON)
            }

            ViewTypes.DROPDOWN.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_spinner_row, parent, false)
                return DynamicSpinnerHolder(v)
            }

            ViewTypes.COUNTRY.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_spinner_row, parent, false)
                return DynamicCountrySpinnerHolder(v, countriesAndCitiesResponse,nationalityListener,countryListener)
            }

            ViewTypes.CITY.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_spinner_row, parent, false)
                return DynamicCountrySpinnerHolder(v, countriesAndCitiesResponse)
            }

            ViewTypes.COUNTRY_AND_CITY.id -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dynamic_two_spinner_row, parent, false)
                return DynamicCountryAndCitySpinnersHolder(v, countriesAndCitiesResponse,countryListener)
            }
        }

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.dynamic_not_handled_row, parent, false)
        return DynamicNotHandeldHolder(v)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        holder.bind(items[position])

    }

    override fun getItemViewType(position: Int): Int {
        return items[position]?.dataType ?: 0
    }


}