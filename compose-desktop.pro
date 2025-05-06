-dontoptimize

-dontwarn androidx.compose.desktop.DesktopTheme*
-dontwarn kotlinx.datetime.**
-dontwarn okio.**


-keep class org.fife.** { *; }
-dontnote org.fife.**

-keep class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe

-keep class com.jetbrains.JBR* { *; }
-dontnote com.jetbrains.JBR*

-keep class com.sun.jna** { *; }
-dontnote com.sun.jna**

-dontnote androidx.compose.ui.input.key.KeyEvent_desktopKt

-dontnote androidx.compose.ui.input.key.KeyEvent_skikoKt
-dontwarn androidx.compose.ui.input.key.KeyEvent_skikoKt

-dontnote org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**
-dontwarn org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**