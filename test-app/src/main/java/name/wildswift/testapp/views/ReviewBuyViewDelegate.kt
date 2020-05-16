package name.wildswift.testapp.views

import android.graphics.drawable.GradientDrawable
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
        ViewField(name = "paymentTypeIcon", byProperty = ViewProperty.imageResource, childName = IdRNames.vrbPaymentTypeIcon),
        ViewField(name = "paymentTypeName", byProperty = ViewProperty.text, childName = IdRNames.vrbPaymentTypeName),
        ViewField(name = "paymentTypeIdName", byProperty = ViewProperty.text, childName = IdRNames.vrbPaymentTypeId),
        ViewField(name = "cryptoName", type = String::class),
        ViewField(name = "cryptoTiker", type = String::class),
        ViewField(name = "cryptoIcon", byProperty = ViewProperty.imageResource, childName = IdRNames.vrbCryptoIcon),
        ViewField(name = "cryptoColor", byProperty = ViewProperty.backgroundColor),
        ViewField(name = "moneyAmount", type = Float::class),
        ViewField(name = "cryptoAmount", type = Float::class),

        ViewField(name = "moneyAmountText", byProperty = ViewProperty.text, childName = IdRNames.vrbMoneyAmount, rwType = ReadWriteMode.Private),
        ViewField(name = "cryptoAmountText", byProperty = ViewProperty.text, childName = IdRNames.vrbCryptoAmount, rwType = ReadWriteMode.Private),
        ViewField(name = "buyButtonName", byProperty = ViewProperty.text, childName = IdRNames.vrbProceed, rwType = ReadWriteMode.Private),
        ViewField(name = "arriveText", byProperty = ViewProperty.text, childName = IdRNames.vrbArriveText, rwType = ReadWriteMode.Private),
        ViewField(name = "buyButtonBg", byProperty = ViewProperty.backgroundDrawable, childName = IdRNames.vrbProceed, rwType = ReadWriteMode.Private)

)
class ReviewBuyViewDelegate(view: ReviewBuyView) : ViewDelegate<ReviewBuyView, ReviewBuyViewIntState>(view) {
    private var lastCoinColor: Int = 0

    override fun validateStateForNewInput(data: ReviewBuyViewIntState): ReviewBuyViewIntState {
        return data
                .copy(
                        moneyAmountText = String.format("%.2f", data.moneyAmount),
                        cryptoAmountText = String.format("%s %.6f", data.cryptoTiker, data.cryptoAmount),
                        buyButtonName = "Buy " + data.cryptoName,
                        arriveText = context.resources.getString(R.string.arrive_text_pattern, data.cryptoName)
                )
                .let {
                    if (lastCoinColor != it.cryptoColor) {
                        lastCoinColor = it.cryptoColor
                        it.copy(
                                buyButtonBg = GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    setColor(data.cryptoColor)
                                    cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, view.resources.displayMetrics)
                                }
                        )
                    } else {
                        it
                    }
                }
    }
}