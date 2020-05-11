package name.wildswift.testapp.views

import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_root.view.*
import name.wildswift.android.kannotations.Delegated
import name.wildswift.android.kannotations.ViewField
import name.wildswift.android.kannotations.ViewWithDelegate
import name.wildswift.android.kannotations.interfaces.ViewDelegate

@ViewWithDelegate(
        parent = LinearLayout::class
)
@ViewField(name = "title", type = String::class)
class RootViewDelegate(view: RootView) : ViewDelegate<RootView, RootViewIntState>(view) {
    override fun setupView() {
        view.orientation = LinearLayout.VERTICAL
    }

    @Delegated
    fun getContentView():FrameLayout {
        return view.vrContent
    }
}