package name.wildswift.testapp.views

import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_crypto_card.view.*
import name.wildswift.android.kannotations.*
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames

@ViewWithDelegate(
        parent = FrameLayout::class
)
@Fields(
        ViewField(name = "cryptoCurAmount", type = Float::class),
        ViewField(name = "countryCurAmount", type = Float::class),
        ViewField(name = "ticker", childName = IdRNames.vccTicker, property = ViewProperty.text),
        ViewField(name = "cryptoName", type = String::class),
        ViewField(name = "iconRef", type = Int::class, childName = IdRNames.vccCoinImage, childPropertySetter = "setImageResource"),
        ViewField(name = "coinColor", type = Int::class),

        ViewField(name = "buyButtonName", property = ViewProperty.text, childName = IdRNames.vccBuyButton, publicAccess = false),
        ViewField(name = "sellButtonName", property = ViewProperty.text, childName = IdRNames.vccSellButton, publicAccess = false),
        ViewField(name = "topRowColor", type = Int::class, childName = IdRNames.vccTopBorder, childPropertySetter = "setBackgroundColor", publicAccess = false),
        ViewField(name = "cryptoCurText", property = ViewProperty.text, childName = IdRNames.vccCryptoCurAmount, publicAccess = false),
        ViewField(name = "countryCurText", property = ViewProperty.text, childName = IdRNames.vccCountryCurAmount, publicAccess = false),
        ViewField(name = "sellTextColor", property = ViewProperty.textColor, childName = IdRNames.vccSellButton, publicAccess = false)
)
@Events(
        ViewEvent(name = "buyClick", childName = IdRNames.vccBuyButton, listener = ViewListener.onClick),
        ViewEvent(name = "sellClick", childName = IdRNames.vccSellButton, listener = ViewListener.onClick)
)
class CryptoCardViewDelegate(view: CryptoCardView) : ViewDelegate<CryptoCardView, CryptoCardViewIntState>(view) {

    override fun setupView() {
        super.setupView()
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, view.resources.displayMetrics))
            }
        }
        view.clipToOutline = true
        view.setBackgroundColor(0xFFFFFFFF.toInt())
        view.elevation = 5f
    }

    override fun onNewInternalState(data: CryptoCardViewIntState) {
        super.onNewInternalState(data)
        // TODO need add values
        view.vccSellButton.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, view.resources.displayMetrics)
            setStroke(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, view.resources.displayMetrics).toInt(), data.coinColor)
        }

        view.vccBuyButton.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            colors = intArrayOf(data.coinColor, data.coinColor)
            cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, view.resources.displayMetrics)
            setStroke(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, view.resources.displayMetrics).toInt(), data.coinColor)
        }
    }

    override fun validateStateForNewInput(data: CryptoCardViewIntState): CryptoCardViewIntState {
        return data.copy(
                buyButtonName = "Buy ${data.cryptoName}",
                sellButtonName = "Sell ${data.cryptoName}",
                topRowColor = data.coinColor,
                cryptoCurText = String.format("%.7f", data.cryptoCurAmount),
                countryCurText = String.format("£%.2f", data.countryCurAmount),
                sellTextColor = data.coinColor
        )
    }
}