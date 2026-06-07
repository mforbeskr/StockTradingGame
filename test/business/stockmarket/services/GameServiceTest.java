package business.stockmarket.services;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

  @Test
  public void startNewGame_ShouldInvokeClearAllPhysicalDataOnUnitOfWork() {
    // 1. ARRANGE (Opsætning af mock og service)
    MockUnitOfWork mockUow = new MockUnitOfWork();

    // Vi tilføjer noget dummy-data til vores mock for at se, om det bliver slettet
    mockUow.getStocks().add(new domain.Stock("OLD", "Old Stock", 10.0, domain.State.STEADY));

    // Initialiser din GameService med din MockUnitOfWork og dummy/mock DAO'er
    // (Erstat med dine rigtige DAO-instanser eller mocks, hvis jeres constructor fejler uden)
    GameService gameService = new GameService(
        mockUow,
        new business.stockmarket.services.MockPortfolioDAO(mockUow),
        new business.stockmarket.services.MockStockDAO(mockUow),
        new business.stockmarket.services.MockOwnedStockDAO(mockUow),
        new business.stockmarket.services.MockTransactionDAO(mockUow)
    );

    // Bekræft udgangspunktet: clearCount skal være 0, og der er data i mocken
    assertEquals(0, mockUow.clearCount);
    assertFalse(mockUow.getStocks().isEmpty());

    // 2. ACT (Udfør handlingen)
    UUID newPortfolioId = gameService.startNewGame();

    // 3. ASSERT (Verificer forventet adfærd)

    // A: Verificer, at clearAllPhysicalData blev kaldt nøjagtigt 1 gang!
    assertEquals(1, mockUow.clearCount, "clearAllPhysicalData() skulle være kaldt én gang");

    // B: Verificer, at transaktionsflowet blev fuldført korrekt (1 commit, 0 rollbacks)
    assertEquals(1, mockUow.commitCount, "Transaktionen skulle være committet");
    assertEquals(0, mockUow.rollbackCount, "Der skulle ikke være sket rollback");

    // C: Verificer, at der blev returneret et gyldigt UUID til det nye spil
    assertNotNull(newPortfolioId, "startNewGame() skal returnere et gyldigt UUID for den nye portfolio");

    // D: Verificer, at de nye standard-aktier nu ligger i mock'en i stedet for den gamle "OLD" aktie
    boolean harApple = mockUow.getStocks().stream().anyMatch(s -> s.getSymbol().equals("AAPL"));
    assertTrue(harApple, "MockUnitOfWork burde nu indeholde de nyligt oprettede standardaktier (f.eks. AAPL)");
  }
}