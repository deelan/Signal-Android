package org.thoughtcrime.securesms.payment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.thoughtcrime.securesms.R;

import java.util.Locale;

public class ProductListItem extends LinearLayout {

    private String   productId;
    private TextView name;
    private TextView amount;
    private TextView description;

    public ProductListItem(Context context) {
        super(context);
    }

    public ProductListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.name     = (TextView) findViewById(R.id.name);
        this.amount      = (TextView) findViewById(R.id.amount);
        this.description = (TextView) findViewById(R.id.description);
    }

    public void set(Product product, Locale locale) {
        this.name.setText(product.getName());

        double priceDbl = product.getAmount() / 100;
        String price = String.format("$ %.2f", priceDbl);

        if (product.getInterval() != null) {
            price = String.format("%s/%s", price, product.getInterval());
        }

        this.amount.setText(price);
        this.description.setText(product.getDescription());
        this.productId = product.getProductId();
    }
}
