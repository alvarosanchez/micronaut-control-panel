package io.micronaut.controlpanel.ui;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HumanizeHelper;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.MethodHelper;
import com.github.jknack.handlebars.helper.StringHelpers;
import io.micronaut.controlpanel.core.ControlPanel;
import io.micronaut.controlpanel.core.ControlPanelRepository;
import io.micronaut.core.reflect.ReflectionUtils;
import io.micronaut.core.version.VersionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;
import jakarta.annotation.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: add javadoc.
 *
 * @author Álvaro Sánchez-Mariscal
 * @since 1.0.0
 */
@Controller("/control-panel")
public class HomeController {

    private Handlebars handlebars;
    private ControlPanelRepository repository;

    public HomeController(Handlebars handlebars, ControlPanelRepository repository) {
        this.handlebars = handlebars;
        this.repository = repository;

        //TODO move this to a better place
        HumanizeHelper.register(handlebars);
        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelpers(StringHelpers.class);
        handlebars.registerHelper("percentage", (ctx, opts) -> {
            Double value = ((Long) ctx).doubleValue();
            Double total = ((Long) opts.param(0)).doubleValue();
            return (int) Math.ceil((value / total) * 100);
        });
        handlebars.registerHelper("minus", (ctx, opts) -> {
            int a = (int) ctx;
            int b = opts.param(0);
            return a - b;
        });
        handlebars.registerHelper("mod", (ctx, opts) -> {
            int a = (int) ctx + 1;
            int b = opts.param(0);
            int c = opts.param(1);

            if ((a % b) == c) {
                return opts.fn();
            } else {
                return opts.inverse();
            }
        });
        handlebars.registerHelper("size", (ctx, opts) -> ((Collection<?>) ctx).size());
//        handlebars.registerHelper("invoke", (ctx, opts) -> {
//            Class<?> clazz = ctx.getClass();
//            String methodName = opts.param(0);
//            Optional<Method> method = ReflectionUtils.getMethod(clazz, methodName);
//            if (method.isPresent()) {
//                return ReflectionUtils.invokeMethod(ctx, method.get());
//            } else {
//                return opts.inverse();
//            }
//        });
    }

    @View("layout")
    @Get
    public HttpResponse index() {
        return byCategory(ControlPanel.Category.MAIN.id());
    }

    @View("layout")
    @Get("/categories/{categoryId}")
    public HttpResponse byCategory(String categoryId) {
        List<ControlPanel.Category> categories = repository.findAllCategories();
        ControlPanel.Category currentCategory = repository.findCategoryById(categoryId);
        List<ControlPanel> controlPanels = repository.findAllByCategory(categoryId);
        return HttpResponse.ok(Map.of(
            "micronautVersion", VersionUtils.getMicronautVersion(),
            "controlPanels", controlPanels,
            "categories", categories,
            "currentCategory", currentCategory,
            "contentView", "index"
        ));

    }

    @View("layout")
    @Get("/{controlPanelName}")
    public HttpResponse detail(String controlPanelName) {
        List<ControlPanel.Category> categories = repository.findAllCategories();
        ControlPanel controlPanel = repository.findByName(controlPanelName);
        ControlPanel.Category currentCategory = repository.findCategoryById(controlPanel.getCategory().id());
        return HttpResponse.ok(Map.of(
            "micronautVersion", VersionUtils.getMicronautVersion(),
            "controlPanel", controlPanel,
            "categories", categories,
            "currentCategory", currentCategory,
            "contentView", "detail"
        ));
    }
}