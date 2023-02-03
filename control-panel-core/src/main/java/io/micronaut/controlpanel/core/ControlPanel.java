package io.micronaut.controlpanel.core;

import io.micronaut.core.naming.Named;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.util.Toggleable;

import java.util.Map;

import static io.micronaut.core.util.StringUtils.EMPTY_STRING;

/**
 * TODO: add javadoc.
 *
 * @author Álvaro Sánchez-Mariscal
 * @since 1.0.0
 */
public interface ControlPanel extends Named, Ordered, Toggleable {

    String getTitle();


    Map<String, Object> getModel();

    default View getBody() {
        return new View("/views/" + getName() + "/body");
    }
    default View getDetailedView() {
        return new View("/views/" + getName() + "/detail");
    }

    default String getBadge() {
        return EMPTY_STRING;
    }

    Category getCategory();

    record Category (String id, String name, String iconClass, Integer order) {
        public static final Category MAIN = new Category("main", "Dashboard", "fa-tachometer-alt", Integer.MIN_VALUE);

        public Category(String id, String name, String iconClass) {
            this(id, name, iconClass, 0);
        }
    }

    record View (String file) {
        public static final View CARD = new View("/views/card");
    }

}