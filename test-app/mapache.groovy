import name.wildswift.testapp.di.DiContext

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

