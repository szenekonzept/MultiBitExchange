package org.multibit.exchange.infrastructure.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Locale;
import org.multibit.exchange.domain.event.MarketEventTopic;
import org.multibit.exchange.infrastructure.adaptor.marketapi.MultiBitExchangeConfiguration;
import org.multibit.exchange.infrastructure.adaptor.persistence.mongo.MongoCollections;
import org.multibit.exchange.infrastructure.adaptor.persistence.mongo.MongoMarketReadModelBuilder;
import org.multibit.exchange.infrastructure.adaptor.persistence.mongo.MongoMarketReadService;
import org.multibit.exchange.infrastructure.guice.annotation.DefaultLocale;
import org.multibit.exchange.infrastructure.service.DefaultMarketService;
import org.multibit.exchange.readmodel.MarketReadModelBuilder;
import org.multibit.exchange.service.DefaultTradingEngineService;
import org.multibit.exchange.service.MarketReadService;
import org.multibit.exchange.service.MarketService;
import org.multibit.exchange.service.TradingEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Guice module to provide the following to application:</p>
 * <ul>
 * <li>Bindings between injected interfaces and their implementations</li>
 * </ul>
 *
 * @since 0.0.1
 *         
 */
public class MultiBitExchangeServiceModule extends AbstractModule {

  private static final Logger log = LoggerFactory.getLogger(MultiBitExchangeServiceModule.class);

  /**
   * The default locale. This ServiceModule is setup with bindings to
   * inject this wherever the annotation @DefaultLocale is used.
   *
   * @see org.multibit.exchange.infrastructure.web.BaseResource
   */
  public static final Locale DEFAULT_LOCALE = Locale.CANADA;

  private final MultiBitExchangeConfiguration configuration;

  public MultiBitExchangeServiceModule(MultiBitExchangeConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {

    // Application support
    bind(MarketService.class).to(DefaultMarketService.class).asEagerSingleton();

    // Read Services
    bind(MarketReadService.class).to(MongoMarketReadService.class).asEagerSingleton();

    // Read Model Builders
    bind(MarketReadModelBuilder.class).to(MongoMarketReadModelBuilder.class).asEagerSingleton();

    // Default Locale
    bind(Locale.class).annotatedWith(DefaultLocale.class).toInstance(DEFAULT_LOCALE);

    // MarketEvent Topic
    bind(MarketEventTopic.class).toProvider(DisruptorMarketEventTopicProvider.class);

    // Trading Engine
    bind(TradingEngineService.class).to(DefaultTradingEngineService.class).asEagerSingleton();

  }

  @Provides
  @Singleton
  public Mongo getMongo() {
    return getDB().getMongo();
  }

  @Provides
  @Singleton
  public DB getDB() {
    // MongoDB Setup
    String mongoUriString = configuration.getMongoUri();
    if (mongoUriString == null) {
      // todo - is there a better way to do this?
      try {
        return getSandboxDB();
      } catch (IOException e) {
        throw new RuntimeException("Unable to spin up a mongo sandbox.", e);
      }
    }

    final MongoURI mongoUri = new MongoURI(mongoUriString);
    final DB db;
    try {
      db = mongoUri.connectDB();
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Cannot connect to MongoDB", e);
    }

    // Authenticate
    if (mongoUri.getUsername() != null && mongoUri.getPassword() != null) {
      db.authenticate(mongoUri.getUsername(), mongoUri.getPassword());
    } else {
      // Check that a collection can be reached anonymously instead
      db.getCollection(MongoCollections.MARKET_READ_COLLECTION).count();
    }
    return db;
  }

  private DB getSandboxDB() throws IOException {
    MongodForTestsFactory factory = new MongodForTestsFactory();
    Mongo mongo = factory.newMongo();
    return factory.newDB(mongo);
  }

}
