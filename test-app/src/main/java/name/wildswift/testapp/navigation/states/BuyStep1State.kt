package name.wildswift.testapp.navigation.states

import android.view.ViewGroup
import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewCouple
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.generated.events.ProceedBuy
import name.wildswift.testapp.generated.events.TestAppEvent
import name.wildswift.testapp.views.*

class BuyStep1State(val tiker:String): MState<TestAppEvent, ViewCouple<RootView, BuyCurrencyStep1View>, FrameLayout, DiContext> {

    override fun setup(rootView: FrameLayout, context: NavigationContext<TestAppEvent, DiContext>): ViewCouple<RootView, BuyCurrencyStep1View> {
        val appRootView = RootView(context.diContext.context)
        rootView.addView(appRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val buyCurrencyStep1View = BuyCurrencyStep1View(context.diContext.context)
        appRootView.getContentView().addView(buyCurrencyStep1View, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return ViewSet.from(appRootView).union(buyCurrencyStep1View)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewCouple<RootView, BuyCurrencyStep1View>): Runnable {
        val (rootView, buyCurrencyStep1View) = views
        val meta = context.diContext.curencies.find { it.tiker == tiker }!!
        rootView.viewModel = RootViewModel("Buy ${meta.name}", true)
        buyCurrencyStep1View.viewModel = BuyCurrencyStep1ViewModel(
                meta.currencyAmount,
                meta.currencyRate * meta.currencyAmount,
                meta.tiker,
                meta.icon,
                meta.color,
                0
        )
        buyCurrencyStep1View.selectCredit = {
            buyCurrencyStep1View.viewModel = buyCurrencyStep1View.viewModel.copy(paymentType = 0)
        }
        buyCurrencyStep1View.selectBank = {
            buyCurrencyStep1View.viewModel = buyCurrencyStep1View.viewModel.copy(paymentType = 1)
        }
        buyCurrencyStep1View.proceed = {
            context.eventer.onNewEvent(ProceedBuy.newInstance(tiker, 50f, buyCurrencyStep1View.viewModel.paymentType))
        }
        rootView.upClick = {
            context.eventer.onBack()
        }
        return Runnable {
            buyCurrencyStep1View.selectCredit = null
            buyCurrencyStep1View.selectBank = null
            buyCurrencyStep1View.proceed = null
            rootView.upClick = null
        }
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>): Runnable {
        return Runnable {  }
    }
}