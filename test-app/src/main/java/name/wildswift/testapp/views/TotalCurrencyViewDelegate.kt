package name.wildswift.testapp.views

import android.widget.FrameLayout
import name.wildswift.android.kannotations.ViewField
import name.wildswift.android.kannotations.ViewProperty
import name.wildswift.android.kannotations.ViewWithDelegate
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames

@ViewWithDelegate(parent = FrameLayout::class)
@ViewField(name = "balance", byProperty = ViewProperty.text, childName = IdRNames.vtcBalance)
class TotalCurrencyViewDelegate(view: TotalCurrencyView) : ViewDelegate<TotalCurrencyView, TotalCurrencyViewIntState>(view)