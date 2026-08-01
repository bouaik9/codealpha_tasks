workspace {

  model {
    user = person "User"

    marketDataProvider = softwareSystem "Market Data Provider" "Simulated external price source"

    stockTradingPlatform = softwareSystem "Stock Trading Platform" {

      api = container "Trading API / Application" "Java 17+, Maven application" "Java" {
        accountManagement = component "Account Management" "Validates account lifecycle and cash rules" "Java"
        trading = component "Trading" "Coordinates buy/sell workflows" "Java"
        portfolio = component "Portfolio" "Calculates portfolio value and P/L" "Java"
        orderManagement = component "Order Management" "Persists and tracks orders" "Java"
        transactionManagement = component "Transaction Management" "Records executed financial events" "Java"
        marketData = component "Market Data" "Provides price abstraction and fake data" "Java"
      }

      domain = container "Trading Domain" "Domain model, business rules, services" "Java"
      db = container "PostgreSQL Database" "User, account, portfolio, order, and transaction data" "Relational database"
      marketAdapter = container "Market Data Adapter" "PriceProvider implementation and fallback logic" "Java"

      api -> domain "Uses services and domain model"
      domain -> db "Persists and reads account and trading data"
      domain -> marketAdapter "Requests current stock prices"
      marketAdapter -> marketDataProvider "Gets market quotes"
      user -> api "Registers, deposits, buys, sells"
    }

    user -> accountManagement "Create account / deposit / withdraw"
    user -> trading "Buy and sell securities"
    user -> portfolio "View portfolio value"

    accountManagement -> domain "Invokes account operations"
    trading -> domain "Invokes trade workflow"
    portfolio -> domain "Uses holdings and pricing logic"
    orderManagement -> domain "Creates and reads orders"
    transactionManagement -> domain "Creates and reads transactions"
    marketData -> domain "Returns current prices"

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