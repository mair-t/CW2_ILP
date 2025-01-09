package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class CreditCardInformation {

    public String creditCardNumber;

    public String creditCardExpiry;

    public String cvv;

    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    public String getCvv() {
        return cvv;
    }
    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    public void setCreditCardExpiry(String creditCardExpiry) {
        this.creditCardExpiry = creditCardExpiry;
    }
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
