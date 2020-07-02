package name.wildswift.testapp.contents

import name.wildswift.android.kannotations.interfaces.ObservableListAdapter
import name.wildswift.mapache.viewcontent.Initializable
import name.wildswift.mapache.viewcontent.ViewContent
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.views.CryptoCardViewModel
import name.wildswift.testapp.views.WalletsView
import name.wildswift.testapp.views.WalletsViewModel

class WalletsViewViewContent: ViewContent<WalletsView>, Initializable<DiContext> {
    private lateinit var walletsViewModel : WalletsViewModel

    override fun fillCurrentData(view: WalletsView) {
        view.viewModel = walletsViewModel
    }

    override fun subscribeForUpdates(view: WalletsView): Runnable {
        return Runnable {

        }
    }

    override fun init(context: DiContext) {
        val total = context.curencies.map { it.currencyAmount * it.currencyRate }.sum()
        walletsViewModel = WalletsViewModel(
                total,
                ObservableListAdapter(
                        context.curencies
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
    }

    override fun close() {
    }
}