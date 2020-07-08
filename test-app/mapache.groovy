import android.view.ViewGroup
import name.wildswift.testapp.contents.WalletsViewViewContent
import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.states.BuyStep1State
import name.wildswift.testapp.navigation.transitions.BuyToReviewTransition
import name.wildswift.testapp.navigation.transitions.BuyToRootTransition
import name.wildswift.testapp.navigation.states.PrimaryState
import name.wildswift.testapp.navigation.states.ReviewBuyState
import name.wildswift.testapp.navigation.transitions.ReviewToPrimaryTransition
import name.wildswift.testapp.navigation.transitions.RootToBuyTransition

basePackageName "name.wildswift.testapp.generated"
statesPackageName ".states"

dependencySource = DiContext

actions {
    Test()
    BuyCrypto(tiker: String)
    SellCrypto(tiker: String)
    ProceedBuy(tiker: String, amount: float, paymentType: int)
    TestPrimitive(pBoolean: boolean, pFloat: float, pInt: int, pByte: byte, pShort: short, pLong: long, pChar: char, pDouble: double)
}

layer {
    from(PrimaryState) {
        when BuyCrypto go BuyStep1State with RootToBuyTransition

        content WalletsViewViewContent
    }

    $(BuyStep1State) {
        addToBackStack true

        when ProceedBuy go ReviewBuyState with BuyToReviewTransition
        go PrimaryState with BuyToRootTransition

//        rootView 0
//
//        from(PrimaryState) {
//            content WalletsViewViewContent
//        }
    }

    $(ReviewBuyState) {
        go PrimaryState with ReviewToPrimaryTransition
    }
}