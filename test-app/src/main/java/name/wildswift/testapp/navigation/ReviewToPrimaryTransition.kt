package name.wildswift.testapp.navigation

import android.view.ViewGroup
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.views.ReviewBuyView
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

class ReviewToPrimaryTransition(from: ReviewBuyState, to: PrimaryState) :
        StateTransition<TestAppEvent, ViewCouple<RootView, ReviewBuyView>, ViewCouple<RootView, WalletsView>, ViewGroup, DiContext>(from, to) {

    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: ViewGroup, inViews: ViewCouple<RootView, ReviewBuyView>, callback: TransitionCallback<ViewCouple<RootView, WalletsView>>) {
        val (root, _) = inViews;
        root.getContentView().removeAllViews()
        val walletsView = WalletsView(root.context)
        root.getContentView().addView(walletsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        callback.onTransitionEnded(ViewSet.from(root, walletsView))
    }
}