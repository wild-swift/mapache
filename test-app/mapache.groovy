basePackageName "name.wildswift.testapp.generated"
statesPackageName ".gen"

actions {
    Test()
    BuyCrypto(tiker: String)
    SellCrypto(tiker: String)
    ProceedBuy(tiker: String, amount: float, paymentType: int)
}