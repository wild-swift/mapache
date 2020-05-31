package name.wildswift.testapp.generated

import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.graph.StateTransition
import name.wildswift.mapache.graph.TransitionCallback
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.BuyToReviewTransition
import name.wildswift.testapp.navigation.RootToBuyTransition
import name.wildswift.testapp.views.BuyCurrencyStep1View
import name.wildswift.testapp.views.ReviewBuyView
import name.wildswift.testapp.views.RootView
import name.wildswift.testapp.views.WalletsView

class BuyToReviewTransitionWrapper(from: BuyStep1StateWrapper, to: ReviewBuyStateWrapper): StateTransition<TestAppEvent, ViewCouple<RootView, BuyCurrencyStep1View>, ViewCouple<RootView, ReviewBuyView>, DiContext>(from, to) {
    private val wrapped = BuyToReviewTransition(from.wrapped, to.wrapped)
    override fun execute(context: NavigationContext<TestAppEvent, DiContext>, rootView: FrameLayout, inViews: ViewCouple<RootView, BuyCurrencyStep1View>, callback: TransitionCallback<ViewCouple<RootView, ReviewBuyView>>) {
        wrapped.execute(context, rootView, inViews, callback)
    }
}