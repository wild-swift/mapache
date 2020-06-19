package name.wildswift.testapp.generated

//class TestAppTransitionFactory: TransitionFactory<TestAppEvent, DiContext, TestAppState<ViewSet, DiContext>> {
//
//    override fun getTransition(from: TestAppState<ViewSet, DiContext>, to: TestAppState<ViewSet, DiContext>): StateTransition<TestAppEvent, *, *, DiContext>? {
//        return when(from) {
//            is PrimaryStateWrapper ->
//                when(to) {
//                    is BuyStep1StateWrapper -> RootToBuyTransitionWrapper(from, to)
//                    else -> return null
//                }
//            is BuyStep1StateWrapper ->
//                when(to) {
//                is ReviewBuyStateWrapper -> BuyToReviewTransitionWrapper(from, to)
//                else -> null
//            }
//
//            is ReviewBuyStateWrapper -> TODO()
//        }
//    }
//}