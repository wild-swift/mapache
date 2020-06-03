package name.wildswift.testapp.generated

import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

class TestAppMetaSource: ViewContentMetaSource<TestAppState<ViewSet, DiContext>> {
    override fun getObjectsForState(state: TestAppState<ViewSet, DiContext>): MutableSet<ViewContentMeta> {
        TODO("Not yet implemented")
    }
}