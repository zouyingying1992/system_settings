package de.florianweinaug.system_settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.lang.Exception
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry.Registrar

import io.flutter.plugin.common.BinaryMessenger
class SystemSettingsPlugin: FlutterPlugin, MethodCallHandler {

  var methodChannel: MethodChannel? = null
  var applicationContext: Context? = null
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val plugin = SystemSettingsPlugin();
      plugin.setupChannel(registrar.messenger(),registrar.context())
    }
  }

  fun setupChannel(messenger: BinaryMessenger, context: Context) {
    this.applicationContext =context;
    methodChannel = MethodChannel(messenger, "system_settings")
    methodChannel?.setMethodCallHandler(this)
  }
  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    setupChannel(binding.binaryMessenger, binding.applicationContext)
  }
  override fun onDetachedFromEngine(p0: FlutterPlugin.FlutterPluginBinding) {
    methodChannel?.setMethodCallHandler(null)
    methodChannel = null
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "app"                 -> openAppSettings()
      "app-notifications"   -> openAppNotificationSettings()
      "system"              -> openSystemSettings()
      "location"            -> openSetting(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
      "wifi"                -> openSetting(Settings.ACTION_WIFI_SETTINGS)
      "bluetooth"           -> openSetting(Settings.ACTION_BLUETOOTH_SETTINGS)
      "security"            -> openSetting(Settings.ACTION_SECURITY_SETTINGS)
      "display"             -> openSetting(Settings.ACTION_DISPLAY_SETTINGS)
      "date"                -> openSetting(Settings.ACTION_DATE_SETTINGS)
      "sound"               -> openSetting(Settings.ACTION_SOUND_SETTINGS)
      "apps"                -> openSetting(Settings.ACTION_APPLICATION_SETTINGS)
      "wireless"            -> openSetting(Settings.ACTION_WIRELESS_SETTINGS)
      "device-info"         -> openSetting(Settings.ACTION_DEVICE_INFO_SETTINGS)
      "data-usage"          -> openSetting(Settings.ACTION_DATA_USAGE_SETTINGS)
      "data-roaming"        -> openSetting(Settings.ACTION_DATA_ROAMING_SETTINGS)
      "locale"              -> openSetting(Settings.ACTION_LOCALE_SETTINGS)
      "default-apps"        -> openSetting(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
      "airplane-mode"       -> openSetting(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
      "privacy"             -> openSetting(Settings.ACTION_PRIVACY_SETTINGS)
      "accessibility"       -> openSetting(Settings.ACTION_ACCESSIBILITY_SETTINGS)
      "internal-storage"    -> openSetting(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
      "notification-policy" -> openSetting(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
      else                  -> result.notImplemented()
    }
  }

  private fun openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

    val uri = Uri.fromParts("package", applicationContext!!.packageName, null)
    intent.data = uri

    applicationContext!!.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  private fun openAppNotificationSettings() {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
              .putExtra(Settings.EXTRA_APP_PACKAGE, applicationContext!!.packageName)
    } else {
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
              .setData(Uri.parse("package:${applicationContext!!.packageName}"))
    }

    applicationContext!!.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }

  private fun openSetting(name: String) {
    try {
      applicationContext!!.startActivity(Intent(name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
      openSystemSettings()
    }
  }

  private fun openSystemSettings() {
    applicationContext!!.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
  }
}
