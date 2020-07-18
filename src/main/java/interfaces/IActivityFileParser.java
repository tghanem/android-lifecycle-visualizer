package interfaces;

import impl.model.dstl.LifecycleAwareComponent;

import java.util.Optional;

public interface IActivityFileParser {
    Optional<LifecycleAwareComponent> parse(
            String activityCode,
            String activityName) throws Exception;
}
