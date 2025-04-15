# === ROOM (SQLite ORM) ===
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }

# === ViewModel (MVVM Arch) ===
-keep class androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModel

# === If using coroutines (optional) ===
-keepclassmembers class kotlinx.coroutines.** { *; }

# === Compose (nur falls nötig – meist nicht notwendig) ===
# Compose wird automatisch von R8 unterstützt

# === Keep annotations (z. B. für Gson, Room, etc.) ===
-keepattributes *Annotation*

# === Optional: Für bessere Debug-Infos (stack traces) ===
-keepattributes SourceFile,LineNumberTable

# === (Optional) für deine eigenen Datenklassen mit Gson:
# -keep class com.example.bloodpressureapp.model.** { *; }

# === Entferne unnötige Klassen automatisch:
# Das macht R8 – du musst nichts manuell angeben.

# === Logging (optional entfernen) ===
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}