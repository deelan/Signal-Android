package org.thoughtcrime.securesms.payment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.thoughtcrime.securesms.ApplicationContext;
import org.thoughtcrime.securesms.PassphraseRequiredActionBarActivity;
import org.thoughtcrime.securesms.R;

import java.util.List;
import java.util.Locale;

public class PaymentListFragment extends ListFragment {

    private static final String TAG = PaymentListFragment.class.getSimpleName();

    private Locale                 locale;
    private View                   empty;
    private View                   progressContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locale = (Locale) getArguments().getSerializable(PassphraseRequiredActionBarActivity.LOCALE_EXTRA);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ApplicationContext.getInstance(context).injectDependencies(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.payment_list_fragment, container, false);

        this.empty             = view.findViewById(R.id.empty);
        this.progressContainer = view.findViewById(R.id.progress_container);

        List<Payment> payments = getArguments().getParcelableArrayList("PAYMENTS");
        setListAdapter(new PaymentListFragment.PaymentListAdapter(getActivity(), R.layout.payment_list_item_view, payments, locale));

        if (payments == null || payments.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }

        return view;
    }

    private static class PaymentListAdapter extends ArrayAdapter<Payment> {

        private final int    resource;
        private final Locale locale;

        public PaymentListAdapter(Context context, int resource, List<Payment> objects, Locale locale) {
            super(context, resource, objects);
            this.resource = resource;
            this.locale = locale;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity)getContext()).getLayoutInflater().inflate(resource, parent, false);
            }

            ((PaymentListItem)convertView).set(getItem(position), locale);

            return convertView;
        }
    }
}
