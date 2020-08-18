package impl.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name="impl.settings.AppSettingsState",
        storages = {@Storage("ActivityLifecycleNavigator.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public List<String> resourceAcquisitions;
    public List<String> resourceReleases;

    public static AppSettingsState getInstance() {
        return ServiceManager.getService(AppSettingsState.class);
    }

    public AppSettingsState() {
        resourceAcquisitions = new ArrayList<>();
        resourceAcquisitions.add("Camera=android.hardware.Camera.open");
        resourceAcquisitions.add("Camera=android.hardware.camera2.CameraManager.openCamera");
        resourceAcquisitions.add("GPS=com.google.android.things.userdriver.UserDriverManager.registerGnssDriver");
        resourceAcquisitions.add("GPS=android.location.LocationManager.requestLocationUpdates");

        resourceReleases = new ArrayList<>();
        resourceReleases.add("Camera=android.hardware.Camera.release");
        resourceReleases.add("GPS=com.google.android.things.userdriver.UserDriverManager.unregisterGnssDriver");
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
