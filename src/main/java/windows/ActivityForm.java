package windows;

import impl.dsvl.LifecycleNode;
import interfaces.IActivityViewProvider;

import javax.swing.*;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;
    private JButton onCreateButton;
    private JButton onRestartButton;
    private JButton onStartButton;
    private JButton onResumeButton;
    private JButton onPauseButton;
    private JButton onStopButton;
    private JButton onDestroyButton;
    private JLabel activityLaunchedLabel;
    private JLabel activityRunningLabel;
    private JLabel appProcessKilledLabel;
    private JLabel activityShutdownLabel;

    @Override
    public void display(LifecycleNode viewDocument) {
    }

    public JPanel getContent() {
        return mainPanel;
    }
}
