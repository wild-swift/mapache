package name.wildswift.testapp.views

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.LinearLayout
import name.wildswift.android.kannotations.*
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames

@ViewWithDelegate(
        parent = LinearLayout::class
)
@Fields(
        ViewField(name = "cryptoCurAmount", type = Float::class),
        ViewField(name = "countryCurAmount", type = Float::class),
        ViewField(name = "ticker", type = String::class),
        ViewField(name = "cryptoName", type = String::class),
        ViewField(name = "iconRef", type = Int::class),
        ViewField(name = "coinColor", type = Int::class),

        ViewField(name = "childCardModel", byDelegate = CryptoCardViewDelegate::class, childName = IdRNames.vbcs1Card, rwType = ReadWriteMode.Private),
        ViewField(name = "proceedButtonBg", byProperty = ViewProperty.backgroundDrawable, childName = IdRNames.vbcs1Proceed, rwType = ReadWriteMode.Private)
)
@Events(
        ViewEvent(name = "proceed", childName = IdRNames.vbcs1Proceed, listener = ViewListener.onClick)
)
class BuyCurrencyStep1ViewDelegate(view: BuyCurrencyStep1View) : ViewDelegate<BuyCurrencyStep1View, BuyCurrencyStep1ViewIntState>(view) {
    override fun setupView() {
        view.orientation = LinearLayout.VERTICAL
    }

    override fun validateStateForNewInput(data: BuyCurrencyStep1ViewIntState): BuyCurrencyStep1ViewIntState {
        return data.copy(
                childCardModel = CryptoCardViewModel(
                    cryptoCurAmount = data.cryptoCurAmount,
                    countryCurAmount = data.countryCurAmount,
                    ticker = data.ticker,
                    cryptoName = "",
                    iconRef = data.iconRef,
                    coinColor = data.coinColor,
                    hideButtons = true
                ),
                proceedButtonBg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    colors = intArrayOf(data.coinColor, data.coinColor)
                    cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, view.resources.displayMetrics)
                }
            )
    }
}