# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep SVG library classes
-keep class com.caverock.androidsvg.** { *; }

# Keep DataStore
-keep class androidx.datastore.*.** { *; }
