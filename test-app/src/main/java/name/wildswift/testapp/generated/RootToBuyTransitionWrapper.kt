package name.wildswift.testapp.generated

import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.RootToBuyTransition
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

class RootToBuyTransitionWrapper(from: PrimaryStateWrapper, to: BuyStep1StateWrapper): StateTransition<TestAppEvent, ViewCouple<RootView, WalletsView>, ViewCouple<RootView, BuyCurrencyStep1View>, DiContext>(from, to) {
    private val wrapped = RootToBuyTransition(from.wrapped, to.wrapped)
    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: FrameLayout?, inViews: ViewCouple<RootView, WalletsView>?, callback: TransitionCallback<ViewCouple<RootView, BuyCurrencyStep1View>>) {
        wrapped.execute(context, rootView, inViews, callback)
    }
}