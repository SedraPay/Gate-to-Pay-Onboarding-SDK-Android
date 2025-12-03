package com.gatetopay.onboardingapplication.view_models

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gatetopay.onboardingsdk.CheckCallback
import com.gatetopay.onboardingsdk.GateToCheck
import com.gatetopay.onboardingsdk.GateToCheckEngine
import com.gatetopay.onboardingsdk.ScanResult
import com.gatetopay.onboardingsdk.exceptions.GateToCheckException
import com.gatetopay.onboardingsdk.network.models.CloseJourneyResponseModel
import com.gatetopay.onboardingsdk.network.models.CountriesAndCitiesResponse
import com.gatetopay.onboardingsdk.network.models.DataExtractionModel
import com.gatetopay.onboardingsdk.network.models.EnumeratedValues
import com.gatetopay.onboardingsdk.network.models.FaceMatchResponseModel
import com.gatetopay.onboardingsdk.network.models.GetFormInfoResponseModel
import com.gatetopay.onboardingsdk.network.models.GetKYCRequestModel
import com.gatetopay.onboardingsdk.network.models.GetKYCResponseModel
import com.gatetopay.onboardingsdk.network.models.GetProductsResponseItem
import com.gatetopay.onboardingsdk.network.models.IdType
import com.gatetopay.onboardingsdk.network.models.IntegrationInfosItem
import com.gatetopay.onboardingsdk.network.models.JourneyCreatedModel
import com.gatetopay.onboardingsdk.network.models.NationalitiesAndIdTypesModel
import com.gatetopay.onboardingsdk.network.models.Nationality
import com.gatetopay.onboardingsdk.network.models.ScreeningRequestModel
import com.gatetopay.onboardingsdk.network.models.ScreeningResponseModel
import com.gatetopay.onboardingsdk.network.models.UpdateIntegrationFieldsItem
import com.gatetopay.onboardingsdk.network.models.UpdateKYCRequestDynamicFieldsItem
import com.google.gson.Gson

import java.io.File
import kotlin.random.Random


class CheckViewModel : ViewModel() {
    private var selfieImagePath: String? = null

    //create an object of SedraCheck interface to be initialized using SedraCheckEngine.Builder below
    private var sc: GateToCheck? = null

    private val sedraCheckJourney: MutableLiveData<String?> =
        MutableLiveData<String?>()

    private val checkException: MutableLiveData<GateToCheckException?> =
        MutableLiveData<GateToCheckException?>()

    private val dataExtraction: MutableLiveData<DataExtractionModel?> =
        MutableLiveData<DataExtractionModel?>()

    private val livenessCheck: MutableLiveData<FaceMatchResponseModel?> =
        MutableLiveData<FaceMatchResponseModel?>()

    private val screeningCheck: MutableLiveData<ScreeningResponseModel?> =
        MutableLiveData<ScreeningResponseModel?>()

    private val closeJourney: MutableLiveData<CloseJourneyResponseModel?> =
        MutableLiveData<CloseJourneyResponseModel?>()

    private val nationalitiesAndIdTypes: MutableLiveData<NationalitiesAndIdTypesModel?> =
        MutableLiveData<NationalitiesAndIdTypesModel?>()
    private val updateRiskKYCResult = MutableLiveData<Int>()

    private val products: MutableLiveData<List<GetProductsResponseItem>?> =
        MutableLiveData<List<GetProductsResponseItem>?>()

    private val countries: MutableLiveData<CountriesAndCitiesResponse?> =
        MutableLiveData<CountriesAndCitiesResponse?>()
    private val formInfo: MutableLiveData<GetFormInfoResponseModel?> =
        MutableLiveData<GetFormInfoResponseModel?>()

    private val kycFormInfo: MutableLiveData<GetKYCResponseModel?> =
        MutableLiveData<GetKYCResponseModel?>()
    private val riskKycFormInfo: MutableLiveData<GetKYCResponseModel?> =
        MutableLiveData<GetKYCResponseModel?>()

    private val updateKYCResult: MutableLiveData<String> = MutableLiveData<String>()
    val uploadListener: MutableLiveData<String> = MutableLiveData<String>()
    val fileUploadListener: MutableLiveData<String> = MutableLiveData<String>()

    //private val updateRiskKYCResult: MutableLiveData<Int> = MutableLiveData<Int>()
     val  selectedProduct: MutableLiveData<String?> = MutableLiveData<String?>()

