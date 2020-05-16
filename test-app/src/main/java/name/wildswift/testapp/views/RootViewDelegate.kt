package name.wildswift.testapp.views

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.view_root.view.*
import name.wildswift.android.kannotations.*
import name.wildswift.android.kannotations.interfaces.ViewDelegate
import name.wildswift.testapp.IdRNames

@ViewWithDelegate(
        parent = DrawerLayout::class
)
@Fields(
        ViewField(name = "title", byProperty = ViewProperty.text, childName = IdRNames.vrTitleText),
        ViewField(name = "showUp", type = Boolean::class),
        ViewField(name = "upVisibility", byProperty = ViewProperty.visibility, childName = IdRNames.vrUp, rwType = ReadWriteMode.Private)
)
@Events(
        ViewEvent(name = "upClick", childName = IdRNames.vrUp, listener = ViewListener.onClick)
)
class RootViewDelegate(view: RootView) : ViewDelegate<RootView, RootViewIntState>(view) {
    override fun setupView() {
        view.setBackgroundColor(Color.WHITE)
    }

    override fun validateStateForNewInput(data: RootViewIntState): RootViewIntState {
        return data.copy(
                upVisibility = if (data.showUp) View.VISIBLE else View.GONE
        )
    }

    @Delegated
    fun getContentView():FrameLayout {
        return view.vrContent
    }
}