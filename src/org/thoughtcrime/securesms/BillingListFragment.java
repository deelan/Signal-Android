package org.thoughtcrime.securesms;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.thoughtcrime.securesms.database.loaders.BillingListLoader;
import org.thoughtcrime.securesms.util.ViewUtil;

import java.util.List;
import java.util.Locale;

public class BillingListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<BillingInfo>>, ListView.OnItemClickListener, Button.OnClickListener
{

    private static final String TAG = BillingListFragment.class.getSimpleName();

    private Locale                 locale;
    private View                   empty;
    private View                   progressContainer;
    private FloatingActionButton   addBillingButton;
    private Button.OnClickListener addBillingButtonListener;
    private BillingItemClickListener billingItemClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locale = (Locale) getArguments().getSerializable(PassphraseRequiredActionBarActivity.LOCALE_EXTRA);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ApplicationContext.getInstance(activity).injectDependencies(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.billing_list_fragment, container, false);

        this.empty             = view.findViewById(R.id.empty);
        this.progressContainer = view.findViewById(R.id.progress_container);
        this.addBillingButton  = ViewUtil.findById(view, R.id.add_billing_integration);
        this.addBillingButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getLoaderManager().initLoader(0, null, this);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public Loader<List<BillingInfo>> onCreateLoader(int id, Bundle args) {
        empty.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);

        return new BillingListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BillingInfo>> loader, List<BillingInfo> data) {
        progressContainer.setVisibility(View.GONE);

        setListAdapter(new BillingListFragment.BillingListAdapter(getActivity(), R.layout.billing_list_item_view, data, locale));

        if (data.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BillingInfo>> loader) {
        setListAdapter(null);
    }

    public void resetLoader() {
        getLoaderManager().restartLoader(0, null, BillingListFragment.this);
    }

    public void setAddBillingButtonListener(Button.OnClickListener listener) {
        this.addBillingButtonListener = listener;
    }

    public void setBillingItemClickListener(BillingItemClickListener listener) {
        this.billingItemClickListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String billingName = ((BillingListItem)view).getBillingName();
        final String userId   = ((BillingListItem)view).getUserId();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.BillingListActivity_unlink_s, billingName));
        builder.setMessage(R.string.BillingListActivity_unlinking_billing_warning);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                billingItemClickListener.onItemClicked(userId);
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if (addBillingButtonListener != null) {
            addBillingButtonListener.onClick(v);
        }
    }

    private static class BillingListAdapter extends ArrayAdapter<BillingInfo> {

        private final int    resource;
        private final Locale locale;

        public BillingListAdapter(Context context, int resource, List<BillingInfo> objects, Locale locale) {
            super(context, resource, objects);
            this.resource = resource;
            this.locale = locale;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((Activity)getContext()).getLayoutInflater().inflate(resource, parent, false);
            }

            ((BillingListItem)convertView).set(getItem(position), locale);

            return convertView;
        }
    }

    public interface BillingItemClickListener {
        public void onItemClicked(final String userId);
    }
}
