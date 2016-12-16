package org.thoughtcrime.securesms.payment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.thoughtcrime.securesms.PassphraseRequiredActionBarActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;

import java.util.ArrayList;

public class PaymentListActivity extends PassphraseRequiredActionBarActivity {

    private static final String TAG = PaymentListActivity.class.getSimpleName();

    private final DynamicTheme dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();

    private PaymentListFragment paymentListFragment;
    private ProductListFragment productListFragment;

    private ArrayList<Product> products;
    private String sellerNumber;

    @Override
    public void onPreCreate() {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
    }

    @Override
    public void onCreate(Bundle bundle, @NonNull MasterSecret masterSecret) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int titleId = R.string.PaymentListActivity_payments_title;
        boolean isProducts = getIntent().getBooleanExtra("products", false);

        if (isProducts) {
            titleId = R.string.PaymentListActivity_products_title;

            products = getIntent().getParcelableArrayListExtra("PRODUCTS");
            sellerNumber = getIntent().getStringExtra("SELLER_NUMBER");

            Bundle args = new Bundle();
            args.putParcelableArrayList("PRODUCTS", products);
            args.putString("SELLER_NUMBER", sellerNumber);

            productListFragment = initFragment(android.R.id.content, new ProductListFragment(),
                    masterSecret, dynamicLanguage.getCurrentLocale(), args);
        } else {
            ArrayList<Payment> payments = getIntent().getParcelableArrayListExtra("PAYMENTS");

            Bundle args = new Bundle();
            args.putParcelableArrayList("PAYMENTS", payments);

            paymentListFragment = initFragment(android.R.id.content, new PaymentListFragment(),
                    masterSecret, dynamicLanguage.getCurrentLocale(), args);
        }

        getSupportActionBar().setTitle(titleId);
    }
}
