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
    maven { url = uri("https://jitpack.io") } // for MPAndroidChart'https://jitpack.io' }
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

    ae = ae ?: GateToCheckEngine.Builder()
  .subscriptionKey("YOUR_KEY_HERE")
  .baseUrl("YOUR_BASE_URL_HERE")
  .nationalID(nationalNumber)
  .riskID(riskNumber)
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

      } else {

        //All exceptions inherit from
        //  open class AbyanException (message: String) : Exception(message)
        //notice that AbyanException inherits from Exception class
        //and each exception would have a technical message explaining what happened

        checkException = exception

        //all types of errors have thier own custom exception so you can build logic
        //to display different types of messages or ui to your user as needed.

      }
    }
  },
  context = activity
)
}
```

The result object for this step is needed currently to tell you if you are subscribed or not

Note that all the methods use the same callback structure

```kotlin
interface CheckCallback<T> {
  fun onResult(
    result: T,
    exception: AbyanException? = null
  )
}
```

if the exception object is not null then the method has failed, for further information on
exceptions, read the Exceptions section in this document.