package name.wildswift.testapp.generated

import name.wildswift.mapache.NavigationStateMachine
import name.wildswift.mapache.viewsets.ViewSet
import name.wildswift.testapp.di.DiContext

typealias TestAppNavigationStateMachine = NavigationStateMachine<TestAppEvent, DiContext, TestAppState<ViewSet, DiContext>>

fun newNavigationStateMachine(diContext: DiContext): TestAppNavigationStateMachine = TestAppNavigationStateMachine(PrimaryStateWrapper, TestAppTransitionFactory(), TestAppSystemEventFactory(), TestAppMetaSource(), diContext)