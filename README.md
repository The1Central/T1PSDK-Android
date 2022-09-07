# T1P SDK Android - WebView embedded version

[![](https://jitpack.io/v/The1Central/T1PSDK-Android.svg)](https://jitpack.io/#mumayank/AirLocation)<a href="http://developer.android.com/index.html" target="_blank"><img src="https://img.shields.io/badge/platform-android-green.svg"/></a> <a href="https://android-arsenal.com/api?level=26" target="_blank"><img src="https://img.shields.io/badge/API-26%2B-green.svg?style=flat"/></a>

An android library to simplify the usage of Authentication with The1 Platform

Jump
to [Setup](https://github.com/The1Central/T1PSDK-Android/blob/master/README.md#Setup "Setup")
or [Usage](https://github.com/The1Central/T1PSDK-Android/blob/master/README.md#Usage "Usage")

# Setup

Add this line in your root build.gradle at the end of repositories:

```gradle
    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' } // this line
      }
    }
```
Add this line in your app build.gradle:
```gradle
    dependencies {
      implementation 'com.github.The1Central:T1PSDK-Android:LATEST_VERSION' // this line
    }
```
where LATEST_VERSION is [![](https://jitpack.io/v/The1Central/T1PSDK-Android.svg)](https://jitpack.io/#The1Central/T1PSDK-Android)

# Usage

1. Define environment(SIT, UAT, PVT, PROD), client Id, redirect URI and language for `T1PSDK`

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )
  }
}
```

2. To start sign in `sdk.signIn()`

- When sign in success T1PSDK will save access token, refresh token, expire time to Encrypted Shared
  Preference

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  private val signInActivityResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        result.data?.getParcelableExtra<AuthToken>(InitialPage.SIGN_IN.value)
          ?.let { authToken ->
            //authToken is value from T1PSDK
          }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )

    t1PSDK.signIn(onActivityResult = signInActivityResult)
  }
}
```

3. To start sign up `sdk.signUp()`

- When sign up success T1PSDK will save access token, refresh token, expire time to Encrypted Shared
  Preference

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  private val signUpActivityResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        result.data?.getParcelableExtra<AuthToken>(InitialPage.SIGN_UP.value)
          ?.let { authToken ->
            //authToken is value from T1PSDK
          }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )

    t1PSDK.signUp(onActivityResult = signUpActivityResult)
  }
}
```

4. To start recovery `sdk.recovery()`

- When recovery success T1PSDK will save access token, refresh token, expire time to Encrypted
  Shared Preference

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  private val recoveryActivityResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        result.data?.getParcelableExtra<AuthToken>(InitialPage.RECOVERY.value)
          ?.let { authToken ->
            //authToken is value from T1PSDK
          }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )

    t1PSDK.recovery(onActivityResult = recoveryActivityResult)
  }
}
```

5. To get access token `sdk.accessToken()`

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )

    t1PSDK.accessToken(object : T1PSdkAccessTokenCallback {
      override fun onSuccess(authToken: AuthToken) {
        //authToken is value from T1PSDK
      }

      override fun onFailure(t1pSdkFailed: T1PSdkFailedEnum) {
        //Type of Failed Exception
      }
    })
  }
}
```

6. To start sign out `sdk.signOut()`

- When sign out success or failure T1PSDK will remove access token, refresh token, expire time in
  Encrypted Shared Preference

Example:

```kotlin
    class MainActivity : AppCompatActivity() {

  private lateinit var t1PSDK: T1PSDK

  override fun onCreate(savedInstanceState: Bundle?) {
    ...
    t1PSDK = T1PSDK(
      activity = this,
      environment = T1PEnvironment.PROD,
      redirectUrl = "VALUE FROM The1 Engineering Teams",
      clientId = "VALUE FROM The1 Engineering Teams",
      language = T1PLanguage.EN
    )

    t1PSDK.signOut(object : T1PSdkCallback {
      override fun onSuccess() {
        //Notify logout success
      }

      override fun onFailure() {
        //Notify logout failure
      }
    })
  }
}
```
