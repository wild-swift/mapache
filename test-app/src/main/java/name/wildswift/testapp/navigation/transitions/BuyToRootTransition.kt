package name.wildswift.testapp.navigation.transitions

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.navigation.states.BuyStep1State
import name.wildswift.testapp.navigation.states.PrimaryState
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

class BuyToRootTransition(from: BuyStep1State, to: PrimaryState) :
        StateTransition<TestAppEvent, ViewCouple<RootView, BuyCurrencyStep1View>, ViewCouple<RootView, WalletsView>, FrameLayout, DiContext>(from, to) {
    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: FrameLayout, inViews: ViewCouple<RootView, BuyCurrencyStep1View>, callback: TransitionCallback<ViewCouple<RootView, WalletsView>>) {
        val (root, _) = inViews;
        root.getContentView().removeAllViews()
        val buyCurrencyStep1View = WalletsView(root.context)
        context.viewsContents.getByView(WalletsView::class.java)?.fillCurrentData(buyCurrencyStep1View)
        root.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        callback.onTransitionEnded(ViewSet.from(root, buyCurrencyStep1View))
    }
}