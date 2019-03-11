object ApplicationId {
    val id = "com.hubofallthings.datatrader"
}

object Modules {
    val login = ":login"
    val signup = ":signup"
    val markdown = ":markdown"
    val calendar = ":calendar"
}

object Releases {
    val versionCode = 1
    val versionName = "1.0.1"
}

object Versions {
    val gradle = "3.2.1"

    val compileSdk = 28
    val minSdk = 21
    val targetSdk = 28

    val kotlin = "1.3.11"

    val hatApi = "0.0.31.7"
    val googleServices = "4.0.1"
    val firebase = "16.0.4"

    val fabric = "1.26.1"

    val jackson = "2.9.4.1"
    val fuel = "1.14.0"
    val appcompat = "28.0.0"
    val design = "28.0.0"
    val customTabs = "28.0.0"

    val recyclerview = "1.0.0"
    val constraint = "1.1.3"
    val maps = "15.0.1"

    val zxcvbn = "1.2.5"

    val glide = "4.8.0"
    val glideTransformation = "3.3.0"
    val crashlytics = "2.9.5"

    val junit = "4.12"
    val markdown = "0.19.0"

}

object Libraries {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    val maps = "com.google.android.gms:play-services-maps:${Versions.maps}"
    val mapsLocations = "com.google.android.gms:play-services-location:${Versions.maps}"

    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val glideTransformation = "jp.wasabeef:glide-transformations:${Versions.glideTransformation}"
    val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"

    val passwordzxcvbn = "com.nulab-inc:zxcvbn:${Versions.zxcvbn}"

    val jackson = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
    val fuel = "com.github.kittinunf.fuel:fuel-android:${Versions.fuel}"

    val markdown = "com.github.tiagohm.MarkdownView:library:${Versions.markdown}"
}
object HATApiLibrary {
    val api = "com.hubofallthings.android.hatApi:hat:${Versions.hatApi}"
}

object SupportLibraries {
    val appcompat = "com.android.support:appcompat-v7:${Versions.appcompat}"
    val design = "com.android.support:design:${Versions.design}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    val constraint = "com.android.support.constraint:constraint-layout:${Versions.constraint}"
    val customTabs = "com.android.support:customtabs:{${Versions.customTabs}}"
}


object FirebaseLibraries {
    val core = "com.google.firebase:firebase-core:${Versions.firebase}"
}

object TestLibraries {
    val junit = "junit:junit:${Versions.junit}"
}