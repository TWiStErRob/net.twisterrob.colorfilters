package net.twisterrob.android.view;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is to make layout editor work:
 * java.lang.UnsupportedOperationException: Unsupported Service: audio
 *   at com.android.layoutlib.bridge.android.BridgeContext.getSystemService(BridgeContext.java:465)
 *   at android.inputmethodservice.KeyboardView.<init>(KeyboardView.java:379)
 *   at android.inputmethodservice.KeyboardView.<init>(KeyboardView.java:283)
 *   at android.inputmethodservice.KeyboardView.<init>(KeyboardView.java:279)
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressWarnings("deprecation")
public class KeyboardViewEditModeContext extends Context {
    private final Context wrapped;

    public KeyboardViewEditModeContext(Context context) {
        this.wrapped = context;
    }

    @Override
    public AssetManager getAssets() {
        return wrapped.getAssets();
    }

    @Override
    public Resources getResources() {
        return wrapped.getResources();
    }

    @Override
    public PackageManager getPackageManager() {
        return wrapped.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver() {
        return wrapped.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return wrapped.getMainLooper();
    }

    @Override
    public Context getApplicationContext() {
        return wrapped.getApplicationContext();
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        wrapped.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        wrapped.unregisterComponentCallbacks(callback);
    }

    @Override
    public void setTheme(int resid) {
        wrapped.setTheme(resid);
    }

    @Override
    public Resources.Theme getTheme() {
        return wrapped.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return wrapped.getClassLoader();
    }

    @Override
    public String getPackageName() {
        return wrapped.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return wrapped.getApplicationInfo();
    }

    @Override
    public String getPackageResourcePath() {
        return wrapped.getPackageResourcePath();
    }

    @Override
    public String getPackageCodePath() {
        return wrapped.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return wrapped.getSharedPreferences(name, mode);
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return wrapped.openFileInput(name);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return wrapped.openFileOutput(name, mode);
    }

    @Override
    public boolean deleteFile(String name) {
        return wrapped.deleteFile(name);
    }

    @Override
    public File getFileStreamPath(String name) {
        return wrapped.getFileStreamPath(name);
    }

    @Override
    public File getFilesDir() {
        return wrapped.getFilesDir();
    }

    @Override public File getNoBackupFilesDir() {
        return wrapped.getNoBackupFilesDir();
    }

    @Override
    public File getExternalFilesDir(String type) {
        return wrapped.getExternalFilesDir(type);
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        return wrapped.getExternalFilesDirs(type);
    }

    @Override
    public File getObbDir() {
        return wrapped.getObbDir();
    }

    @Override
    public File[] getObbDirs() {
        return wrapped.getObbDirs();
    }

    @Override
    public File getCacheDir() {
        return wrapped.getCacheDir();
    }

    @Override public File getCodeCacheDir() {
        return wrapped.getCodeCacheDir();
    }

    @Override
    public File getExternalCacheDir() {
        return wrapped.getExternalCacheDir();
    }

    @Override
    public File[] getExternalCacheDirs() {
        return wrapped.getExternalCacheDirs();
    }

    @Override public File[] getExternalMediaDirs() {
        return wrapped.getExternalMediaDirs();
    }

    @Override
    public String[] fileList() {
        return wrapped.fileList();
    }

    @Override
    public File getDir(String name, int mode) {
        return wrapped.getDir(name, mode);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return wrapped.openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return wrapped.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @Override
    public boolean deleteDatabase(String name) {
        return wrapped.deleteDatabase(name);
    }

    @Override
    public File getDatabasePath(String name) {
        return wrapped.getDatabasePath(name);
    }

    @Override
    public String[] databaseList() {
        return wrapped.databaseList();
    }

    @Override
    @Deprecated
    public Drawable getWallpaper() {
        return wrapped.getWallpaper();
    }

    @Override
    @Deprecated
    public Drawable peekWallpaper() {
        return wrapped.peekWallpaper();
    }

    @Override
    @Deprecated
    public int getWallpaperDesiredMinimumWidth() {
        return wrapped.getWallpaperDesiredMinimumWidth();
    }

    @Override
    @Deprecated
    public int getWallpaperDesiredMinimumHeight() {
        return wrapped.getWallpaperDesiredMinimumHeight();
    }

    @Override
    @Deprecated
    public void setWallpaper(Bitmap bitmap) throws IOException {
        wrapped.setWallpaper(bitmap);
    }

    @Override
    @Deprecated
    public void setWallpaper(InputStream data) throws IOException {
        wrapped.setWallpaper(data);
    }

    @Override
    @Deprecated
    public void clearWallpaper() throws IOException {
        wrapped.clearWallpaper();
    }

    @Override
    public void startActivity(Intent intent) {
        wrapped.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        wrapped.startActivity(intent, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        wrapped.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        wrapped.startActivities(intents, options);
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        wrapped.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        wrapped.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        wrapped.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        wrapped.sendBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        wrapped.sendOrderedBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        wrapped.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        wrapped.sendBroadcastAsUser(intent, user);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        wrapped.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        wrapped.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        wrapped.sendStickyBroadcast(intent);
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        wrapped.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        wrapped.removeStickyBroadcast(intent);
    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        wrapped.sendStickyBroadcastAsUser(intent, user);
    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        wrapped.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        wrapped.removeStickyBroadcastAsUser(intent, user);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return wrapped.registerReceiver(receiver, filter);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return wrapped.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        wrapped.unregisterReceiver(receiver);
    }

    @Override
    public ComponentName startService(Intent service) {
        return wrapped.startService(service);
    }

    @Override
    public boolean stopService(Intent service) {
        return wrapped.stopService(service);
    }

    @Override
    public boolean bindService(Intent service, @NonNull ServiceConnection conn, int flags) {
        return wrapped.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(@NonNull ServiceConnection conn) {
        wrapped.unbindService(conn);
    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName className, String profileFile, Bundle arguments) {
        return wrapped.startInstrumentation(className, profileFile, arguments);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        try {
            return wrapped.getSystemService(name);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @Override
    public int checkPermission(@NonNull String permission, int pid, int uid) {
        return wrapped.checkPermission(permission, pid, uid);
    }

    @Override
    public int checkCallingPermission(@NonNull String permission) {
        return wrapped.checkCallingPermission(permission);
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String permission) {
        return wrapped.checkCallingOrSelfPermission(permission);
    }

    @Override
    public void enforcePermission(@NonNull String permission, int pid, int uid, String message) {
        wrapped.enforcePermission(permission, pid, uid, message);
    }

    @Override
    public void enforceCallingPermission(@NonNull String permission, String message) {
        wrapped.enforceCallingPermission(permission, message);
    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String permission, String message) {
        wrapped.enforceCallingOrSelfPermission(permission, message);
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        wrapped.grantUriPermission(toPackage, uri, modeFlags);
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        wrapped.revokeUriPermission(uri, modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return wrapped.checkUriPermission(uri, pid, uid, modeFlags);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return wrapped.checkCallingUriPermission(uri, modeFlags);
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return wrapped.checkCallingOrSelfUriPermission(uri, modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        return wrapped.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        wrapped.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        wrapped.enforceCallingUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        wrapped.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        wrapped.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return wrapped.createPackageContext(packageName, flags);
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        return wrapped.createConfigurationContext(overrideConfiguration);
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return wrapped.createDisplayContext(display);
    }

    @Override
    public boolean isRestricted() {
        return wrapped.isRestricted();
    }
}
