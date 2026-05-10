# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep class com.magnet.search.data.model.** { *; }
-keep class org.jsoup.** { *; }
-keep class okhttp3.** { *; }
-dontwarn org.jspecify.annotations.**
-dontwarn javax.annotation.**
