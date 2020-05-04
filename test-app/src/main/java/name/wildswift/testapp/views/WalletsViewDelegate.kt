package name.wildswift.testapp.views

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_wallets.view.*
import name.wildswift.android.kannotations.*
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames

@ViewWithDelegate(
        parent = LinearLayout::class,
        saveInstanceState = false
)
@Fields(
        ViewField(name = "total", type = Float::class),
        ViewField(name = "totalSumTitle", childName = IdRNames.vwTotal, type = String::class, childPropertyName = "balance", rwType = ReadWriteMode.Private)
)
@CollectionViewField(name = "wallets", childName = IdRNames.vwList, byDelegate = CryptoCardViewDelegate::class,
        elementEvents = [
            ListEvent(name = "onItemBuyClick", listenerName = "buyClick"),
            ListEvent(name = "onItemSellClick", listenerName = "sellClick")
        ]
)
class WalletsViewDelegate(view: WalletsView) : ViewDelegate<WalletsView, WalletsViewIntState>(view) {

    override fun setupView() {
        view.vwList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position != 0) outRect.top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30.0f, context.resources.displayMetrics).toInt()
            }
        })
    }

    override fun validateStateForNewInput(data: WalletsViewIntState): WalletsViewIntState {
        return data.copy(totalSumTitle = String.format("%.2f", data.total))
    }
}