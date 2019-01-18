package io.github.ufukhalis.phoenix.data;

import io.vavr.API;
import io.vavr.Predicates;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import static io.vavr.API.*;

@Component
public class PhoenixConnectionPool {

    private final Logger logger = LoggerFactory.getLogger(PhoenixConnectionPool.class);

    @Autowired
    PhoenixDataConnection phoenixDataConnection;

    private int min;
    private int max;
    private int used = 0;

    private final Queue<Connection> CONNECTION_POOL = new ConcurrentLinkedQueue<>();

    private Predicate<Integer> checkConnectionSizePredicate = size -> size > 0;

    @PostConstruct
    private void initPool() {
        this.max = phoenixDataConnection.getMaxConnection().getOrElse(20);
        this.min = phoenixDataConnection.getMinConnection().getOrElse(5);

        final List<Connection> connections = io.vavr.collection.Queue.fill(min, () -> phoenixDataConnection.connect()).asJava();
        CONNECTION_POOL.addAll(connections);
    }

    public Connection getConnectionFromPool() {
        return Match(this.CONNECTION_POOL.size()).of(
                Case($(Predicates.allOf(checkConnectionSizePredicate)), () -> {
                    createNewConnection();
                    return CONNECTION_POOL.poll();}),
                API.Case($(), () -> {
                    if (used < max) {
                        used++;
                        return phoenixDataConnection.connect();
                    } else {
                        throw new RuntimeException("There is no available connection in the pool");
                    }
                })
        );
    }

    public void releaseConnection(Option<Connection> maybeConnection) {
        maybeConnection.map(connection -> Try.run(() -> {
            logger.debug("Releasing connection..");
            connection.close();
            used--;
        }).getOrElseThrow(e -> new RuntimeException("Connection couldn't release", e)));
    }

    private void createNewConnection() {
        Future.of(() -> phoenixDataConnection.connect()).onSuccess(CONNECTION_POOL::add);
    }
}
