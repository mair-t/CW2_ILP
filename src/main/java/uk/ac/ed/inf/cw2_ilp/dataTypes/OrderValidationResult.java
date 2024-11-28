package uk.ac.ed.inf.cw2_ilp.dataTypes;

public enum OrderValidationResult {

    UNDEFINED,

    NO_ERROR,

    CARD_NUMBER_INVALID, //done

    EXPIRY_DATE_INVALID,

    CVV_INVALID, //done

    TOTAL_INCORRECT, //done

    PIZZA_NOT_DEFINED,

    MAX_PIZZA_COUNT_EXCEEDED, //done

    PIZZA_FROM_MULTIPLE_RESTAURANTS,

    RESTAURANT_CLOSED
}
