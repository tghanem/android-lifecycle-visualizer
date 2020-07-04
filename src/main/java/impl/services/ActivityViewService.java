package impl.services;

import interfaces.IActivityViewProvider;
import interfaces.IActivityViewService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActivityViewService implements IActivityViewService {
    private final List<IActivityViewProvider> providers;

    public ActivityViewService() {
        providers = new ArrayList<>();
    }

    @Override
    public void registerViewProvider(IActivityViewProvider provider) {
        providers.add(provider);
    }

    @Override
    public Collection<IActivityViewProvider> getViewProviders() {
        return providers;
    }
}
