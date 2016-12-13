package org.thoughtcrime.securesms.payment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.stripe.android.model.Card;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.components.CreditCardView;


/**
 * Created by simonkenny on 08/12/2016.
 */

public class CardValidationController {

    private CreditCardView mCreditCardView;
    private TextView mValidationErrorTextView;
    private MessageDialogHandler mMessageDialogHandler;
    private final AppCompatActivity activity;

    public CardValidationController(
            @NonNull final AppCompatActivity activity,
            @NonNull CreditCardView creditCardView,
            @NonNull final Button payButton,
            @NonNull TextView validationErrorTextView,
            @NonNull MessageDialogHandler messageDialogHandler) {
        this.activity = activity;
        mCreditCardView = creditCardView;
        mValidationErrorTextView = validationErrorTextView;
        mMessageDialogHandler = messageDialogHandler;

        creditCardView.setCallback(new CreditCardView.Callback() {
            @Override
            public void onValidated(Card card) {
                mMessageDialogHandler.showMessage(R.string.PaymentActivity_validationSuccess, mCreditCardView.getContext().getString(R.string.PaymentActivity_cardValidatedMessage));
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

                payButton.setEnabled(true);
                // NOTE : could automatically save, or focus on other field, etc., at this point
                //          as we have a validated Card object
            }

            @Override
            public void onError(int errorCode) {
                showValidationError(errorCode);
            }

            @Override
            public void onClearError() {
                showValidationError(CreditCardView.ERROR_NONE);
            }
        });
    }

    private void showValidationError(int errorCode) {
        if (errorCode == CreditCardView.ERROR_NUMBER) {
            mValidationErrorTextView.setText(R.string.PaymentActivity_cardErrorNumber);
        } else if (errorCode == CreditCardView.ERROR_EXPIRY_MONTH
                || errorCode == CreditCardView.ERROR_EXPIRY_YEAR) {
            mValidationErrorTextView.setText(R.string.PaymentActivity_cardErrorExpDate);
        } else if (errorCode == CreditCardView.ERROR_CVC) {
            mValidationErrorTextView.setText(R.string.PaymentActivity_cardErrorCvc);
        } else if (errorCode == CreditCardView.ERROR_UNKNOWN) {
            mValidationErrorTextView.setText(R.string.PaymentActivity_cardErrorUnknown);
        } else {
            mValidationErrorTextView.setText("");
        }
    }
}
