package name.wildswift.testapp.di

import android.content.Context
import name.wildswift.testapp.R

class DiContext(val context: Context) {
    val curencies: List<CurrencyData> = listOf(
            CurrencyData(
                    0.1195656f,
                    6101.25f,
                    "ZTC",
                    "Zitcoin",
                    R.drawable.ic_ztc_icon,
                    0xFFFF7141.toInt()
            ),
            CurrencyData(
                    2.1195632f,
                    381.19f,
                    "ATH",
                    "Atherium",
                    R.drawable.ic_ath_icon,
                    0xFF4B70FF.toInt()
            )
    )
}