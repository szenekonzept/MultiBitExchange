package org.multibit.exchange.infrastructure.adaptor.marketapi.resources;

import com.google.common.collect.Lists;
import com.yammer.dropwizard.testing.ResourceTest;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.multibit.exchange.readmodel.MarketListReadModel;
import org.multibit.exchange.readmodel.MarketReadModel;
import org.multibit.exchange.service.MarketReadService;
import org.multibit.exchange.service.MarketService;


import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MarketResourceIntegrationTest extends ResourceTest {

  private final MarketService marketService = mock(MarketService.class);
  private final MarketReadService marketReadService = mock(MarketReadService.class);

  @Override
  protected void setUpResources() throws Exception {
    addResource(new MarketResource(marketService, marketReadService));
  }

  @After
  public void tearDown() {
    reset(marketReadService);
    reset(marketService);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void GET_markets() {
    // Arrange
    final List<MarketReadModel> expectedMarkets = Lists.newArrayList();
    expectedMarkets.add(new MarketReadModel(ObjectId.get().toString(), "symbol", "itemSymbol", "currencySymbol"));
    when(marketReadService.fetchMarkets()).thenReturn(expectedMarkets);

    // Act
    MarketListReadModel actualReadModel = client().resource("/markets").get(MarketListReadModel.class);

    // Assert
    assertThat(actualReadModel.getMarkets()).isEqualTo(expectedMarkets);
    verify(marketReadService, times(1)).fetchMarkets();
  }

  @Test
  public void POST_market() {
    // Arrange
    final String marketSymbol = "marketSymbol";
    final String itemSymbol = "itemSymbol";
    final String currencySymbol = "currencySymbol";
    MarketDescriptor marketDescriptor = new MarketDescriptor(marketSymbol, itemSymbol, currencySymbol);

    // Act
    client().resource("/markets").type(MediaType.APPLICATION_JSON).post(marketDescriptor);

    // Assert
    verify(marketService, times(1)).addMarket(marketSymbol, itemSymbol, currencySymbol);
  }
}
