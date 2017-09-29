## Add project specific ProGuard rules here.
## By default, the flags in this file are appended to flags specified
## in /Users/oldfeel/android/android-sdk-macosx/tools/proguard/proguard-android.txt
## You can edit the include path and order by changing the proguardFiles
## directive in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## Add any project specific keep options here:
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#
## com.android.support:appcompat-v7
## com.android.support:recyclerview-v7
#-keep class android.support.v7.widget.** { *; }
#-keep class android.support.v7.internal.widget.** { *; }
#-keep class android.support.v7.internal.view.menu.** { *; }
#
#-keep class * extends android.support.v4.view.ActionProvider {
#    public <init>(android.content.Context);
#}
#
## com.android.support:support-v13
#-keep class android.support.v4.app.** { *; }
#
## com.android.support:design
#-keep class android.support.design.widget.** { *; }
#
## com.google.code.gson:gson
#
## com.j256.ormlite:ormlite-core
## com.j256.ormlite:ormlite-android
#-keep class com.j256.**
#-keepclassmembers class com.j256.** { *; }
#-keep enum com.j256.**
#-keepclassmembers enum com.j256.** { *; }
#-keep interface com.j256.**
#-keepclassmembers interface com.j256.** { *; }
#
## Keep the helper class and its constructor
#-keep class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
#-keepclassmembers class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
#  public <init>(android.content.Context);
#}
#
## Keep the annotations
#-keepattributes *Annotation*
#
## Keep all model classes that are used by OrmLite
## Also keep their field names and the constructor
#-keep @com.j256.ormlite.table.DatabaseTable class * {
#    @com.j256.ormlite.field.DatabaseField <fields>;
#    @com.j256.ormlite.field.ForeignCollectionField <fields>;
#}
## com.qiniu:qiniu-android-sdk
#
## de.hdodenhof:circleimageview
#
## com.squareup.picasso:picasso
#-keep class com.squareup.picasso.** { *; }
#-keep interface com.squareup.picasso.** { *; }
#-dontwarn com.squareup.picasso.**
#
## com.jakewharton:butterknife
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}
#
## com.nostra13.universalimageloader:universal-image-loader
#
#-dontwarn com.pgyersdk.**
#-keep class com.pgyersdk.** { *; }
#
#-dontwarn com.baidu.**
#-keep class com.baidu.** { *; }
#-keep class vi.com.gdi.bgl.android.java.** {*;}