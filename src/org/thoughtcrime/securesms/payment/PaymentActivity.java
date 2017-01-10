package org.thoughtcrime.securesms.payment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.thoughtcrime.securesms.BuildConfig;
import org.thoughtcrime.securesms.PassphraseRequiredActionBarActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.components.CreditCardView;
import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;

public class PaymentActivity extends PassphraseRequiredActionBarActivity {

    private PaymentController paymentController;
    private CardInformationReader cardInformationReader;
    private CardValidationController cardValidationController;

    private final DynamicTheme dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();

    @Override
    public void onPreCreate() {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
    }

    @Override
    public void onCreate(Bundle bundle, @NonNull MasterSecret masterSecret) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.PaymentActivity_title);

        setContentView(R.layout.payment_activity);

        String sellerNumber = getIntent().getStringExtra("SELLER_NUMBER");
        String productId = getIntent().getStringExtra("PRODUCT_ID");
        String skuId = getIntent().getStringExtra("SKU_ID");
        String productName = getIntent().getStringExtra("PRODUCT_NAME");

        Button payButton = (Button) findViewById(R.id.save);
        payButton.setEnabled(false);

        initializeViews(payButton);

        if (paymentController == null) {
            paymentController = new PaymentController(
                    this,
                    payButton,
                    cardInformationReader,
                    BuildConfig.STRIPE_PK,
                    sellerNumber,
                    productId,
                    skuId,
                    productName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentController = null;
        cardValidationController = null;
    }

    /**
     * For some reason this override is required to produce the appropriate back behaviour on click of the up/back arrow in the action bar.
     * @param item The menu item that was clicked (will only be the up/back arrow in this case)
     * @return the result of the operation
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // fespond to the action bar's Up/Home button by using the default back button behaviour
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews(Button payButton) {
        CreditCardView creditCardView = (CreditCardView) findViewById(R.id.credit_card);
        TextView validationErrorTextView = (TextView) findViewById(R.id.validation_error);

        cardInformationReader = new CardInformationReader(creditCardView);
        cardValidationController = new CardValidationController(this, creditCardView, payButton, validationErrorTextView);
    }
}
