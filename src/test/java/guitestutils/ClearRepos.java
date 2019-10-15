package guitestutils;

import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.*;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

/**
 * Classes can be annotated with the {@link ClearRepos} annotation.
 * It indicates, that <i>before</i> each test execution, the repositories and nextID counters from
 * bePREPARED should be reset.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestExecutionListeners(
        listeners = CustomTestExecutionListener.class,
        mergeMode = MERGE_WITH_DEFAULTS)
public @interface ClearRepos {
}