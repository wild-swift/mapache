package name.wildswift.testapp.generated

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.osintegration.SystemCalls
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.mapache.viewsets.ViewSingle
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.BuyStep1State
import name.wildswift.testapp.navigation.PrimaryState
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

sealed class TestAppState<VS: ViewSet, DC>: MState<TestAppEvent, VS, DC>

object PrimaryStateWrapper: TestAppState<ViewSet, DiContext>() {
    val wrapped = PrimaryState()

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, WalletsView> {
        return wrapped.setup(rootView, context)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewSet): Runnable {
        return wrapped.dataBind(context, views as ViewCouple<RootView, WalletsView>)
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return wrapped.start(context)
    }
}

class BuyStep1StateWrapper(val tiker:String): TestAppState<ViewSet, DiContext>() {
    val wrapped = BuyStep1State(tiker)

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, BuyCurrencyStep1View> {
        return wrapped.setup(rootView, context)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewSet): Runnable {
        return wrapped.dataBind(context, views as ViewCouple<RootView, BuyCurrencyStep1View>)
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return wrapped.start(context)
    }

}