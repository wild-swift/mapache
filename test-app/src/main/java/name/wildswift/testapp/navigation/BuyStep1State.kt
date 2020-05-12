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

class BuyStep1State: MState<TestAppEvent, ViewCouple<RootView, BuyCurrencyStep1View>, DiContext> {

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, BuyCurrencyStep1View> {
        val appRootView = RootView(context.diContext!!.context)
        rootView.addView(appRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val buyCurrencyStep1View = BuyCurrencyStep1View(context.diContext!!.context)
        appRootView.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return ViewSet.from(appRootView).union(buyCurrencyStep1View)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewCouple<RootView, BuyCurrencyStep1View>): Runnable {
        val (_, buyCurrencyStep1View) = views
        buyCurrencyStep1View.viewModel = BuyCurrencyStep1ViewModel(
                0.1195656f,
                729.5f,
                "ZTC",
                R.drawable.ic_ztc_icon,
                0xFFFF7141.toInt(),
//                2.1195632f,
//                807.96f,
//                "ATH",
//                R.drawable.ic_ath_icon,
//                0xFF4B70FF.toInt()
                0
        )
        buyCurrencyStep1View.selectCredit = {
            buyCurrencyStep1View.viewModel = buyCurrencyStep1View.viewModel.copy(paymentType = 0)
        }
        buyCurrencyStep1View.selectBank = {
            buyCurrencyStep1View.viewModel = buyCurrencyStep1View.viewModel.copy(paymentType = 1)
        }
        return Runnable {
        }
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return Runnable {  }
    }
}