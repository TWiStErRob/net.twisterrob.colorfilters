# Kotlin
# Targeting minSdkVersion 14 so JDK8 is not available yet
-dontnote kotlin.internal.jdk8.JDK8PlatformImplementations

# TODEL AGP 3.4
# Note: kotlin.coroutines.jvm.internal.DebugMetadataKt accesses a declared field 'label' dynamically
# let's keep its name to make sure that reflection works
-keepclassmembernames class kotlin.coroutines.jvm.internal.BaseContinuationImpl {
	*** label;
}

# androidx:core
# This is safe because it's on android.app.Notification.extras
# Note: androidx.core.app.NotificationCompatJellybean calls 'Field.getType'
# These are safe because they're on android.app.Notification$Action
# Note: androidx.core.app.NotificationCompatJellybean accesses a declared field 'title' dynamically
# Note: androidx.core.app.NotificationCompatJellybean accesses a declared field 'icon' dynamically
# Note: androidx.core.app.NotificationCompatJellybean accesses a declared field 'actionIntent' dynamically
-dontnote androidx.core.app.NotificationCompatJellybean

# androidx:*
# Trust that AndroidX libraries are ProGuard-ready
-dontnote androidx.**

# Glide vs. AndroidX
# Not using Glide.with(support.Fragment), so it's safe to ignore
#-dontwarn com.bumptech.glide.manager.SupportRequestManagerFragment
# but Jetifier is a better solution