    private var selectedNationality: Nationality? = null
    var idType: IdType? = null;

    var hasDocumentScanner = true
    var hasScreening = true
    var hasLivenessCheck = true

    var isNeedOCR = false
    var customerId = ""
    var isLivenessEnabled = false
    var isDocumentVerificationEnabled = false


    fun getUpdateRiskKYCResult(): MutableLiveData<Int> {
        return updateRiskKYCResult
    }


    fun startJourney(
        subscriptionKey: String,
        baseUrl: String,
        activity: Activity,
        nationalNumber: String,
        riskNumber: String
    ) {
        //initialize SedraCheckEngine before calling any functions, and make sure to use the same
        //object, we recommend to keep it in the ViewModel and use it across your code
        sc = sc ?: GateToCheckEngine.Builder()
            .subscriptionKey(subscriptionKey)
            .appID("")
            .nationalID(nationalNumber)
            .riskID(riskNumber)
            .isOcrEnabled(isNeedOCR)
            .baseUrl(baseUrl)
            .build()


        //the first step must be use SedraCheckEngine to start a new journey.
        //Note that each time your user restarts the KYC journey you should start a new journey
        // because each journey will have a unique GUID to identify it.
        sc?.startJourney(
            checkCallback = object : CheckCallback<JourneyCreatedModel?> {
                override fun onResult(
                    result: JourneyCreatedModel?,
                    exception: GateToCheckException?
                ) {

                    //handle result and exception here

                    //if exception is not null then there is an issue and you should handle it or
                    //display a message to the user.
                    if (exception == null) {

                        //result is the journeyGuid, which should be stored on your backend to match
                        //this journey with the user ID from your backend.

                        sedraCheckJourney.value = result?.journeyId
                        isDocumentVerificationEnabled = result?.isDocumentVerification?:true
                        isLivenessEnabled = result?.isLiveness?:true
                        requestCountriesAndCities()

                    } else {

                        //All exceptions inherit from
                        //  open class GateToCheckException (message: String) : Exception(message)
                        //notice that GateToCheckException inherits from Exception class
                        //and each exception would have a technical message explaining what happened

                        checkException.value = exception

                        //all types of errors have thier own custom exception so you can build logic
                        //to display different types of messages or ui to your user as needed.

                    }
                }
            },
            context = activity,
            refranceNumber = customerId
        )
    }

    fun extractDataFromDocuments(scanResults: ArrayList<ScanResult>) {
        sc?.extractDataFromDocuments(checkCallback = object :
            CheckCallback<DataExtractionModel?> {
            override fun onResult(
                result: DataExtractionModel?,
                exception: GateToCheckException?
            ) {
                if (exception == null) {
                    dataExtraction.value = result ?: DataExtractionModel()
                } else {
                    checkException.value = exception
                }
            }
        }, scannedDocuments = scanResults)
    }

    fun startSelfieMatch(selfieImagePath: String) {
        this.selfieImagePath = selfieImagePath

        //this method requires the image path extracted from the liveness check activity
        sc?.startSelfieMatch(checkCallback = object : CheckCallback<FaceMatchResponseModel?> {
            override fun onResult(
                result: FaceMatchResponseModel?,
                exception: GateToCheckException?
            ) {

                livenessCheck.value = result ?: FaceMatchResponseModel()
            }
        }, selfieImagePath = selfieImagePath)
    }

    //in the screening request model, the first and last names are mandatory, but the second and
    //third are optional
    fun checkScreening(screeningRequestModel: ScreeningRequestModel) {
        sc?.startScreening(checkCallback = object : CheckCallback<ScreeningResponseModel?> {
            override fun onResult(
                result: ScreeningResponseModel?,
                exception: GateToCheckException?
            ) {
                screeningCheck.value = result ?: ScreeningResponseModel()
            }
        }, screeningRequestModel = screeningRequestModel)
    }

    fun closeJourney() {
        if (customerId.isNullOrBlank()) {
            customerId = Random(System.currentTimeMillis()).nextInt().toString()
        }
        sc?.closeJourney(checkCallback = object : CheckCallback<CloseJourneyResponseModel?> {
            override fun onResult(
                result: CloseJourneyResponseModel?,
                exception: GateToCheckException?
            ) {
                //if result is null or exception is not null then handle the error accordingly
                closeJourney.value = result ?: CloseJourneyResponseModel()
            }
        }, customerId = customerId)
    }


    fun getSedraCheckException(): LiveData<GateToCheckException?> {
        return checkException
    }

