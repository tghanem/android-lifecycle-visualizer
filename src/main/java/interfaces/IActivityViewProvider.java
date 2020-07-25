package interfaces;

import impl.graphics.ActivityMetadataToRender;

public interface IActivityViewProvider {
    void display(ActivityMetadataToRender metadata);
}
