package name.wildswift.testapp.generated

import android.util.Pair
import name.wildswift.mapache.graph.NavigationGraphOld
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionFactory
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

class TestAppTransitionFactory: TransitionFactory<TestAppEvent, DiContext, TestAppState<ViewSet, DiContext>> {

    override fun getTransition(from: TestAppState<ViewSet, DiContext>, to: TestAppState<ViewSet, DiContext>): StateTransition<TestAppEvent, *, *, DiContext>? {
        return when(from) {
            is PrimaryStateWrapper ->
                when(to) {
                    is BuyStep1StateWrapper -> RootToBuyTransitionWrapper(from, to)
                    else -> return null
                }
            is BuyStep1StateWrapper ->
                when(to) {
                is ReviewBuyStateWrapper -> BuyToReviewTransitionWrapper(from, to)
                else -> null
            }

            is ReviewBuyStateWrapper -> TODO()
        }
    }
}