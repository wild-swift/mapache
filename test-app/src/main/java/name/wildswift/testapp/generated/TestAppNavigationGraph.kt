package name.wildswift.testapp.generated

import android.util.Pair
import name.wildswift.mapache.graph.NavigationGraph
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.RootToBuyTransition

class TestAppNavigationGraph: NavigationGraph<TestAppEvent, DiContext, TestAppState<ViewSet, DiContext>> {
    override fun getNextState(currentState: TestAppState<ViewSet, DiContext>, e: TestAppEvent): Pair<TestAppState<ViewSet, DiContext>, StateTransition<TestAppEvent, ViewSet, ViewSet, DiContext>>? {
        return when(currentState) {
            PrimaryStateWrapper -> {
                when(e) {
                    is BuyCrypto -> {
                        val buyStep1StateWrapper = BuyStep1StateWrapper(e.tiker)
                        return Pair(buyStep1StateWrapper as TestAppState<ViewSet, DiContext>, RootToBuyTransitionWrapper(currentState as PrimaryStateWrapper, buyStep1StateWrapper) as StateTransition<TestAppEvent, ViewSet, ViewSet, DiContext>)
                    }
//                    is SellCrypto -> return Pair()
                    else -> return null
                }
            }
            is BuyStep1StateWrapper -> TODO()
        }
    }
}