    fun clearException() {
        checkException.value = null
    }

    fun getSedraCheckJourney(): LiveData<String?> {
        return sedraCheckJourney
    }

    fun getScanDoc(): LiveData<DataExtractionModel?> {
        return dataExtraction
    }

    fun getLivenessCheck(): LiveData<FaceMatchResponseModel?> {
        return livenessCheck
    }

    fun getScreeningCheck(): LiveData<ScreeningResponseModel?> {
        return screeningCheck
    }

    fun getCloseJourney(): LiveData<CloseJourneyResponseModel?> {
        return closeJourney
    }

    fun getNationalitiesAndIdTypes(): LiveData<NationalitiesAndIdTypesModel?> {
        return nationalitiesAndIdTypes
    }

    fun getProducts(): LiveData<List<GetProductsResponseItem>?> {
        return products
    }

    fun setSelectedNationality(position: Int) {
        selectedNationality = nationalitiesAndIdTypes.value?.nationalities?.get(position)
    }

    fun getIdTypes(): ArrayList<IdType>? {
        return selectedNationality?.idTypes
    }

    fun getFormInfo(): LiveData<GetFormInfoResponseModel?> {
        return formInfo
    }

    fun getCountries(): LiveData<CountriesAndCitiesResponse?> {
        return countries
    }


    fun getKycForm(): LiveData<GetKYCResponseModel?> {
        return kycFormInfo
    }

    fun getRiskKycForm(): LiveData<GetKYCResponseModel?> {
        return riskKycFormInfo
    }

    fun getUpdateKYCResult(): LiveData<String> {
        return updateKYCResult
    }

    /* fun getUpdateRiskKYCResult(): LiveData<Int> {
         return updateRiskKYCResult
     }*/
    fun resetDataExtraction() {
        dataExtraction.value = null
    }

    fun resetState() {
        //reset all values to reset the state of the journey like new
        sedraCheckJourney.value = null
        checkException.value = null
        dataExtraction.value = null
        livenessCheck.value = null
        screeningCheck.value = null
        closeJourney.value = null
        nationalitiesAndIdTypes.value = null
    }

    fun requestProducts() {
        sc?.requestProducts(
            checkCallback = object : CheckCallback<List<GetProductsResponseItem>?> {
                override fun onResult(
                    result: List<GetProductsResponseItem>?,
                    exception: GateToCheckException?
                ) {
                    if (exception == null) {
                        products.value = result ?: listOf()
                    } else {
                        checkException.value = exception
                    }
                }
            }
        )
    }


    fun requestCountriesAndCities() {
        sc?.getCountriesAndCities(
            checkCallback = object : CheckCallback<CountriesAndCitiesResponse?> {
                override fun onResult(
                    result: CountriesAndCitiesResponse?,
                    exception: GateToCheckException?
                ) {
                    if (exception == null) {
                        countries.value = result
                    } else {
                        checkException.value = exception
                    }
                }
            }
        )
    }


    fun requestFormInfoByProduct() {
        sc?.requestFormInfoByProduct(checkCallback = object :
            CheckCallback<GetFormInfoResponseModel?> {
            override fun onResult(
                result: GetFormInfoResponseModel?,
                exception: GateToCheckException?
            ) {
                //if result is null or exception is not null then handle the error accordingly
                if (exception == null) {
                    val integrationList = ArrayList<IntegrationInfosItem>()
                    result?.let {
                        for (field in it.integrationInfos ?: emptyList()) {
                            if (field.integrationName.equals("OCR")) {
                                isNeedOCR = true

                            } else {
                                integrationList.add(field)
                            }
                        }

                    }
                    formInfo.value = GetFormInfoResponseModel(integrationInfos = integrationList)
                } else {
                    checkException.value = exception
                }
            }
        }, productId = selectedProduct.value?.toInt() ?: 0)
    }


    fun requestRiskKyc() {
        sc?.requestRiskKycInfo(
            checkCallback = object : CheckCallback<GetKYCResponseModel?> {
                override fun onResult(
                    result: GetKYCResponseModel?,
                    exception: GateToCheckException?
                ) {
                    if (exception == null) {
                        formId = result?.formId
                        riskKycFormInfo.value = result
                    } else {
                        checkException.value = exception
                    }
                }

            })
    }

