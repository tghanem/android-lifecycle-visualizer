package impl.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import impl.Helper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private AppSettingsComponent settingsComponent;

    //@Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Activity Lifecycle Navigator Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsComponent = new AppSettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        if (!Helper.areEqual(settings.resourceAcquisitions, settingsComponent.getResourceAcquisitions())) {
            return true;
        }
        if (!Helper.areEqual(settings.resourceReleases, settingsComponent.getResourceReleases())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.resourceAcquisitions = settingsComponent.getResourceAcquisitions();
        settings.resourceReleases = settingsComponent.getResourceReleases();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settingsComponent.setResourceAcquisitions(settings.resourceAcquisitions);
        settingsComponent.setResourceReleases(settings.resourceReleases);
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}
