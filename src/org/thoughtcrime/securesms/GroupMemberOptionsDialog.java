package org.thoughtcrime.securesms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;

import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.ThreadDatabase;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.recipients.RecipientFactory;
import org.thoughtcrime.securesms.recipients.Recipients;

public class GroupMemberOptionsDialog {

    private static final String TAG = GroupMemberOptionsDialog.class.getSimpleName();

    private final Recipient recipient;
    private final Context context;

    public GroupMemberOptionsDialog(Context context, Recipient recipient) {
        this.recipient = recipient;
        this.context   = context;
    }

    public void display() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.ConversationActivity_group_member_choose_action);
        builder.setIconAttribute(R.attr.group_members_dialog_icon);
        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.cancel, null);

        MemberOnClickListener mocl = new MemberOnClickListener(this.context, recipient);
        if (recipient.getContactUri() != null) {
            builder.setNegativeButton("View Contact", mocl);
        } else {
            builder.setNegativeButton("Add Contact", mocl);
        }

        builder.setPositiveButton("Send Message", mocl);
        builder.show();
    }

    private static class MemberOnClickListener implements DialogInterface.OnClickListener {
        private final Recipient member;
        private final Context context;

        public MemberOnClickListener(Context context, Recipient member) {
            this.context = context;
            this.member = member;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int item) {
            if (item == DialogInterface.BUTTON_NEGATIVE) {
                if (member.getContactUri() != null) {
                    ContactsContract.QuickContact.showQuickContact(context, new Rect(0, 0, 0, 0),
                        member.getContactUri(),
                        ContactsContract.QuickContact.MODE_LARGE, null);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, member.getNumber());
                    intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    context.startActivity(intent);
                }

            } else {
                Recipients recipients = RecipientFactory.getRecipientsFromString(context, member.getNumber(), true);

                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationActivity.RECIPIENTS_EXTRA, recipients.getIds());
                intent.setDataAndType(((ConversationActivity)context).getIntent().getData(), ((ConversationActivity)context).getIntent().getType());

                long existingThread = DatabaseFactory.getThreadDatabase(context).getThreadIdIfExistsFor(recipients);

                intent.putExtra(ConversationActivity.THREAD_ID_EXTRA, existingThread);
                intent.putExtra(ConversationActivity.DISTRIBUTION_TYPE_EXTRA, ThreadDatabase.DistributionTypes.DEFAULT);
                context.startActivity(intent);
            }
        }
    }
}
