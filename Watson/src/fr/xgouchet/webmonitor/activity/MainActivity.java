package fr.xgouchet.webmonitor.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.Constants;
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
			showTargetList();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs;
		prefs = getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Settings.updateFromPreferences(prefs);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);

		// TODO remove some contents
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			Log.i("TOTO", "GET CURRENT");
		} else {
			Log.i("TOTO", "null");
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {

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
		addTransactionToBackStack(transaction);
		transaction.commit();

		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showSettings() {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		Fragment fragment = new PrefsFragment();

		setTransactionAnimations(transaction);
		transaction.replace(android.R.id.primary, fragment, TAG_PREFS);
		addTransactionToBackStack(transaction);
		transaction.commit();

		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	private void showAbout() {

	}

	/**
	 * 
	 * @param transaction
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void setTransactionAnimations(FragmentTransaction transaction) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR2) {
			transaction.setCustomAnimations(android.R.animator.fade_in,
					android.R.animator.fade_out, android.R.animator.fade_in,
					android.R.animator.fade_out);
		} else {
			transaction.setCustomAnimations(android.R.animator.fade_in,
					android.R.animator.fade_out);
		}
	}

	/**
	 * 
	 * @param transaction
	 */
	private void addTransactionToBackStack(FragmentTransaction transaction) {
		if (getFragmentManager().findFragmentById(android.R.id.primary) != null) {
			transaction.addToBackStack(TAG_PREFS);
		}
	}
}
