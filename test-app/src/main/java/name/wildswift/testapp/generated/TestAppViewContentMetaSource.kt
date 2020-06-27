package name.wildswift.testapp.generated

import android.view.ViewGroup
import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.testapp.generated.gen.TestAppMState

class TestAppViewContentMetaSource : ViewContentMetaSource<TestAppMState<ViewGroup, *>> {
    override fun getObjectsForState(state: TestAppMState<ViewGroup, *>): Set<ViewContentMeta> {
        return emptySet()
    }
}