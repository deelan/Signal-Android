package org.thoughtcrime.securesms;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.thoughtcrime.securesms.database.loaders.BillingListLoader;
import org.thoughtcrime.securesms.dependencies.InjectableType;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.ViewUtil;
import org.thoughtcrime.securesms.util.task.ProgressDialogAsyncTask;
import org.whispersystems.signalservice.api.SignalServiceBillingManager;
import org.whispersystems.signalservice.internal.push.BillingInfo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class BillingListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<BillingInfo>>, ListView.OnItemClickListener, Button.OnClickListener, InjectableType
{

    private static final String TAG = BillingListFragment.class.getSimpleName();

    private Locale                 locale;
    private View                   empty;
    private View                   progressContainer;
    private FloatingActionButton   addBillingButton;
    private Button.OnClickListener addBillingButtonListener;

    @Inject
    SignalServiceBillingManager billingManager;

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

    public void setAddBillingButtonListener(Button.OnClickListener listener) {
        this.addBillingButtonListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String billingName = ((BillingListItem)view).getBillingName();
        final String userId   = ((BillingListItem)view).getUserId();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.BillingListFragment_unlink_s, billingName));
        builder.setMessage(R.string.BillingListFragment_unlinking_billing_warning);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleRevokeAccess(userId);
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

    private void handleRevokeAccess(String userId) {
        new ProgressDialogAsyncTask<String, Void, Integer>(getActivity(),
                getString(R.string.BillingListFragment_unlinking_billing_no_ellipsis),
                getString(R.string.BillingListFragment_unlinking_billing))
        {
            private static final int SUCCESS        = 0;
            private static final int NETWORK_ERROR  = 1;

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    billingManager.revokeBillingAccess(params[0]);

                    TextSecurePreferences.setBillingCredentials(getActivity(), null);

                    return SUCCESS;
                } catch (IOException e) {
                    Log.w(TAG, e);
                    return NETWORK_ERROR;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);

                Context context = getActivity();

                switch (result) {
                    case SUCCESS:
                        Toast.makeText(context, R.string.BillingListFragment_unlinking_billing_success, Toast.LENGTH_SHORT).show();
                        getLoaderManager().restartLoader(0, null, BillingListFragment.this);
                        return;
                    case NETWORK_ERROR:
                        Toast.makeText(context, R.string.DeviceProvisioningActivity_content_progress_network_error, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }.execute(userId);
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
}