    var formId: Int? = null;
    fun requestKycForm(fields: List<UpdateIntegrationFieldsItem>) {
        sc?.requestKycInfo(
            checkCallback = object : CheckCallback<GetKYCResponseModel?> {
                override fun onResult(
                    result: GetKYCResponseModel?,
                    exception: GateToCheckException?
                ) {
                    if (exception == null) {
                        formId = result?.formId
                        kycFormInfo.value = result
                        Log.d("KYC_FORM", "onResult: ${Gson().toJson(result)}")
                    } else {
                        checkException.value = exception
                    }
                }

            },
            getKYCRequestModel = GetKYCRequestModel(
                productId = selectedProduct.value?.toInt()?:0,
                fieldValues = fields
            )
        )
    }


    fun updateRiskKyc(updateItems: List<UpdateKYCRequestDynamicFieldsItem>) {


        sc?.postRiskKycInfo(
            checkCallback = object : CheckCallback<String?> {
                override fun onResult(
                    result: String?,
                    exception: GateToCheckException?
                ) {

                    updateRiskKYCResult.value = result?.toInt()
                }

            },
            updateKYCRequestModelList = updateItems
        )


    }

    fun updateKyc(updateItems: List<UpdateKYCRequestDynamicFieldsItem>) {


        sc?.postKycInfo(
            checkCallback = object : CheckCallback<String?> {
                override fun onResult(
                    result: String?,
                    exception: GateToCheckException?
                ) {
                    if (exception == null) {
                        updateKYCResult.value = Gson().toJson(result)
                    } else {
                        checkException.value = exception
                    }
                }

            },
            updateKYCRequestModelList = updateItems
        )


    }



    fun setIdType(position: Int,typeId: Int) {
        when (position) {
            0 -> {
                idType = IdType(typeId=typeId,numberOfImages = 2, currentImage = 1)
            }

            1 -> {
                idType = IdType(typeId=typeId,numberOfImages = 1, currentImage = 1)
            }

        }
    }

    var updateIntegrationFieldsItems = emptyList<UpdateIntegrationFieldsItem>()
    fun setUpdatedInfo(updateIntegrationFieldsItems: List<UpdateIntegrationFieldsItem>) {
        this.updateIntegrationFieldsItems = updateIntegrationFieldsItems
    }

    fun uploadFile(it: Uri, code: String) {
        val f = File(it.path)
        Log.d("TAG_FILE", "uploadFile: ${f.absolutePath}")
        Log.d("TAG_CODE", "uploadFile: ${code}")
        try {
            sc?.uploadDocument(
                f.absolutePath,
                code,
                checkCallback = object : CheckCallback<String?> {
                    override fun onResult(result: String?, exception: GateToCheckException?) {
                        if (result != null && exception == null) {
                            fileUploadListener.value = result ?: ""
                        } else {
                            checkException.value = exception
                        }
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
            //checkException.value = GateToCheckException(e.message)
        }

    }

    fun uploadImageFile(it: Uri) {
        val f = File(it.path)
        sc?.uploadImage(f.absolutePath, checkCallback = object : CheckCallback<String?> {
            override fun onResult(result: String?, exception: GateToCheckException?) {
                if (result != null && exception == null) {
                    uploadListener.value = result ?: ""
                } else {
                    checkException.value = exception
                }
            }

        })
    }

    var idTypes = MutableLiveData<List<EnumeratedValues>>()
    fun getIdTypesByNationality(){
        sc?.requestIdTypesByNationality(nationality = nationality?:"", residence = residence?:"", checkCallback = object : CheckCallback<List<EnumeratedValues>> {
            override fun onResult(result: List<EnumeratedValues>, exception: GateToCheckException?) {
                if(exception==null) {
                    idTypes.value = result
                }else{
                    checkException.value = exception
                }
            }
        })
    }

    fun getProductByIdType(){
        val riskLevel=updateRiskKYCResult.value.toString()
        sc?.getProductByIdType(riskLevel = riskLevel, customerIdentityType = idType?.typeId?:0, checkCallback = object : CheckCallback<Int?> {
            override fun onResult(result: Int?, exception: GateToCheckException?) {
                if(exception==null) {
                    selectedProduct.value= result?.toString()
                }else{
                    checkException.value = exception
                }
            }
        })
    }
    fun removeFormInfo() {
        formInfo.value = null
    }

    var nationality: String? = ""
    var residence: String? = ""
    fun setSelectedNationalityName(nationality: String) {

        this.nationality = nationality
    }

    fun setSelectedResidenceName(residance: String) {
        this.residence = residance
    }

}