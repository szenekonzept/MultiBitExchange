package org.multibit.exchange.domain.event;

import org.multibit.exchange.domain.market.Market;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>Event to provide the following to the application.</p>
 * <ul>
 * <li>Notification of changes relating to {@link Market}s</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public abstract class MarketEvent extends Event {
  protected final Market market;

  public MarketEvent(Market market) {
    checkNotNull(market, "market cannot be null");

    this.market = market;
  }

  public Market getMarket() {
    return market;
  }

  public abstract void onEvent(MarketEventContext context);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MarketEvent that = (MarketEvent) o;

    if (!market.equals(that.market)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return market.hashCode();
  }

  @Override
  public String toString() {
    return "MarketEvent{" +
        "market=" + market +
        '}';
  }
}
