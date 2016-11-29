package org.thoughtcrime.securesms.sms;

public class InfoMessage extends IncomingTextMessage {

    public InfoMessage(IncomingTextMessage base, String body) {
        super(base, body);
    }

    @Override
    public boolean isSecureMessage() {
        return true;
    }

    @Override
    public InfoMessage withMessageBody(String body) {
        return new InfoMessage(this, body);
    }
}
