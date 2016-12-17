package org.thoughtcrime.securesms.payment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.util.DateUtils;

import java.util.Locale;

public class PaymentListItem extends LinearLayout {

    private String   paymentId;
    private TextView description;
    private TextView created;
    private TextView amount;

    public PaymentListItem(Context context) {
        super(context);
    }

    public PaymentListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.description = (TextView) findViewById(R.id.description);
        this.amount      = (TextView) findViewById(R.id.amount);
        this.created     = (TextView) findViewById(R.id.created);
    }

    public void set(Payment payment, Locale locale) {

        this.description.setText(payment.getDescription());

        this.created.setText(getContext().getString(R.string.PaymentListItem_paid_s,
                DateUtils.getDayPrecisionTimeSpanString(getContext(), locale, payment.getCreated().getTime())));

        double amountInCAD = payment.getAmount() / 100;
        this.amount.setText(getContext().getString(R.string.PaymentListItem_amount_s, String.format("%.2f", amountInCAD)));

        this.paymentId = payment.getId();
    }
}
