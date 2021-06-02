package com.example.testing.payment;

public class CardPaymentCharge {

    private final boolean wasCardDebited;

    public CardPaymentCharge(boolean isCardDebited) {
        this.wasCardDebited = isCardDebited;
    }

    public boolean isWasCardDebited() {
        return wasCardDebited;
    }

    @Override
    public String toString() {
        return "CardPaymentCharge{" +
                "isCardDebited=" + wasCardDebited +
                '}';
    }
}
