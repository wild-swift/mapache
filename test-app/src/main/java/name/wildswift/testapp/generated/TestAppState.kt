package name.wildswift.testapp.generated

import android.widget.FrameLayout
import name.wildswift.mapache.NavigationContext
import name.wildswift.mapache.osintegration.SystemCalls
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.mapache.viewsets.ViewSingle
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.PrimaryState
import name.wildswift.testapp.views.WalletsView

sealed class TestAppState<VS: ViewSet, DC>: MState<TestAppEvent, VS, DC>

object PrimaryStateWrapper: TestAppState<ViewSet, DiContext>() {
    private val wrapped = PrimaryState()

    override fun setup(rootView: FrameLayout, context: NavigationContext<TestAppEvent, DiContext>): ViewSingle<WalletsView> {
        return wrapped.setup(rootView, context)
    }

    override fun dataBind(context: NavigationContext<TestAppEvent, DiContext>, views: ViewSet): Runnable {
        return wrapped.dataBind(context, views as ViewSingle<WalletsView>)
    }

    override fun start(context: NavigationContext<TestAppEvent, DiContext>, caller: SystemCalls): Runnable {
        return wrapped.start(context, caller)
    }

}