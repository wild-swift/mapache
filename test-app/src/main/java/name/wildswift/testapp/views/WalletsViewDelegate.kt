package name.wildswift.testapp.views

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_wallets.view.*
import name.wildswift.android.kannotations.Fields
import name.wildswift.android.kannotations.ListViewField
import name.wildswift.android.kannotations.ViewField
import name.wildswift.android.kannotations.ViewWithDelegate
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames
import name.wildswift.testapp.WalletsList

@ViewWithDelegate(
        parent = LinearLayout::class,
        saveInstanceState = false
)
@Fields(
        ViewField(name = "total", type = Float::class),

        ViewField(name = "totalSumTitle", childName = IdRNames.vwTotal, type = String::class, childPropertyName = "balance", publicAccess = false)
)
@ListViewField(name = "wallets", childListView = IdRNames.vwList, delegateClass = CryptoCardViewDelegate::class)
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