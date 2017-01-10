package org.thoughtcrime.securesms.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.thoughtcrime.securesms.PassphraseRequiredActionBarActivity;
import org.thoughtcrime.securesms.R;

import java.util.List;
import java.util.Locale;

public class ProductListFragment extends ListFragment implements ListView.OnItemClickListener {

    private static final String TAG = ProductListFragment.class.getSimpleName();

    private static final int PAYMENT = 100;

    private Locale                 locale;
    private View                   empty;
    private View                   progressContainer;

    private List<Product> products;
    private String sellerNumber;
    private String platformCustomerId;
    private String connectedCustomerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locale = (Locale) getArguments().getSerializable(PassphraseRequiredActionBarActivity.LOCALE_EXTRA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.product_list_fragment, container, false);

        this.empty             = view.findViewById(R.id.empty);
        this.progressContainer = view.findViewById(R.id.progress_container);

        sellerNumber = getArguments().getString("SELLER_NUMBER");
        products = getArguments().getParcelableArrayList("PRODUCTS");
        platformCustomerId = getArguments().getString("platformCustomerId");
        connectedCustomerId = getArguments().getString("connectedCustomerId");

        setListAdapter(new ProductListFragment.ProductListAdapter(getActivity(), R.layout.product_list_item_view, products, locale));

        if (products == null || products.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product product = products.get(position);

        if (platformCustomerId != null) {
            PaymentController pc = new PaymentController(getActivity(), sellerNumber, product.getProductId(), product.getSkuId(), product.getName());
            pc.processPaymentWithStoredCustomer();
        } else {
            Intent paymentIntent = new Intent(getActivity(), PaymentActivity.class);

            if (sellerNumber != null) {
                paymentIntent.putExtra("SELLER_NUMBER", sellerNumber);
                paymentIntent.putExtra("PRODUCT_ID", product.getProductId());
                paymentIntent.putExtra("SKU_ID", product.getSkuId());
                paymentIntent.putExtra("PRODUCT_NAME", product.getName());
                startActivityForResult(paymentIntent, PAYMENT);
            }
        }
    }

    @Override
    public void onActivityResult(final int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PAYMENT && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
    }

    private static class ProductListAdapter extends ArrayAdapter<Product> {

        private final int    resource;
        private final Locale locale;

        public ProductListAdapter(Context context, int resource, List<Product> objects, Locale locale) {
            super(context, resource, objects);
            this.resource = resource;
            this.locale = locale;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity)getContext()).getLayoutInflater().inflate(resource, parent, false);
            }

            ((ProductListItem)convertView).set(getItem(position), locale);

            return convertView;
        }
    }
}
