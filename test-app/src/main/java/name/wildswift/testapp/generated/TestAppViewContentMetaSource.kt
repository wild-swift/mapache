package name.wildswift.testapp.generated

import android.view.View
import android.view.ViewGroup
import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.testapp.contents.WalletsViewViewContent
import name.wildswift.testapp.generated.states.PrimaryStateWrapper
import name.wildswift.testapp.generated.states.TestAppMState
import name.wildswift.testapp.views.WalletsView

class TestAppViewContentMetaSource : ViewContentMetaSource<TestAppMState<ViewGroup, *>> {
    override fun getObjectsForState(state: TestAppMState<ViewGroup, *>): Set<ViewContentMeta<*, *>> {
        if (state is PrimaryStateWrapper) {
            return setOf(ViewContentMeta(WalletsView::class.java, WalletsViewViewContent::class.java, null, false))
        }
        return emptySet()
    }
}