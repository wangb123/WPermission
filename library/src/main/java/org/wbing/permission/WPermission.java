package org.wbing.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 获取权限
 * <p>•Normal Permissions如下 （不需要动态申请，只需要在清单文件注册即可）
 * ACCESS_LOCATION_EXTRA_COMMANDS
 * ACCESS_NETWORK_STATE
 * ACCESS_NOTIFICATION_POLICY
 * ACCESS_WIFI_STATE
 * BLUETOOTH
 * BLUETOOTH_ADMIN
 * BROADCAST_STICKY
 * CHANGE_NETWORK_STATE
 * CHANGE_WIFI_MULTICAST_STATE
 * CHANGE_WIFI_STATE
 * DISABLE_KEYGUARD
 * EXPAND_STATUS_BAR
 * GET_PACKAGE_SIZE
 * INSTALL_SHORTCUT
 * INTERNET
 * KILL_BACKGROUND_PROCESSES
 * MODIFY_AUDIO_SETTINGS
 * NFC
 * READ_SYNC_SETTINGS
 * READ_SYNC_STATS
 * RECEIVE_BOOT_COMPLETED
 * REORDER_TASKS
 * REQUEST_INSTALL_PACKAGES
 * SET_ALARM
 * SET_TIME_ZONE
 * SET_WALLPAPER
 * SET_WALLPAPER_HINTS
 * TRANSMIT_IR
 * UNINSTALL_SHORTCUT
 * USE_FINGERPRINT
 * VIBRATE
 * WAKE_LOCK
 * WRITE_SYNC_SETTINGS
 * <p>•Dangerous Permissions: (需要动态申请，当然也要在清单文件声明)
 * group:android.String-group.CONTACTS
 * String:android.String.WRITE_CONTACTS
 * String:android.String.GET_ACCOUNTS
 * String:android.String.READ_CONTACTS
 * group:android.String-group.PHONE
 * String:android.String.READ_CALL_LOG
 * String:android.String.READ_PHONE_STATE
 * String:android.String.CALL_PHONE
 * String:android.String.WRITE_CALL_LOG
 * String:android.String.USE_SIP
 * String:android.String.PROCESS_OUTGOING_CALLS
 * String:com.android.voicemail.String.ADD_VOICEMAIL
 * group:android.String-group.CALENDAR
 * String:android.String.READ_CALENDAR
 * String:android.String.WRITE_CALENDAR
 * group:android.String-group.CAMERA
 * String:android.String.CAMERA
 * group:android.String-group.SENSORS
 * String:android.String.BODY_SENSORS
 * group:android.String-group.LOCATION
 * String:android.String.ACCESS_FINE_LOCATION
 * String:android.String.ACCESS_COARSE_LOCATION
 * group:android.String-group.STORAGE
 * String:android.String.READ_EXTERNAL_STORAGE
 * String:android.String.WRITE_EXTERNAL_STORAGE
 * group:android.String-group.MICROPHONE
 * String:android.String.RECORD_AUDIO
 * group:android.String-group.SMS
 * String:android.String.READ_SMS
 * String:android.String.RECEIVE_WAP_PUSH
 * String:android.String.RECEIVE_MMS
 * String:android.String.RECEIVE_SMS
 * String:android.String.SEND_SMS
 * String:android.String.READ_CELL_BROADCASTS
 *
 * @author wangbing
 * @date 2018/8/27
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WPermission {
    String[] value();
}
