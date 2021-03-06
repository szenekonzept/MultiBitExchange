package org.multibit.exchange.readmodel;

import org.multibit.exchange.domain.event.MarketEventSubscriber;

/**
 * <p>ReadModelBuilder to provide the following to the application:</p>
 * <ul>
 * <li>The single writer of the market read model</li>
 * <li>On-the-fly maintenance of the market read model</li>
 * </ul>
 * <p>Example:</p>
 * <pre>
 * </pre>
 *
 * @since 0.0.1
 *         
 */
public interface MarketReadModelBuilder extends MarketEventSubscriber {
}
