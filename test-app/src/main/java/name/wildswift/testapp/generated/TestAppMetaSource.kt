package name.wildswift.testapp.generated

import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

class TestAppMetaSource: ViewContentMetaSource<DiContext, TestAppState<out ViewSet, DiContext>> {
    override fun getObjectsForState(state: TestAppState<out ViewSet, DiContext>?): MutableSet<ViewContentMeta> {
        TODO("Not yet implemented")
    }
}