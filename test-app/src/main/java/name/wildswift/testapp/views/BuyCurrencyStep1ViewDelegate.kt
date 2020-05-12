package name.wildswift.testapp.views

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import android.util.TypedValue
import android.widget.LinearLayout
import name.wildswift.android.kannotations.*
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames
import name.wildswift.testapp.R

@ViewWithDelegate(
        parent = LinearLayout::class
)
@Fields(
        ViewField(name = "cryptoCurAmount", type = Float::class),
        ViewField(name = "countryCurAmount", type = Float::class),
        ViewField(name = "ticker", type = String::class),
        ViewField(name = "iconRef", type = Int::class),
        ViewField(name = "coinColor", type = Int::class),
        ViewField(name = "paymentType", type = Int::class),

        ViewField(name = "childCardModel", byDelegate = CryptoCardViewDelegate::class, childName = IdRNames.vbcs1Card, rwType = ReadWriteMode.Private),
        ViewField(name = "proceedButtonBg", byProperty = ViewProperty.backgroundDrawable, childName = IdRNames.vbcs1Proceed, rwType = ReadWriteMode.Private),
        ViewField(name = "proceedButtonText", byProperty = ViewProperty.text, childName = IdRNames.vbcs1Proceed, rwType = ReadWriteMode.Private),
        ViewField(name = "creditBg", byProperty = ViewProperty.backgroundDrawable, childName = IdRNames.vbcs1PaymentCredit, rwType = ReadWriteMode.Private),
        ViewField(name = "creditSelected", type = Boolean::class, childPropertyName = "isSelected", childName = IdRNames.vbcs1PaymentCredit, rwType = ReadWriteMode.Private),
        ViewField(name = "creditElevation", type = Float::class, childPropertyName = "elevation", childName = IdRNames.vbcs1PaymentCredit, rwType = ReadWriteMode.Private),
        ViewField(name = "bankBg", byProperty = ViewProperty.backgroundDrawable, childName = IdRNames.vbcs1PaymentBank, rwType = ReadWriteMode.Private),
        ViewField(name = "bankSelected", type = Boolean::class, childPropertyName = "isSelected", childName = IdRNames.vbcs1PaymentBank, rwType = ReadWriteMode.Private),
        ViewField(name = "bankElevation", type = Float::class, childPropertyName = "elevation", childName = IdRNames.vbcs1PaymentBank, rwType = ReadWriteMode.Private)
)
@Events(
        ViewEvent(name = "proceed", childName = IdRNames.vbcs1Proceed, listener = ViewListener.onClick),
        ViewEvent(name = "selectCredit", childName = IdRNames.vbcs1PaymentCredit, listener = ViewListener.onClick),
        ViewEvent(name = "selectBank", childName = IdRNames.vbcs1PaymentBank, listener = ViewListener.onClick)

)
class BuyCurrencyStep1ViewDelegate(view: BuyCurrencyStep1View) : ViewDelegate<BuyCurrencyStep1View, BuyCurrencyStep1ViewIntState>(view) {
    private var lastCoinColor = 0

    override fun setupView() {
        view.orientation = LinearLayout.VERTICAL
        view.elevation
    }

    override fun validateStateForNewInput(data: BuyCurrencyStep1ViewIntState): BuyCurrencyStep1ViewIntState {
        return data
                .copy(
                        childCardModel = CryptoCardViewModel(
                                cryptoCurAmount = data.cryptoCurAmount,
                                countryCurAmount = data.countryCurAmount,
                                ticker = data.ticker,
                                cryptoName = "",
                                iconRef = data.iconRef,
                                coinColor = data.coinColor,
                                hideButtons = true
                        ),
                        proceedButtonText = context.getString(if (data.paymentType == 0) R.string.continue_with_credit_card else R.string.continue_with_bank_account),
                        creditSelected = data.paymentType == 0,
                        bankSelected = data.paymentType == 1,
                        creditElevation = if (data.paymentType == 0) 0f else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, view.resources.displayMetrics),
                        bankElevation = if (data.paymentType == 1) 0f else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, view.resources.displayMetrics)
                )
                .let {
                    if (lastCoinColor != it.coinColor) {
                        lastCoinColor = it.coinColor
                        it.copy(
                                proceedButtonBg = GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    setColor(data.coinColor)
                                    cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, view.resources.displayMetrics)
                                },
                                creditBg = StateListDrawable().apply {
                                    addState(intArrayOf(android.R.attr.state_selected), GradientDrawable().apply {
                                        shape = GradientDrawable.RECTANGLE
                                        setColor(0xFFFFFFFF.toInt())
                                        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics)
                                        setStroke(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, view.resources.displayMetrics).toInt(), data.coinColor)
                                    })
                                    addState(StateSet.WILD_CARD, GradientDrawable().apply {
                                        shape = GradientDrawable.RECTANGLE
                                        setColor(0xFFFFFFFF.toInt())
                                        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics)
                                    })
                                },
                                bankBg = StateListDrawable().apply {
                                    addState(intArrayOf(android.R.attr.state_selected), GradientDrawable().apply {
                                        shape = GradientDrawable.RECTANGLE
                                        setColor(0xFFFFFFFF.toInt())
                                        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics)
                                        setStroke(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, view.resources.displayMetrics).toInt(), data.coinColor)
                                    })
                                    addState(StateSet.WILD_CARD, GradientDrawable().apply {
                                        shape = GradientDrawable.RECTANGLE
                                        setColor(0xFFFFFFFF.toInt())
                                        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, view.resources.displayMetrics)
                                    })
                                }

                        )
                    } else {
                        it
                    }
                }
    }
}