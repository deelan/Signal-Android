package org.thoughtcrime.securesms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import org.thoughtcrime.securesms.components.CreditCardView;
import org.thoughtcrime.securesms.crypto.MasterSecret;
import org.thoughtcrime.securesms.payment.PaymentController;
import org.thoughtcrime.securesms.payment.CardInformationReader;
import org.thoughtcrime.securesms.payment.CardValidationController;
import org.thoughtcrime.securesms.payment.MessageDialogHandler;
import org.thoughtcrime.securesms.payment.ProgressDialogController;
import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;

public class PaymentActivity extends PassphraseRequiredActionBarActivity {

    private PaymentController paymentController;
    private CardInformationReader cardInformationReader;
    private MessageDialogHandler messageDialogHandler;
    private ProgressDialogController progressDialogController;
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

        initializeViews();

        Button saveButton = (Button) findViewById(R.id.save);
        if (paymentController == null) {
            paymentController = new PaymentController(
                    this,
                    saveButton,
                    cardInformationReader,
                    messageDialogHandler,
                    progressDialogController,
                    BuildConfig.STRIPE_PK,
                    sellerNumber,
                    productId,
                    skuId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentController = null;
        cardValidationController = null;
    }

    private void initializeViews() {
        CreditCardView creditCardView = (CreditCardView) findViewById(R.id.credit_card);
        TextView validationErrorTextView = (TextView) findViewById(R.id.validation_error);

        cardInformationReader = new CardInformationReader(creditCardView);

        progressDialogController = new ProgressDialogController(getSupportFragmentManager(), R.string.ConversationActivity__billing__processing_payment_title, R.string.ConversationActivity__billing__processing_payment_content);

        messageDialogHandler = new MessageDialogHandler(getSupportFragmentManager());

        cardValidationController = new CardValidationController(this, creditCardView, validationErrorTextView, messageDialogHandler);
    }
}
