# Kotlin
# Targeting minSdkVersion 14 so JDK8 is not available yet
-dontnote kotlin.internal.jdk8.JDK8PlatformImplementations

# TODO Probably a rouge rule from Android keeping every constructor with param "context"
# Note: the configuration keeps the entry point '...', but not the descriptor class '...'
-dontnote kotlin.coroutines.AbstractCoroutineContextElement
-dontnote kotlinx.coroutines.android.AndroidExceptionPreHandler

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
