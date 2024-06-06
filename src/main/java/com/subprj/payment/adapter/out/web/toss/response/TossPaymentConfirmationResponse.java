package com.subprj.payment.adapter.out.web.toss.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class TossPaymentConfirmationResponse {

    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method;
    private double totalAmount;
    private double balanceAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private boolean useEscrow;
    private String lastTransactionKey;
    private double suppliedAmount;
    private double vat;
    private boolean cultureExpense;
    private double taxFreeAmount;
    private int taxExemptionAmount;
    private List<Cancel> cancels;
    private boolean isPartialCancelable;
    private Card card;
    private VirtualAccount virtualAccount;
    private MobilePhone mobilePhone;
    private String settlementStatus;
    private Receipt receipt;
    private Checkout checkout;
    private EasyPay easyPay;
    private CashReceipt cashReceipt;
    private List<CashReceipt> cashReceipts;

    // Constructor, getters, and setters

    @NoArgsConstructor
    @Getter
    @ToString
    static class Cancel {
        private double cancelAmount;
        private String cancelReason;
        private double taxFreeAmount;
        private int taxExemptionAmount;
        private double refundableAmount;
        private double easyPayDiscountAmount;
        private Date canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;

        // Constructor, getters, and setters
    }

    static class Card {
        private double amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private int installmentPlanMonths;
        private String approveNo;
        private boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private boolean isInterestFree;
        private String interestPayer;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private Date dueDate;
        private String refundStatus;
        private RefundReceiveAccount refundReceiveAccount;
        private String secret;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class MobilePhone {
        private CustomerMobilePhone customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class CustomerMobilePhone {
        private String plain;
        private String masking;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class Receipt {
        private String url;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class Checkout {
        private String url;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class EasyPay {
        private String provider;
        private double amount;
        private double discountAmount;

        // Constructor, getters, and setters
    }

    @NoArgsConstructor
    @Getter
    @ToString
    static class CashReceipt {
        private String type;
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String businessNumber;
        private String transactionType;
        private double amount;
        private double taxFreeAmount;
        private String issueStatus;
        private String failure;
        private CustomerIdentityNumber customerIdentityNumber;
        private Date requestedAt;

        // Constructor, getters, and setters
    }

    static class CustomerIdentityNumber {
        private String value;

        // Constructor, getters, and setters
    }
}
