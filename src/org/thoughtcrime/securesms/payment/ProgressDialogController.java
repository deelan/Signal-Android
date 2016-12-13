package org.thoughtcrime.securesms.payment;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;


/**
 * Class used to show and hide the progress spinner.
 */
public class ProgressDialogController {

    private FragmentManager mFragmentManager;
    private ProgressDialogFragment mProgressFragment;

    public ProgressDialogController(@NonNull FragmentManager fragmentManager, int titleId, int msgId) {
        mFragmentManager = fragmentManager;
        mProgressFragment = ProgressDialogFragment.newInstance(titleId, msgId);
    }

    void startProgress() {
        mProgressFragment.show(mFragmentManager, "progress");
    }

    void finishProgress() {
        mProgressFragment.dismiss();
    }
}
