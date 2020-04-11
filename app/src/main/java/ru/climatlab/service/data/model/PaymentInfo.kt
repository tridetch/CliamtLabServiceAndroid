package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

data class PaymentInfo(
    @SerializedName("TransactionId") val transactionId: String? = null,
    @SerializedName("Created") val created: Long? = null,
    @SerializedName("CreatedDT") val createdDT: String? = null,
    @SerializedName("ClientID") val clientID: Int? = null,
    @SerializedName("BranchID") val branchID: Int? = null,
    @SerializedName("PosID") val posID: Int? = null,
    @SerializedName("Invoice") val invoice: String? = null,
    @SerializedName("Amount") val amount: Double? = null,
    @SerializedName("PaymentType") val paymentType: String? = null,
    @SerializedName("Description") val description: String? = null,
    @SerializedName("ExtID") val extID: String? = null,
    @SerializedName("ReceiptPhone") val receiptPhone: String? = null,
    @SerializedName("ReceiptEmail") val receiptEmail: String? = null,
    @SerializedName("ExternalPayment") val externalPayment: String? = null,
    @SerializedName("IIN") val iin: String? = null,
    @SerializedName("RRN") val rrn: String? = null,
    @SerializedName("ApprovalCode") val approvalCode: String? = null,
    @SerializedName("TerminalID") val terminalID: String? = null,
    @SerializedName("AcquirerTranId") val acquirerTranId: String? = null,
    @SerializedName("PAN") val pan: String? = null,
    @SerializedName("FiscalPrinterSN") val fiscalPrinterSN: String? = null,
    @SerializedName("FiscalShift") val fiscalShift: String? = null,
    @SerializedName("FiscalCryptoVerifCode") val fiscalCryptoVerifCode: String? = null,
    @SerializedName("FiscalDocSN") val fiscalDocSN: String? = null,
    @SerializedName("FiscalPrinterRegnum") val fiscalPrinterRegnum: String? = null,
    @SerializedName("FiscalDocumentNumber") val fiscalDocumentNumber: String? = null,
    @SerializedName("FiscalStorageNumber") val fiscalStorageNumber: String? = null,
    @SerializedName("FiscalMark") val fiscalMark: String? = null,
    @SerializedName("FiscalDatetime") val fiscalDatetime: String? = null
)