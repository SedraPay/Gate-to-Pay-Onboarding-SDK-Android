<p align="center">
  <img src="https://github.com/SedraPay/Gate-to-Pay-Onboarding-SDK/blob/main/gatetopay.png" alt="Icon"/>
</p>
<H1 align="center">Gate to Pay Onboarding SDK</H1>

The new eKYC in simple way.

`Gate to Pay Onboarding SDK` is between your hands to help you onboard your customer easily with almost no effort.

## Screenshot
[![GatetoPayOnboardingSDK](https://github.com/SedraPay/Gate-to-Pay-Onboarding-SDK/blob/main/screenshot.png)](https://youtu.be/8oehz24fXI4)

## Requirements

Minimum SDK = 24 The target device must have Google Play Services The fowling permissions needs to
be added to your app’s manifest

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools" package="com.sedra.check.sample">

  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-permission android:name="android.permission.RECORD_AUDIO" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
          tools:ignore="ScopedStorage" />
  <uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />

  <!-- The rest of you manifest here -->

</manifest>
```

## Installation

To install the SDK you need to import it in your application's gradle file
```groovy
    implementation "com.gatetopay:onboarding-sdk:0.1.1"
```
and you need to add to your build gradle the url of the github repo
```groovy
       repositories {
  google()
  mavenCentral()
  maven { url = uri("https://jitpack.io") } // for MPAndroidChart'https://jitpack.io' }
  maven {
    url = uri("https://maven.pkg.github.com/sedrapay/Abyan-Android")

    credentials {
      username = "..."
      password = "..."
    }
  }
}
```
and if setting.gradle is available, you need to add the url of the github repo in the resolution
management section

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven {
      url = uri("https://maven.pkg.github.com/sedrapay/Abyan-Android")

      credentials {
        username = "..."
        password = "..."
      }
    }
  }
}
```

The SDK is developed in Kotlin so you need to add the fowling at the top level of your gradle file:

```groovy
plugins {
  id 'com.android.application'
  id 'kotlin-android'
  id 'kotlin-kapt'
}
```

and make sure to have the fowling inside your ‘android’ module in gradle

```groovy
android {
  ...
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  ...
}
```

## Notes

> The provided sample code and all the documentation and examples bellow are all written in Kotlin.

## Initializing the SDK

To initialize the SDK you should use the Builder class provided with SedraCheckEngine to help you
setup the SDK’s functionality


### Full initialization

```kotlin
/// This function is required to initialize the SDK correctly.
/// You must provide all parameters to ensure proper functionality.
///
/// - Parameters:
///   - serverKey: The key provided to you from the portal.
///   - serverURLString: The base URL provided by the sales team.
///   - nationalNumber: Required so the backend can communicate with Civil Status Authority to retrieve user information.
///   - riskFormId: Used to fetch the first request which contains the risk form data.
///   - applicationId: Unique identifier used to label each KYC onboarding journey so it can be easily distinguished
sc = sc ?: GateToCheckEngine.Builder()
  .subscriptionKey(subscriptionKey)
  .nationalID(nationalNumber)
  .riskID(riskNumber)
  .baseUrl(baseUrl)
  .appID(appId)
  .build()
```

Note the GateToCheckEngine interface provides access to all the exposed APIs of the SDK. To use any of the
functionality that will be explained in the rest of this document you need to make sure you reuse
the same instance otherwise you will lose any intermediate information between different methods. To
handle this, we recommend keeping the GateToCheckEngine instance inside a ViewModel that will stay alive if
the user is still in the related journey. You can also keep an instance of GateToCheckEngine class alive in
a Singleton class, or any similar way. If the instance of GateToCheckEngine class is destroyed and
recreated, you will lose key info to connect different steps into one journey.

## Start new journey

A GateToCheck journey is comprised of a number of steps, based on your subsicription, for example
here is a full journey:

1. Start Journey
2. risk kyc form by your risk form id
2. Scan Documents (if your bundle available)
3. Liveness check and image matching (if your bundle available)
4. Kyc Form
5. Compliance check


### Start Journey

Using the instance of GateToCheck we created in the previous section, here is how to request to start
a new journey.

Note that each time your user restarts the KYC journey you should start a new journey because each
journey will have a unique GUID to identify it.

```kotlin
      ae?.startJourney(
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

        journeyId = result?.journeyId
        //will be true if the ocr scan bundle is available
        isDocumentVerificationEnabled = result?.isDocumentVerification?:true
        //will be true if the liveness check bundle is available
        isLivenessEnabled = result?.isLiveness?:true
        
        requestCountriesAndCities()

      } else {

        //All exceptions inherit from
        //  open class GateToCheckException (message: String) : Exception(message)
        //notice that GateToCheckException inherits from Exception class
        //and each exception would have a technical message explaining what happened

        checkException = exception

        //all types of errors have their own custom exception so you can build logic
        //to display different types of messages or ui to your user as needed.

      }
    }
  },
  context = activity
)
```

The result object for this step is needed currently to tell you if you are subscribed or not

Note that all the methods use the same callback structure

```kotlin
interface CheckCallback<T> {
  fun onResult(
    result: T,
    exception: GateToCheckException? = null
  )
}
```

if the exception object is not null then the method has failed, for further information on
exceptions, read the Exceptions section in this document.

## Countries and Cities
Call this method only once and cache the response (countries and cities) locally you can call it whenever you want after startJourney and before getKyc.
This data will be used later in the KYC screen to handle data types like country and countryCity.

```kotlin
fun requestCountriesAndCities() {
    ae?.getCountriesAndCities(
            checkCallback = object : CheckCallback<CountriesAndCitiesResponse?> {
                override fun onResult(
                    result: CountriesAndCitiesResponse?,
                    exception: GateToCheckException?
                ) {
                    if(exception==null) {
                        countries.value = result
                    }else{
                        checkException.value = exception
                    }
                }
            }
        )
    }
```

## Risk Kyc Form
To Get the Risk KYC fields you need to call the requestRiskKycInfo function the function takes empty array,
and the productId as a parameter the productId is the id of the risk form you want to get it
```kotlin
    fun requestRiskKyc() {
    ae?.requestRiskKycInfo(
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

```
the returned fields have multiple types as below:
```kotlin
    enum class ViewTypes(val id:Int){
        TEXT_FIELD(1),
        DROPDOWN(2),
        CHECKBOX(3),
        RADIO_BUTTON(4),
        DATE_TIME(5),
        BOOLEAN(6),
        FILE(7),
        IMAGE(8),
        COUNTRY(9),
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
```
after drawing your own ui for the fields you need to call the postRiskKycInfo function to continue the flow you should have a field with id type on your risk kyc form 
save the selected value from it 
```kotlin
    fun updateRiskKyc(updateItems: List<UpdateKYCRequestDynamicFieldsItem>) {


    ae?.postRiskKycInfo(
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

```
this call will return the risk calculation as int tha you will use later on getProductByIdType call with tha saved id type value saved to get the rest kyc info fields by the returned id 

```kotlin
    fun getProductByIdType(){
    val riskLevel=updateRiskKYCResult.value.toString()
    ae?.getProductByIdType(riskLevel = riskLevel, customerIdentityType = idType?.typeId?:0, checkCallback = object : CheckCallback<Int?> {
        override fun onResult(result: Int?, exception: GateToCheckException?) {
            if(exception==null) {
                selectedProduct.value= result?.toString()
            }else{
                checkException.value = exception
            }
        }
    })
}

```

## Scan ID

The scan ID functionality is split into 2 steps

1. Document Scanner – provided as an Activity for result contract
2. Data extraction function that will take the files paths from the Document Scanner to extract data
   from the images

### Open document scanner

```kotlin
//
//In Activity or Fragment:
//
val scans = ArrayList<ScanResult>()

var getScannerResult: ActivityResultLauncher<IdType> =
  registerForActivityResult(ScannerResultContract()) { imagePath ->
    if (imagePath != null) {
      binding.btnNext.visibility = View.GONE
      binding.progressIndicator.visibility = View.VISIBLE

      scans.add(ScanResult(imagePath, IdType.ID, IdPage.FRONT))
      //do logic here if you need to get front and back image of an id you need to launch the 
      //scanner again

      //after you get all the needed images, you need to call the extractDataFromDocuments()
      //method from GateToCheck, in this example the extractDataFromDocuments() is inside a 
      //view model:
      model.extractDataFromDocuments(scans)
    } else {
      // handle no result returned
    }
  }

binding.btnNext.setOnClickListener {
  //when lauching the scanner you need to provide what type of id is being scanned from IdType enum 
  // class
  getScannerResult.launch(IdType.ID)
}
```

### Start extracting data from document images

```kotlin
//
//In ViewModel:
//
fun extractDataFromDocuments(scanResults: ArrayList<ScanResult>) {
  ae?.extractDataFromDocuments(checkCallback = object :
    CheckCallback<DataExtractionResponseModel?> {
    override fun onResult(
      result: DataExtractionResponseModel?,
      exception: GateToCheckException?
    ) {
      if (exception == null) {
        //this function will take all the images as a list of ScanResults, and will return 
        //the data extracted from the documents (if possible) in the below model
        dataExtraction.value = result ?: DataExtractionResponseModel()
      } else {
        sedraCheckException.value = exception
      }
    }
  }, scannedDocuments = scanResults)
}

//
//DataExtractionResponseModel:
//
class DataExtractionResponseModel {
  //extractedFields is a list of all the extracted data 
  var extractedFields: ArrayList<ExtractedFields> = arrayListOf()

  //validationResult represents all the validation checks performed as a list and the result of
  //each check and an overall result
  var validationResult: ValidationResult? = ValidationResult()

  //ids of images for reference
  var frontImageId: String? = null
  var backImageId: String? = null
}
```

The result object will return all the fields that GateToCheck was able to extract from the provided
images & all the validations with success/failure status for each validation

## Liveness Check and Image Matching

These 2 functions are related to each other and split into 2 steps 1- Selfie Camera – provided as an
Activity 2- Image Matching method that will compare 2 faces

### Selfie Camera

```kotlin
binding.btnTakeSelfie.setOnClickListener {
    binding.btnTakeSelfie.visibility = View.GONE
    val steps = ArrayList<FaceDetectionStep>().apply {
        //The order these steps will be displayed to the end-user will always be random,
        //except that the SMILE step will always be the last one.
        add(FaceDetectionStep.BLINK)
        add(FaceDetectionStep.LOOK_LEFT)

        //Adding the same step multiple times will be ignored, and it will be shown to the
        //end-user only once.
        add(FaceDetectionStep.LOOK_RIGHT)
        add(FaceDetectionStep.LOOK_RIGHT)

        //Note that SMILE step will always be added, even if you don't include it, and will
        //always be the last step shown to the user, this is to make sure the captured
        //selfie image is the best possible.
        add(FaceDetectionStep.SMILE)

    }

    getLivenessCheckResult.launch(steps)
}
```

### Start face matching check

```kotlin
fun startSelfieMatch(selfieImagePath: String) {
    this.selfieImagePath = selfieImagePath

    //this method requires the image path extracted from the liveness check activity
    ae?.startSelfieMatch(checkCallback = object : CheckCallback<FaceMatchResponseModel?> {
        override fun onResult(
            result: FaceMatchResponseModel?,
            exception: GateToCheckException?
        ) {
            livenessCheck.value = result ?: FaceMatchResponseModel()
        }
    }, selfieImagePath = selfieImagePath)
}
```

the result object will provide you with a flag ‘isIdentical’ and confidence level (0-1)


## Get KYC
To Get the KYC fields you need to call the requestKycInfo function the function takes empty array  as a parameter,
and the productId that is stored from the getProductByIdType function as a parameter
```kotlin
     fun requestKycForm(){
     ae?.requestKycInfo(
     checkCallback = object : CheckCallback<GetKYCResponseModel?> {
     override fun onResult(
     result: GetKYCResponseModel?,
     exception: GateToCheckException?
     ) {
     formId = result?.formId
     kycFormInfo.value = Gson().toJson(result)
     }
     
                 },
                 getKYCRequestModel = GetKYCRequestModel( productId =  selectedProduct?.id, fieldValues = listOf())
             )
         }
```
the returned fields have multiple types as below:

```kotlin
    enum class ViewTypes(val id:Int){
        TEXT_FIELD(1),
        DROPDOWN(2),
        CHECKBOX(3),
        RADIO_BUTTON(4),
        DATE_TIME(5),
        BOOLEAN(6),
        FILE(7),
        IMAGE(8),
        COUNTRY(9),
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
```
after drawing your own ui for the fields you need to call the requestKycForm function to continue the flow
```kotlin

     ae?.postKycInfo(
            checkCallback = object : CheckCallback<String?> {
                override fun onResult(
                    result: String?,
                    exception: GateToCheckException?
                ) {
                    updateKYCResult .value = Gson().toJson(result)
                }

            },
            updateKYCRequestModelList =list)
```


## Close Journey

This method is used to inform the GateToCheck portal that a certain journy has been completed, and
with it you should send a customer ID.

This Customer ID, should be an ID used in your core system. This way you will have the ability to
search GateToCheck portal by customer ID.

```kotlin
fun closeJourney(customerId: String) {
    ae?.closeJourney(checkCallback = object : CheckCallback<CloseJourneyResponseModel?> {
        override fun onResult(
            result: CloseJourneyResponseModel?,
            exception: GateToCheckException?
        ) {
            closeJourney.value = result ?: CloseJourneyResponseModel()
        }
    }, customerId = customerId)
}
```

## Exceptions

- All methods will return failures or errors as custom exceptions, and all exceptions inherit from
  GateToCheckException
- Each exception will have a message string to provide the developer with a clear description of the
  problem to be able to track and debug it.
- If any CheckCallback returns null exception that the method has completed its operation
  successfully. If CheckCallback returns any exception, then you can safely ignore the result
  object.
- Also CheckCallback provides rawInput object in all cases to show the developer how the SDK
  received the data you provided to further help in debug.
- GateToCheck Android SDK does not have any internal log or print statement.

## Contact Us

If you have any questions or you want to contact us, visit
our [website](https://gatetopay.com) or contact us via email mobdev@gatetopay.com