-verbose

# keep everything in order to try to not crash the tests
-dontshrink
-dontoptimize
-dontpreverify
-keep class ** { *; }
-keepattributes *

-dontnote
-dontwarn

# list all the things that the above includes
-dontnote kotlin.reflect.**

-dontnote org.junit.**
-dontnote junit.framework.**
-dontnote junit.runner.**

-dontnote org.mockito.**
-dontwarn org.mockito.**
-dontnote net.bytebuddy.**
-dontwarn net.bytebuddy.**
-dontnote org.objenesis.**
-dontwarn org.objenesis.**

-dontnote android.support.test.espresso.core.**
-dontwarn android.support.test.espresso.core.**
-dontnote android.support.test.espresso.base.**
-dontnote android.support.test.runner.**
-dontnote android.support.test.internal.**
-dontnote android.support.test.rule.**

-dontnote org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.XmlPullParser
-dontnote org.xmlpull.v1.XmlPullParserException
-dontnote org.xmlpull.v1.XmlPullParserFactory
-dontnote org.xmlpull.v1.XmlSerializer
-dontwarn org.xmlpull.v1.XmlSerializer


# https://code.google.com/p/android/issues/detail?id=194513
# Reading library jar [P:\tools\android-sdk-windows\platforms\android-23\optional\org.apache.http.legacy.jar]
# Note: duplicate definition of library class [...]
# Note: there were 7 duplicate class definitions.
#       (http://proguard.sourceforge.net/manual/troubleshooting.html#duplicateclass)
-dontnote android.net.http.SslError
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.SslCertificate$DName
-dontnote org.apache.http.conn.scheme.HostNameResolver
-dontnote org.apache.http.conn.scheme.SocketFactory
-dontnote org.apache.http.conn.ConnectTimeoutException
-dontnote org.apache.http.params.HttpParams

# Reading library jar [P:\tools\sdk\android\platforms\android-24\optional\org.apache.http.legacy.jar]
# Note: duplicate definition of library class [...]
# Note: there were 4 duplicate class definitions.
#       (http://proguard.sourceforge.net/manual/troubleshooting.html#duplicateclass)
-dontnote android.net.http.HttpResponseCache
-dontnote org.apache.http.conn.scheme.LayeredSocketFactory
-dontnote org.apache.http.params.CoreConnectionPNames
-dontnote org.apache.http.params.HttpConnectionParams

# Android Gradle Plugin adds core-lambda-stubs.jar unconditionally (not using Java 8) to the classpath,
# even though android.jar has these classes.
# Reading library jar [P:\tools\sdk\android\build-tools\28.0.3\core-lambda-stubs.jar]
# Note: duplicate definition of library class [...]
# Note: there were 6 duplicate class definitions.
#       (http://proguard.sourceforge.net/manual/troubleshooting.html#duplicateclass)
-dontnote java.lang.invoke.CallSite
-dontnote java.lang.invoke.LambdaConversionException
-dontnote java.lang.invoke.MethodHandle
-dontnote java.lang.invoke.MethodHandles$Lookup
-dontnote java.lang.invoke.MethodHandles
-dontnote java.lang.invoke.MethodType
