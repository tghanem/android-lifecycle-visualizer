package interfaces;

import java.util.Collection;

public interface IActivityViewService {
    void registerViewProvider(IActivityViewProvider provider);

    Collection<IActivityViewProvider> getViewProviders();
}
