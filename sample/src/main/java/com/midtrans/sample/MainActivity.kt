package com.midtrans.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.midtrans.sdk.corekit.MidtransSdk
import com.midtrans.sdk.corekit.base.callback.MidtransCallback
import com.midtrans.sdk.corekit.base.enums.*
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.CheckoutTransaction
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.optional.Item
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.optional.customer.Address
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.optional.customer.CustomerDetails
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.request.specific.creditcard.CreditCard
import com.midtrans.sdk.corekit.core.api.merchant.model.checkout.response.CheckoutWithTransactionResponse
import com.midtrans.sdk.corekit.core.api.midtrans.model.registration.CreditCardTokenizeResponse
import com.midtrans.sdk.corekit.core.api.snap.model.paymentinfo.PaymentInfoResponse
import com.midtrans.sdk.corekit.core.payment.CreditCardCharge
import com.midtrans.sdk.corekit.utilities.Logger
import com.midtrans.sdk.uikit.MidtransKit
import com.midtrans.sdk.uikit.MidtransKitConfig
import com.midtrans.sdk.uikit.base.callback.PaymentResult
import com.midtrans.sdk.uikit.base.callback.Result
import com.midtrans.sdk.uikit.base.model.PaymentResponse
import com.midtrans.sdk.uikit.base.theme.ColorTheme
import com.midtrans.sdk.uikit.base.theme.CustomColorTheme
import com.midtrans.sdk.uikit.utilities.Helper
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text_view_test.setOnClickListener {
            initMidtransKit()
            MidtransKit 
                .getInstance()
                .startPaymentUiFlow(
                    this,
                    initCheckout(),
                    object : PaymentResult {
                        override fun onPaymentFinished(result: Result?, response: PaymentResponse?) {
                            Logger.debug("RESULT IS >>> ${result?.paymentType} AND ${result?.paymentStatus}")
                            val showToast = "${response?.orderId
                                ?: "No Order ID"} is ${result?.paymentType
                                ?: "Cancel Payment"} and status is ${result?.paymentStatus
                                ?: "CANCEL"}"
                            Logger.debug("RESULT >>> $showToast")
                            Toast.makeText(this@MainActivity, showToast, Toast.LENGTH_LONG).show()
                        }

                        override fun onFailed(throwable: Throwable?) {
                            Logger.debug("ERROR IS >>> ${throwable?.message}")
                        }
                    }
                )
        }
    }

    private fun initCheckout(): CheckoutTransaction {
        val installment = HashMap<String, MutableList<Int>>()
        installment[BankType.MANDIRI] = mutableListOf(3, 6, 12)
        val bin = edit_text_bin.text.toString().trim()
        return CheckoutTransaction
            .builder(
                getRandomString(5),
                20000.0
            )
            .setUserId("pahlevi@yopmail.com")
            .setCreditCard(
                CreditCard
                    .builder()
                    .setAcquiringBank(AcquiringBankType.BNI)
                    .setInstallment(radio_installment_mandatory.isChecked, installment)
                    .setBlackListBins(
                        if (radio_blacklist.isChecked && bin.isNotEmpty()) {
                            mutableListOf(bin)
                        } else {
                            mutableListOf()
                        }
                    )
                    .setWhiteListBins(
                        if (radio_whitelist.isChecked && bin.isNotEmpty()) {
                            mutableListOf(bin)
                        } else {
                            mutableListOf()
                        }
                    )
                    .setSaveCard(check_box_saved_card.isChecked)
                    .setAuthentication(
                        when {
                            radio_auth_3ds.isChecked -> Authentication.AUTH_3DS
                            radio_auth_rba.isChecked -> Authentication.AUTH_RBA
                            else -> Authentication.AUTH_NONE
                        }
                    )
                    .setType(
                        if (check_box_authorize_payment.isChecked) {
                            CreditCardTransactionType.AUTHORIZE
                        } else {
                            CreditCardTransactionType.AUTHORIZE_CAPTURE
                        }
                    )
                    .build())
            .setCustomerDetails(
                CustomerDetails
                    .builder()
                    .setFirstName("Budi")
                    .setLastName("Utomo")
                    .setEmail("midtrans.android2@yopmail.com")
                    .setPhone("08123456789")
                    .setBillingAddress(
                        Address
                            .builder()
                            .setFirstName("FirstName")
                            .setLastName("LastName")
                            .setAddress("address")
                            .setCity("City")
                            .setPostalCode("12345")
                            .setPhone("08123456789")
                            .build()
                    )
                    .setShippingAddress(
                        Address
                            .builder()
                            .setFirstName("FirstName")
                            .setLastName("LastName")
                            .setAddress("address")
                            .setCity("City")
                            .setPostalCode("12345")
                            .setPhone("08123456789")
                            .build()
                    )
                    .build()
            )
            .setCheckoutItems(mutableListOf(Item("1", 20000.0, 1, "sabun")))
            .build()
    }

    private fun initMidtransKit() {
        // 3DS 1Click key >>> VT-client-F91kdUrnE5w8zCja and set custom field = one_click

        MidtransKit
            .builder(
                this,
                if (radio_two_click.isChecked) {
                    "VT-client-E4f1bsi1LpL1p5cF"
                } else {
                    BuildConfig.CLIENT_KEY
                },
                if (radio_two_click.isChecked) {
                    "https://rakawm-snap.herokuapp.com/"
                } else {
                    BuildConfig.BASE_URL
                }
            )
            .setEnvironment(
                if (BuildConfig.FLAVOR == "development") {
                    Environment.SANDBOX
                } else {
                    Environment.PRODUCTION
                }
            )
            .setApiRequestTimeOut(60)
            .setLogEnabled(true)
            .setBuiltinStorageEnabled(true)
            .setMidtransKitConfig(
                MidtransKitConfig
                    .builder()
                    .setShowEmailInCcForm(check_box_email.isChecked)
                    .setColorTheme(
                        when {
                            radio_custom_theme_input.isChecked -> CustomColorTheme("#008577", "#00574B", "#D81B60")
                            radio_custom_theme_local.isChecked -> ColorTheme(this, ColorTheme.CORAL)
                            else -> null
                        }
                    )
                    .build()
            )
            .build()
    }

    private fun checkout(checkoutTransaction: CheckoutTransaction) {
        MidtransSdk.getInstance().checkoutWithTransaction(checkoutTransaction,
            object : MidtransCallback<CheckoutWithTransactionResponse> {
                override fun onSuccess(data: CheckoutWithTransactionResponse) {
                    Logger.debug("Success return snapToken ${data.token}")
                }

                override fun onFailed(throwable: Throwable) {
                    Logger.debug("Failed return error >>> ${throwable.message}")
                }
            })
    }

    fun getRandomString(length: Int): String {
        val formatter = SimpleDateFormat("mmss")
        val date = Date()
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        val alpha = (1..length)
            .map { allowedChars.random() }
            .joinToString("")
        val randomNumber = (Math.random() * 5250 + 152).toInt()
        return "SAM-$alpha${Helper.generateRandomNumber()}$randomNumber${formatter.format(date)}"
    }

    private fun getTransactionOptions(snapToken: String) {
        MidtransSdk.getInstance().getPaymentInfo(snapToken, object : MidtransCallback<PaymentInfoResponse> {
            override fun onSuccess(data: PaymentInfoResponse) {
                //startPayment(snapToken);
                Logger.debug("RESULT SUCCESS PAYMENT INFO " + data.token)
            }

            override fun onFailed(throwable: Throwable) {
                Logger.debug("MIDTRANS SDK NEW RETURN ERROR >>> " + throwable.message)

            }
        })
    }

    private fun registerCard() {
        CreditCardCharge.tokenizeCard(
            "4105058689481467",
            "123",
            "12",
            "2019",
            object : MidtransCallback<CreditCardTokenizeResponse> {
                override fun onSuccess(data: CreditCardTokenizeResponse) {

                }

                override fun onFailed(throwable: Throwable) {

                }
            }
        )
    }

}