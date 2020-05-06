package name.wildswift.testapp.generated

import android.util.Pair
import name.wildswift.mapache.graph.NavigationGraph
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

class TestAppNavigationGraph: NavigationGraph<TestAppEvent, DiContext, TestAppState<ViewSet, DiContext>> {
    override fun getNextState(currentState: TestAppState<ViewSet, DiContext>?, e: TestAppEvent?): Pair<TestAppState<ViewSet, DiContext>, StateTransition<TestAppEvent, ViewSet, ViewSet>> {
        TODO("Not yet implemented")
    }
}