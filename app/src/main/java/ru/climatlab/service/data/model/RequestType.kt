package ru.climatlab.service.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */
enum class RequestType(val title: String) {
    @SerializedName("0")
    CommissioningWorks("ПНР"),
    @SerializedName("1")
    CommissioningWorksAndContract("ПНР+Договор"),
    @SerializedName("2")
    WarrantyRepair("ГАРАНТИЙНЫЙ ремонт"),
    @SerializedName("3")
    MaintenancePaid("ТО платное"),
    @SerializedName("4")
    MaintenanceContract("ТО по договору"),
    @SerializedName("5")
    FreeWorkUnderTheContract("Бесплатный выезд по договору"),
    @SerializedName("6")
    PaidWorkUnderTheContract("Платный выезд по договору"),
    @SerializedName("7")
    PaidWork("Разовый выезд Платный"),
    @SerializedName("8")
    RepeatedWork("Повторный выезд бесплатный"),
    @SerializedName("9")
    ConclusionOfAnAgreement("Заключение договора"),
    @SerializedName("10")
    Mounting("Монтаж"),
    @SerializedName("11")
    CancelRequest("ОТМЕНА ЗАЯВКИ"),
}