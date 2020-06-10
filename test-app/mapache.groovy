basePackageName "name.wildswift.testapp.generated"
statesPackageName ".gen"

actions {
    Test()
    BuyCrypto(tiker: String)
    SellCrypto(tiker: String)
    ProceedBuy(tiker: String, amount: float, paymentType: int)
    TestPrimitive(pBoolean: boolean, pFloat: float, pInt: int, pByte: byte, pShort: short, pLong: long, pChar: char, pDouble: double)
}