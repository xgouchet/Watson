package fr.xgouchet.webmonitor.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.ui.fragment.PrefsFragment;
import fr.xgouchet.webmonitor.ui.fragment.TargetListFragment;

public class MainActivity extends Activity {

	public static final String TAG_LIST = "LIST";
	public static final String TAG_PREFS = "PREFS";
	public static final String TAG_ABOUT = "ABOUT";
	public static final String TAG_HELP = "HELP";

	// ////////////////////////////////////////////////////////////////////////////////////
	// Activity Lifecycle
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();

		String action = getIntent().getAction();
		Log.i("Action", action);

		// deep link
		if (Intent.ACTION_MANAGE_NETWORK_USAGE.equals(action)) {
			showSettings();
		} else {
			// Check if current fragment is set already
			if (getFragmentManager().getBackStackEntryCount() == 0) {
				showTargetList();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Settings.updateFromPreferences(prefs);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);

		// TODO remove some contents
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			Log.i("onCreateOptionsMenu", getFragmentManager()
					.getBackStackEntryAt(0).getName());
		} else {
			Log.i("onCreateOptionsMenu", "null");
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		boolean result = true;

		switch (item.getItemId()) {
		case R.id.menu_settings:
			showSettings();
			break;
		case R.id.menu_about:
			showAbout();
			break;
		case android.R.id.home:

			break;
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}

		return result;
	}

	@Override
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getFragmentManager().popBackStackImmediate()) {
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// Fragment Utils
	// ////////////////////////////////////////////////////////////////////////////////////

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showTargetList() {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		Fragment fragment = new TargetListFragment();

		setTransactionAnimations(transaction);
		transaction.replace(android.R.id.primary, fragment, TAG_LIST);
		addToBackstackIfDifferent(fragment, transaction, TAG_LIST);
		transaction.commit();

		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showSettings() {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		Fragment fragment = new PrefsFragment();

		setTransactionAnimations(transaction);
		transaction.replace(android.R.id.primary, fragment, TAG_PREFS);
		addToBackstackIfDifferent(fragment, transaction, TAG_PREFS);
		transaction.commit();

		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	private void showAbout() {

	}

	private void addToBackstackIfDifferent(final Fragment fragment,
			final FragmentTransaction transaction, final String tag) {
		FragmentManager fragmentMgr = getFragmentManager();
		int count = fragmentMgr.getBackStackEntryCount();

		if (count == 0) {
			return;
		}

		String name = fragmentMgr.getBackStackEntryAt(count - 1).getName();
		if (!TextUtils.isEmpty(name)) {
			Fragment top = fragmentMgr.findFragmentByTag(name);
			if ((top != null) && (top.getClass().equals(fragment.getClass()))) {
				return;
			}
		}

		transaction.addToBackStack(tag);
	}

	/**
	 * 
	 * @param transaction
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void setTransactionAnimations(final FragmentTransaction transaction) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2) {
			transaction.setCustomAnimations(android.R.animator.fade_in,
					android.R.animator.fade_out, android.R.animator.fade_in,
					android.R.animator.fade_out);
		} else {
			transaction.setCustomAnimations(android.R.animator.fade_in,
					android.R.animator.fade_out);
		}
	}
}
