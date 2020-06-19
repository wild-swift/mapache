package name.wildswift.testapp.navigation

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

class RootToBuyTransition(from: PrimaryState, to: BuyStep1State) :
        StateTransition<TestAppEvent, ViewCouple<RootView, WalletsView>, ViewCouple<RootView, BuyCurrencyStep1View>, ViewGroup, DiContext>(from, to) {
    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: ViewGroup, inViews: ViewCouple<RootView, WalletsView>, callback: TransitionCallback<ViewCouple<RootView, BuyCurrencyStep1View>>) {
        val (root, _) = inViews;
        root.getContentView().removeAllViews()
        val buyCurrencyStep1View = BuyCurrencyStep1View(root.context)
        root.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        callback.onTransitionEnded(ViewSet.from(root, buyCurrencyStep1View))
    }
}