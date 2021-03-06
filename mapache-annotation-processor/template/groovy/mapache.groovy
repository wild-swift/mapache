import name.wildswift.testapp.di.DiContext
import name.wildswift.testapp.navigation.*

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
        when ProceedBuy go ReviewBuyState with BuyToReviewTransition
    }

    $(ReviewBuyState) {

    }
}