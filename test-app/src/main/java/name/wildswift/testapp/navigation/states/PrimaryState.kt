package name.wildswift.testapp.navigation.states

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.android.kannotations.interfaces.ObservableListAdapter
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.BuyCrypto
import name.wildswift.testapp.generated.events.SellCrypto
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.views.*

class PrimaryState: MState<TestAppEvent, ViewCouple<RootView, WalletsView>, FrameLayout, DiContext> {

    override fun setup(rootView: FrameLayout, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, WalletsView> {
        val appRootView = RootView(context.diContext.context)
        rootView.addView(appRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val walletsView = WalletsView(context.diContext.context)
        appRootView.getContentView().addView(walletsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val viewContentHolder = context.viewsContents.getByView(WalletsView::class.java)
        viewContentHolder?.fillCurrentData(walletsView)

        return ViewSet.from(appRootView).union(walletsView)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewCouple<RootView, WalletsView>): Runnable {
        val (root, walletsView) = views
        root.viewModel = RootViewModel(" ", false)
        walletsView.onItemBuyClick = {
            context.eventer.onNewEvent(BuyCrypto.newInstance(it.ticker))
        }
        walletsView.onItemSellClick = {
            context.eventer.onNewEvent(SellCrypto.newInstance(it.ticker))
        }
        return Runnable {
            walletsView.onItemBuyClick = null
            walletsView.onItemSellClick = null
        }
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return Runnable {  }
    }
}