/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import android.util.Log;
import com.google.zxing.Result;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.library.Book;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A base class for the Android-specific barcode handlers. These allow the app to polymorphically
 * suggest the appropriate actions for each data type.
 *
 * This class also contains a bunch of utility methods to take common actions like opening a URL.
 * They could easily be moved into a helper object, but it can't be static because the Activity
 * instance is needed to launch an intent.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public abstract class ResultHandler {

  private static final String TAG = ResultHandler.class.getSimpleName();

  private static final DateFormat DATE_FORMAT;
  static {
    DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    // For dates without a time, for purposes of interacting with Android, the resulting timestamp
    // needs to be midnight of that day in GMT. See:
    // http://code.google.com/p/android/issues/detail?id=8330
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
  }
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

  public static final int MAX_BUTTON_COUNT = 4;

  private final ParsedResult result;
  private final Activity activity;
  private final Result rawResult;
  private final String customProductSearch;

  ResultHandler(Activity activity, ParsedResult result) {
    this(activity, result, null);
  }

  ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
    this.result = result;
    this.activity = activity;
    this.rawResult = rawResult;
    this.customProductSearch = parseCustomSearchURL();
  }

  public ParsedResult getResult() {
    return result;
  }

  /**
   * Indicates how many buttons the derived class wants shown.
   *
   * @return The integer button count.
   */
  public abstract int getButtonCount();

  /**
   * The text of the nth action button.
   *
   * @param index From 0 to getButtonCount() - 1
   * @return The button text as a resource ID
   */
  public abstract int getButtonText(int index);


  /**
   * Execute the action which corresponds to the nth button.
   *
   * @param index The button that was clicked.
   */
  public abstract void handleButtonPress(int index);

  /**
   * Some barcode contents are considered secure, and should not be saved to history, copied to
   * the clipboard, or otherwise persisted.
   *
   * @return If true, do not create any permanent record of these contents.
   */
  public boolean areContentsSecure() {
    return false;
  }

  /**
   * Create a possibly styled string for the contents of the current barcode.
   *
   * @return The text to be displayed.
   */
  public CharSequence getDisplayContents() {
    String contents = result.getDisplayResult();
    return contents.replace("\r", "");
  }

  /**
   * A string describing the kind of barcode that was found, e.g. "Found contact info".
   *
   * @return The resource ID of the string.
   */
  public abstract int getDisplayTitle();

  /**
   * A convenience method to get the parsed type. Should not be overridden.
   *
   * @return The parsed type, e.g. URI or ISBN
   */
  public final ParsedResultType getType() {
    return result.getType();
  }

  private static long calculateMilliseconds(String when) {
    if (when.length() == 8) {
      // Only contains year/month/day
      Date date;
      synchronized (DATE_FORMAT) {
        date = DATE_FORMAT.parse(when, new ParsePosition(0));
      }
      // Note this will be relative to GMT, not the local time zone
      return date.getTime();
    } else {
      // The when string can be local time, or UTC if it ends with a Z
      Date date;
      synchronized (DATE_TIME_FORMAT) {
       date = DATE_TIME_FORMAT.parse(when.substring(0, 15), new ParsePosition(0));
      }
      long milliseconds = date.getTime();
      if (when.length() == 16 && when.charAt(15) == 'Z') {
        Calendar calendar = new GregorianCalendar();
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        milliseconds += offset;
      }
      return milliseconds;
    }
  }

  final void openBookSearch(String isbn) {
    Uri uri = Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD() +
        "/books?vid=isbn" + isbn);
    launchIntent(1, new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openURL(String isbn) {
	  Intent intent = new Intent();
	  Bundle bl = new Bundle();
	  bl.putString("isbn", isbn);
	  intent.putExtras(bl);
	  launchIntent(2, intent);
  }

  final void webSearch(String query) {
    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
    intent.putExtra("query", query);
    launchIntent(0, intent);
  }

  void launchIntent(int buttonIndex, Intent intent) {
    if (intent != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
      try {
    	switch(buttonIndex) {
    	case 0:
    		break;
    	case 1:
    		Log.e("openbs", "openbs");
    		break;
    	case 2:
    		Log.e("openURI", "openURI");
    		intent.setClass(activity, Book.class);
    		break;
    	}
        activity.startActivity(intent);
      } catch (ActivityNotFoundException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.msg_intent_failed);
        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();
      }
    }
  }

  private static void putExtra(Intent intent, String key, String value) {
    if (value != null && value.length() > 0) {
      intent.putExtra(key, value);
    }
  }

  protected void showNotOurResults(int index, AlertDialog.OnClickListener proceedListener) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    if (prefs.getBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, false)) {
      // already seen it, just proceed
      proceedListener.onClick(null, index);
    } else {
      // note the user has seen it
      prefs.edit().putBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, true).commit();
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setMessage(R.string.msg_not_our_results);
      builder.setPositiveButton(R.string.button_ok, proceedListener);
      builder.show();
    }
  }

  private String parseCustomSearchURL() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    String customProductSearch = prefs.getString(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH,
        null);
    if (customProductSearch != null && customProductSearch.trim().length() == 0) {
      return null;
    }
    return customProductSearch;
  }

  String fillInCustomSearchURL(String text) {
    String url = customProductSearch.replace("%s", text);
    if (rawResult != null) {
      url = url.replace("%f", rawResult.getBarcodeFormat().toString());
    }
    return url;
  }

}
