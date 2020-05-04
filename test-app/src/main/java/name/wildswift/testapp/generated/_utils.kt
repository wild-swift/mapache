package name.wildswift.testapp.generated

import name.wildswift.mapache.NavigationStateMachine
import name.wildswift.mapache.states.MState
import name.wildswift.mapache.viewcontent.ViewContentMeta
import name.wildswift.mapache.viewcontent.ViewContentMetaSource
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

typealias TestAppNavigationStateMachine = NavigationStateMachine<TestAppEvent, TestAppState<out ViewSet, DiContext>>

fun newNavigationStateMachine(diContext: DiContext): TestAppNavigationStateMachine = TestAppNavigationStateMachine(PrimaryStateWrapper, TestAppNavigationGraph(), TestAppSystemEventFactory(), TestAppMetaSource())