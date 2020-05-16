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

class ReviewBuyState(val tiker:String, val amount: Float, val paymentType: Int): MState<TestAppEvent, ViewCouple<RootView, ReviewBuyView>, DiContext> {

    override fun setup(rootView: ViewGroup, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, ReviewBuyView> {
        val appRootView = RootView(context.diContext.context)
        rootView.addView(appRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val buyCurrencyStep1View = ReviewBuyView(context.diContext.context)
        appRootView.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return ViewSet.from(appRootView).union(buyCurrencyStep1View)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewCouple<RootView, ReviewBuyView>): Runnable {
        val (rootView, buyCurrencyStep1View) = views
        val meta = context.diContext.curencies.find { it.tiker == tiker }!!
        rootView.viewModel = RootViewModel("Review", true)
        buyCurrencyStep1View.viewModel = ReviewBuyViewModel(
                paymentTypeIcon = if (paymentType == 0) R.drawable.ic_credit_card else R.drawable.ic_bank,
                paymentTypeName = context.diContext.context.resources.getString(if(paymentType == 0) R.string.credit_card else R.string.bank_account),
                paymentTypeIdName = context.diContext.context.resources.getString(if(paymentType == 0) R.string.card_ending_in else R.string.account_ending_in),
                cryptoName = meta.name,
                cryptoTiker = meta.tiker,
                cryptoIcon = meta.icon,
                cryptoColor = meta.color,
                moneyAmount = amount,
                cryptoAmount = amount / meta.currencyRate
        )
        rootView.upClick = {

        }
        return Runnable {
            rootView.upClick = null
        }
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return Runnable {  }
    }
}