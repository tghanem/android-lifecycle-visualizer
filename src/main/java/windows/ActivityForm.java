package windows;

import interfaces.IActivityViewProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public void display(Document viewDocument) {
        Element root = viewDocument.getDocumentElement();
    }

    public JPanel getContent() {
        return mainPanel;
    }
}
