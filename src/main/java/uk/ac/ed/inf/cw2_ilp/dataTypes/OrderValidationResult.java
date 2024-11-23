package uk.ac.ed.inf.cw2_ilp.dataTypes;

public enum OrderValidationResult {

    UNDEFINED,

    NO_ERROR,

    CARD_NUMBER_INVALID,

    EXPIRY_DATE_INVALID,

    CVV_INVALID,

    TOTAL_INCORRECT,

    PIZZA_NOT_DEFINED,

    MAX_PIZZA_COUNT_EXCEEDED,

    PIZZA_FROM_MULTIPLE_RESTAURANTS,

    RESTAURANT_CLOSED
}
