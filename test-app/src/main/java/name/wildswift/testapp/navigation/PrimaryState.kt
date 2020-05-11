package name.wildswift.testapp.navigation

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.android.kannotations.interfaces.ObservableListAdapter
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.osintegration.SystemCalls
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.mapache.viewsets.ViewSingle
import name.wildswift.testapp.R
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.BuyCrypto
import name.wildswift.testapp.generated.SellCrypto
import name.wildswift.testapp.generated.TestAppEvent
import name.wildswift.testapp.views.CryptoCardViewModel
import name.wildswift.testapp.views.WalletsView
import name.wildswift.testapp.views.WalletsViewModel

class PrimaryState: MState<TestAppEvent, ViewSingle<WalletsView>, DiContext> {

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewSingle<WalletsView> {
        val walletsView = WalletsView(context.diContext!!.context)
        rootView.addView(walletsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return ViewSet.from(walletsView)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewSingle<WalletsView>): Runnable {
        val (walletsView) = views
        walletsView.viewModel = WalletsViewModel(
                1537.46f,
                ObservableListAdapter(
                        listOf(
                                CryptoCardViewModel(
                                        0.1195656f,
                                        729.5f,
                                        "ZTC",
                                        "Zitcoin",
                                        R.drawable.ic_ztc_icon,
                                        0xFFFF7141.toInt(),
                                        true
                                ),
                                CryptoCardViewModel(
                                        2.1195632f,
                                        807.96f,
                                        "ATH",
                                        "Atherium",
                                        R.drawable.ic_ath_icon,
                                        0xFF4B70FF.toInt(),
                                        false
                                )
                        )
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