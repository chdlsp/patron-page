package com.chdlsp.patronpage.exception;

import java.math.BigDecimal;

public class SupportAmountException extends RuntimeException{
    public SupportAmountException(BigDecimal supportAmount) {
        super("후원 금액은 1원 이상이어야 합니다. : " + supportAmount.toString());
    }
}
