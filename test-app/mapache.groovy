import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.BuyStep1State
import name.wildswift.testapp.navigation.BuyToReviewTransition
import name.wildswift.testapp.navigation.BuyToRootTransition
import name.wildswift.testapp.navigation.PrimaryState
import name.wildswift.testapp.navigation.ReviewBuyState
import name.wildswift.testapp.navigation.ReviewToPrimaryTransition
import name.wildswift.testapp.navigation.RootToBuyTransition

basePackageName "name.wildswift.testapp.generated"
statesPackageName ".gen"

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
    }

    $(BuyStep1State) {
        addToBackStack false

        when ProceedBuy go ReviewBuyState with BuyToReviewTransition
        go PrimaryState with BuyToRootTransition
    }

    $(ReviewBuyState) {
        go PrimaryState with ReviewToPrimaryTransition
    }
}