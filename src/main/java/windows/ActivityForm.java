package windows;

import impl.model.dstl.LifecycleEventHandler;
import interfaces.IActivityViewProvider;

import javax.swing.*;
import java.util.List;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;

    @Override
    public void display(List<LifecycleEventHandler> handlers) {

    }

    public JPanel getContent() {
        return mainPanel;
    }
}
