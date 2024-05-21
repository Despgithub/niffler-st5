package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.Selector.*;
import static guru.qa.niffler.model.UserJson.simpleUser;

// Любой тест проходит через него
public class UserQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static final Map<User.Selector, Queue<UserJson>> USERS = new ConcurrentHashMap<>();

    static {

        USERS.put(WITH_FRIENDS, new ConcurrentLinkedQueue<>(
                List.of(
                        simpleUser("barsik", "12345")
                ))
        );
        USERS.put(INVITATION_SEND, new ConcurrentLinkedQueue<>(
                List.of(
                        simpleUser("duck", "12345")
                ))
        );
        USERS.put(INVITATION_RECEIVED, new ConcurrentLinkedQueue<>(
                List.of(
                        simpleUser("dima", "12345")
                ))
        );
    }


    @Override
    public void beforeEach(ExtensionContext context) {
        Map<User.Selector, UserJson> users = new HashMap<>();
        List<Method> methods = new ArrayList<>();

        methods.add(context.getRequiredTestMethod());

        Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(BeforeEach.class))
                .forEach(methods::add);

        List<Parameter> parameters = methods.stream()
                .flatMap(p -> Arrays.stream(p.getParameters()))
                .filter((p -> p.isAnnotationPresent(User.class)))
                .toList();

        for (Parameter parameter : parameters) {
            User.Selector selector = parameter.getAnnotation(User.class).value();
            if (users.containsKey(selector)) {
                continue;
            }
            UserJson userForTest = null;
            Queue<UserJson> queue = USERS.get(selector);
            while (userForTest == null) {
                userForTest = queue.poll();
            }
            users.put(selector, userForTest);
        }
        Allure.getLifecycle().updateTestCase(testCase ->
                testCase.setStart(new Date().getTime()));
        context.getStore(NAMESPACE).put(context.getUniqueId(), users);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Map<User.Selector, UserJson> usersAfterTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (User.Selector user : usersAfterTest.keySet()) {
            USERS.get(user).add(usersAfterTest.get(user));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        User.Selector selector = parameterContext.getParameter().getAnnotation(User.class).value();
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.findAnnotation(User.class).get().value());
    }

}
