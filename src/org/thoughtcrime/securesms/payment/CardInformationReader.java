package org.thoughtcrime.securesms.payment;

import com.stripe.android.model.Card;

import org.thoughtcrime.securesms.components.CreditCardView;


/**
 * A class that reads the UI.
 */
public class CardInformationReader {

    private CreditCardView creditCardView;

    public CardInformationReader(
            CreditCardView creditCardView) {
        this.creditCardView = creditCardView;
    }

    /**
     * Read the user input and create a {@link Card} from it.\
     *
     * @return a {@link Card} based on the currently displayed user input
     */
    public Card readCardData() {
        Card cardToSave = creditCardView.getCard();
        if (cardToSave != null) {
            return cardToSave;
        }

        return new Card.Builder("", 1, 0, "").build();
    }
}
