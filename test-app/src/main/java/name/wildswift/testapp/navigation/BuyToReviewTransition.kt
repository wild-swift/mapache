package name.wildswift.testapp.navigation

import android.view.ViewGroup
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.ReviewBuyView
import name.wildswift.testapp.views.RootView

class BuyToReviewTransition(from: BuyStep1State, to: ReviewBuyState) :
        StateTransition<TestAppEvent, ViewCouple<RootView, BuyCurrencyStep1View>, ViewCouple<RootView, ReviewBuyView>, ViewGroup, DiContext>(from, to) {
    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: ViewGroup, inViews: ViewCouple<RootView, BuyCurrencyStep1View>, callback: TransitionCallback<ViewCouple<RootView, ReviewBuyView>>) {
        val (root, _) = inViews;
        root.getContentView().removeAllViews()
        val buyCurrencyStep1View = ReviewBuyView(root.context)
        root.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        callback.onTransitionEnded(ViewSet.from(root, buyCurrencyStep1View))
    }
}