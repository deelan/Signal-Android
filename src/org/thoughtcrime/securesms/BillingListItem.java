package org.thoughtcrime.securesms;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.thoughtcrime.securesms.util.DateUtils;
import org.whispersystems.signalservice.internal.push.BillingInfo;

import java.util.Locale;

public class BillingListItem extends LinearLayout {

    private String   billingId;
    private String   userIdString;
    private TextView name;
    private TextView created;
    private TextView userId;

    public BillingListItem(Context context) {
        super(context);
    }

    public BillingListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.name       = (TextView) findViewById(R.id.name);
        this.userId     = (TextView) findViewById(R.id.userId);
        this.created    = (TextView) findViewById(R.id.created);
    }

    public void set(BillingInfo billingInfo, Locale locale) {

        this.name.setText(billingInfo.getName());

        this.created.setText(getContext().getString(R.string.BillingListItem_linked_s,
                DateUtils.getDayPrecisionTimeSpanString(getContext(),
                        locale,
                        billingInfo.getCreated())));

        this.userId.setText(getContext().getString(R.string.BillingListItem_user_id_s, billingInfo.getUserId()));

        this.billingId = billingInfo.getId();
        this.userIdString = billingInfo.getUserId();
    }

    public String getUserId() { return userIdString; };

    public String getBillingId() {
        return billingId;
    }

    public String getBillingName() {
        return name.getText().toString();
    }
}
