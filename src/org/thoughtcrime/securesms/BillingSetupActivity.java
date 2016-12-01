package org.thoughtcrime.securesms;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;
import org.thoughtcrime.securesms.util.TextSecurePreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BillingSetupActivity extends PassphraseRequiredActionBarActivity implements BillingAddFragment.AuthCodeReceivedListener, BillingListFragment.BillingItemClickListener, Button.OnClickListener {

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
        return "Set up Stripe billing integration";
    }

    @Override
    public void onCreate(Bundle bundle, @NonNull MasterSecret masterSecret) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Billing");
        this.billingAddFragment  = new BillingAddFragment();
        this.billingListFragment = new BillingListFragment();

        this.billingListFragment.setAddBillingButtonListener(this);
        this.billingListFragment.setBillingItemClickListener(this);
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

        Toast.makeText(this, "Auth code: " + code, Toast.LENGTH_SHORT).show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(
                Request.Method.POST,
                BuildConfig.STRIPE_OAUTH_URI,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(BillingSetupActivity.this, "Got creds! - " + response, Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        BillingInfo billingInfo = gson.fromJson(response, BillingInfo.class);
                        billingInfo.setName("Stripe");
                        billingInfo.setId("0");
                        billingInfo.setCreated(new Date().getTime());

                        TextSecurePreferences.setBillingCredentials(BillingSetupActivity.this, gson.toJson(billingInfo));
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BillingSetupActivity.this, "Failed trying to get OAuth credentials from Stripe! Oops!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("grant_type", "authorization_code");
                params.put("client_id", BuildConfig.STRIPE_CLIENT_ID);
                params.put("client_secret", BuildConfig.STRIPE_API_KEY);
                params.put("code", code);

                return params;
            }
        };

        // add the request to the queue.
        queue.add(strRequest);
    }

    @Override
    public void onError(String error, String description) {
        Toast.makeText(this, error + " - " + description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClicked(final String userId) {
        final ProgressDialog pd = ProgressDialog.show(
                this,
                getString(R.string.BillingListActivity_unlinking_billing_no_ellipsis),
                getString(R.string.BillingListActivity_unlinking_billing),
                true);

        RequestQueue queue = Volley.newRequestQueue(BillingSetupActivity.this);

        StringRequest strRequest = new StringRequest(
                Request.Method.POST,
                BuildConfig.STRIPE_OAUTH_REVOCATION_URI,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(BillingSetupActivity.this, "Stripe access revoked!", Toast.LENGTH_SHORT).show();
                        TextSecurePreferences.setBillingCredentials(BillingSetupActivity.this, null);

                        billingListFragment.resetLoader();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(BillingSetupActivity.this, "Failed trying to revoke Stripe access!", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", BuildConfig.STRIPE_CLIENT_ID);
                params.put("stripe_user_id", userId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + BuildConfig.STRIPE_API_KEY);

                return headers;
            }
        };

        // add the request to the queue.
        queue.add(strRequest);
    }
}
