package org.thoughtcrime.securesms.database.loaders;

import android.content.Context;

import com.google.gson.Gson;

import org.thoughtcrime.securesms.util.AsyncLoader;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.whispersystems.signalservice.internal.push.BillingInfo;

import java.util.ArrayList;
import java.util.List;

public class BillingListLoader extends AsyncLoader<List<BillingInfo>> {

    private static final String TAG = BillingListLoader.class.getSimpleName();

    public BillingListLoader(Context context) {
        super(context);
    }

    @Override
    public List<BillingInfo> loadInBackground() {
        String credsJson = TextSecurePreferences.getBillingCredentials(getContext());

        List<BillingInfo> list = new ArrayList<>();

        if (credsJson != null && !credsJson.equals("{}")) {
            Gson gson = new Gson();
            BillingInfo billingInfo = gson.fromJson(credsJson, BillingInfo.class);
            list.add(billingInfo);
        }

        return list;
    }
}
