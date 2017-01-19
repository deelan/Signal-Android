package org.thoughtcrime.securesms.payment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.push.BillingManagerFactory;
import org.whispersystems.signalservice.api.SignalServiceBillingManager;

import java.io.IOException;


/**
 * Controller used to create a charge token from the card information entered by the user and then process the charge in Stripe.
 */
public class PaymentController {

    private static final String TAG = PaymentController.class.getSimpleName();

    private FragmentActivity activity;
    private CardInformationReader cardInformationReader;
    private MessageDialogHandler messageDialogHandler;
    private ProgressDialogController progressDialogController;
    private String publishableKey;
    private String sellerNumber;
    private String productId;
    private String skuId;
    private String productName;

    public PaymentController(
            @NonNull FragmentActivity activity,
            @NonNull Button button,
            @NonNull CardInformationReader cardInformationReader,
            @NonNull String publishableKey,
            @NonNull String sellerNumber,
            @NonNull String productId,
            String skuId,
            @NonNull String productName) {
        this.activity = activity;
        this.cardInformationReader = cardInformationReader;
        this.messageDialogHandler = new MessageDialogHandler(activity.getSupportFragmentManager());
        this.publishableKey = publishableKey;
        this.progressDialogController = new ProgressDialogController(activity.getSupportFragmentManager(), R.string.ConversationActivity__billing__processing_payment_title, R.string.ConversationActivity__billing__processing_payment_content);;
        this.sellerNumber = sellerNumber;
        this.productId = productId;
        this.skuId = skuId;
        this.productName = productName;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPaymentWithCard();
            }
        });
    }

    public PaymentController(
            @NonNull FragmentActivity activity,
            @NonNull String sellerNumber,
            @NonNull String productId,
            String skuId,
            @NonNull String productName) {
        this.activity = activity;
        this.messageDialogHandler = new MessageDialogHandler(activity.getSupportFragmentManager());
        this.progressDialogController = new ProgressDialogController(activity.getSupportFragmentManager(), R.string.ConversationActivity__billing__processing_payment_title, R.string.ConversationActivity__billing__processing_payment_content);
        this.sellerNumber = sellerNumber;
        this.productId = productId;
        this.skuId = skuId;
        this.productName = productName;
    }

    private AsyncTask<String, Void, Integer> getPaymentTask(final String tokenId) {
        return new AsyncTask<String, Void, Integer>() {
            private static final int SUCCESS        = 0;
            private static final int NETWORK_ERROR  = 1;
            private static final int INTERNAL_ERROR = 2;
            private static final int CARD_ERROR     = 3;

            @Override
            protected Integer doInBackground(String... params) {
                String response = "";
                try {
                    Context context = activity;
                    String sellerNumber = params[0];
                    String productId = params[1];
                    String skuId = params[2];

                    SignalServiceBillingManager billingManager = BillingManagerFactory.createManager(context);

                    if (skuId != null) {
                        // TODO: ignore the return value? or do something with it?
                        billingManager.performCharge(productId, skuId, tokenId == null ? "" : tokenId, sellerNumber, productName);
                    } else {
                        billingManager.subscribeToPlan(productId, tokenId == null ? "" : tokenId, sellerNumber, productName);
                    }

                    return SUCCESS;
                } catch (IOException e) {
                    Log.w(TAG, e);

                    if (e.getMessage().contains("decline")) {
                        return CARD_ERROR;
                    }

                    return NETWORK_ERROR;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);

                Context context = activity;

                //TODO: handle error cases so that activity finishes
                progressDialogController.finishProgress();

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                switch (result) {
                    case SUCCESS:
                        builder.setTitle(R.string.PaymentActivity_payment_success_title);
                        builder.setMessage(R.string.PaymentActivity_payment_success);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishActivity();
                            }
                        });
                        builder.show();

                        return;
                    case CARD_ERROR:
                        builder.setMessage(R.string.PaymentActivity_payment_declined);
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                        break;
                    case NETWORK_ERROR:
                        Toast.makeText(context, R.string.PaymentActivity_network_error, Toast.LENGTH_LONG).show();
                        break;
                    case INTERNAL_ERROR:
                        Toast.makeText(context, R.string.PaymentActivity_payment_error, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
    }

    public void processPaymentWithStoredCustomer() {
        progressDialogController.startProgress();
        getPaymentTask(null).execute(sellerNumber, productId, skuId);
    }

    private void processPaymentWithCard() {
        Card cardToCharge = cardInformationReader.readCardData();
        progressDialogController.startProgress();

        new Stripe().createToken(
                cardToCharge,
                publishableKey,
                new TokenCallback() {
                    public void onSuccess(final Token token) {
                        getPaymentTask(token.getId()).execute(sellerNumber, productId, skuId);
                    }
                    public void onError(Exception error) {
                        messageDialogHandler.showMessage(R.string.PaymentActivity_validationErrors, error.getLocalizedMessage());
                        progressDialogController.finishProgress();
                    }
                });
    }

    private void finishActivity() {
        Intent resultIntent = new Intent();
        activity.setResult(Activity.RESULT_OK, resultIntent);
        activity.finish();
    }
}
