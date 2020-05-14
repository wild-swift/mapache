package name.wildswift.testapp.navigation

import android.view.ViewGroup
import name.wildswift.android.kannotations.interfaces.ObservableListAdapter
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.R
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.BuyCrypto
import name.wildswift.testapp.generated.SellCrypto
import name.wildswift.testapp.generated.TestAppEvent
import name.wildswift.testapp.views.*

class PrimaryState: MState<TestAppEvent, ViewCouple<RootView, WalletsView>, DiContext> {

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, WalletsView> {
        val appRootView = RootView(context.diContext.context)
        rootView.addView(appRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val walletsView = WalletsView(context.diContext.context)
        appRootView.getContentView().addView(walletsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return ViewSet.from(appRootView).union(walletsView)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewCouple<RootView, WalletsView>): Runnable {
        val (root, walletsView) = views
        root.viewModel = RootViewModel(" ", false)
        val total = context.diContext.curencies.map { it.currencyAmount * it.currencyRate }.sum()
        walletsView.viewModel = WalletsViewModel(
                total,
                ObservableListAdapter(
                        context.diContext
                                .curencies
                                .map {
                                    CryptoCardViewModel(
                                            it.currencyAmount,
                                            it.currencyAmount * it.currencyRate,
                                            it.tiker,
                                            it.name,
                                            it.icon,
                                            it.color,
                                            false
                                    )
                                }
                )
        )
        walletsView.onItemBuyClick = {
            context.eventer.onNewEvent(BuyCrypto(it.ticker))
        }
        walletsView.onItemSellClick = {
            context.eventer.onNewEvent(SellCrypto(it.ticker))
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