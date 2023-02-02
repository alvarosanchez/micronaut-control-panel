package io.micronaut.controlpanel.core;

import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * TODO: add javadoc.
 *
 * @author Álvaro Sánchez-Mariscal
 * @since 1.0.0
 */
@Singleton
public class HealthControlPanel implements ControlPanel {

    private final HealthEndpoint endpoint;

    public HealthControlPanel(HealthEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getTitle() {
        return "Application health";
    }

    @Override
    public Map<String, Object> getModel() {
        HealthResult healthResult = Mono.from(endpoint.getHealth(null)).block();
        return Map.of(
            "healthResult", healthResult
        );
    }

    @Override
    public Category getCategory() {
        return Category.MAIN;
    }

    @Override
    public String getName() {
        return "health";
    }
}
