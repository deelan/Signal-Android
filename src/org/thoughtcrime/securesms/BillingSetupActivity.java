package org.thoughtcrime.securesms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.push.BillingManagerFactory;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.task.ProgressDialogAsyncTask;
import org.whispersystems.signalservice.api.SignalServiceBillingManager;
import org.whispersystems.signalservice.internal.push.BillingInfo;

import java.io.IOException;
import java.util.Date;

public class BillingSetupActivity extends PassphraseRequiredActionBarActivity implements BillingAddFragment.AuthCodeReceivedListener, Button.OnClickListener {

    private static final String TAG = BillingSetupActivity.class.getSimpleName();

    private final DynamicTheme    dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();

    private BillingAddFragment  billingAddFragment;
    private BillingListFragment billingListFragment;

    @Override
    public void onPreCreate() {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
    }

    public static CharSequence getSummary(Context context) {
        return context.getString(R.string.BillingSetupActivity_summary);
    }

    @Override
    public void onCreate(Bundle bundle, @NonNull MasterSecret masterSecret) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.BillingSetupActivity_title));
        this.billingAddFragment  = new BillingAddFragment();
        this.billingListFragment = new BillingListFragment();

        this.billingListFragment.setAddBillingButtonListener(this);
        this.billingAddFragment.setAuthCodeReceivedListener(this);

        if (getIntent().getBooleanExtra("add", false)) {
            initFragment(android.R.id.content, billingAddFragment, masterSecret, dynamicLanguage.getCurrentLocale());
        } else {
            initFragment(android.R.id.content, billingListFragment, masterSecret, dynamicLanguage.getCurrentLocale());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dynamicTheme.onResume(this);
        dynamicLanguage.onResume(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, billingAddFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCodeReceived(String data) {
        final String code = data;

        new ProgressDialogAsyncTask<String, Void, Integer>(this,
                getString(R.string.BillingSetupActivity_connect_dialog_title),
                getString(R.string.BillingSetupActivity_connect_dialog_content))
        {
            private static final int SUCCESS        = 0;
            private static final int NETWORK_ERROR  = 1;

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    Context                     context          = BillingSetupActivity.this;
                    SignalServiceBillingManager billingManager   = BillingManagerFactory.createManager(context);

                    BillingInfo billingInfo = billingManager.connectAccount(params[0]);

                    billingInfo.setCreated(new Date().getTime());

                    // TODO: don't hard code these?
                    billingInfo.setName("Stripe");
                    billingInfo.setId("0");

                    Gson gson = new Gson();
                    TextSecurePreferences.setBillingCredentials(BillingSetupActivity.this, gson.toJson(billingInfo));

                    return SUCCESS;
                } catch (IOException e) {
                    Log.w(TAG, e);
                    return NETWORK_ERROR;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);

                Context context = BillingSetupActivity.this;

                switch (result) {
                    case SUCCESS:
                        Toast.makeText(context, getString(R.string.BillingSetupActivity_connect_success), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    case NETWORK_ERROR:
                        Toast.makeText(context, R.string.DeviceProvisioningActivity_content_progress_network_error, Toast.LENGTH_LONG).show();
                        break;
                }

                getSupportFragmentManager().popBackStackImmediate();
            }
        }.execute(code);
    }

    @Override
    public void onError(String error, String description) {
        // TODO: do something more with this (and clean up the error to use a localized string)
        Toast.makeText(this, error + " - " + description, Toast.LENGTH_SHORT).show();
    }
}
