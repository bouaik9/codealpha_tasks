workspace {
  model {
    user = person "User"

    marketDataProvider = softwareSystem "Market Data Provider" "Simulated external price source"

    stockTradingPlatform = softwareSystem "Stock Trading Platform" {
      api = container "Trading API / Application" "Java 17+, Maven application" "Java"
      domain = container "Trading Domain" "Domain model, business rules, services" "Java"
      db = container "PostgreSQL Database" "User, account, portfolio, order, and transaction data" "Relational database"
      marketAdapter = container "Market Data Adapter" "PriceProvider implementation and fallback logic" "Java"

      api -> domain "Uses services and domain model"
      domain -> db "Persists and reads account and trading data"
      domain -> marketAdapter "Requests current stock prices"
      marketAdapter -> marketDataProvider "Gets market quotes"
      user -> api "Registers, deposits, buys, sells"
    }

    accountManagement = component "Account Management" "Validates account lifecycle and cash rules" "Account Management" within api
    trading = component "Trading" "Coordinates buy/sell workflows" "Trading" within api
    portfolio = component "Portfolio" "Calculates portfolio value and P/L" "Portfolio" within api
    orderManagement = component "Order Management" "Persists and tracks orders" "Order Management" within api
    transactionManagement = component "Transaction Management" "Records executed financial events" "Transaction Management" within api
    marketData = component "Market Data" "Provides price abstraction and fake data" "Market Data" within api

    user -> accountManagement "Create account / deposit / withdraw"
    user -> trading "Buy and sell securities"
    user -> portfolio "View portfolio value"

    accountManagement -> domain "Invokes account operations"
    trading -> domain "Invokes trade workflow"
    portfolio -> domain "Uses holdings and pricing logic"
    orderManagement -> domain "Creates and reads orders"
    transactionManagement -> domain "Creates and reads transactions"
    marketData -> domain "Returns current prices"

    domain -> db "Stores records"
    marketData -> marketDataProvider "Reads simulated market prices"
  }

  views {
    systemContext stockTradingPlatform "SystemContext" {
      include *
    }

    container stockTradingPlatform "Containers" {
      include *
    }

    component api "ComponentView" {
      include *
    }
  }
}
