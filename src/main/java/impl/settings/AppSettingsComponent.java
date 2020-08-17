package impl.settings;

import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.sun.istack.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class AppSettingsComponent {

    private final JPanel mainPanel;
    private final JBTextArea resourceAcquisitions = new JBTextArea();
    private final JBTextArea resourceReleases = new JBTextArea();

    public AppSettingsComponent() {
        resourceAcquisitions.setBorder(
                new ColoredSideBorder(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, 1));

        resourceReleases.setBorder(
                new ColoredSideBorder(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, 1));

        mainPanel =
                FormBuilder
                    .createFormBuilder()
                    .addComponent(new JLabel("Resource Acquisitions"))
                    .addComponent(resourceAcquisitions)
                    .addComponent(new JLabel("Resource Releases"))
                    .addComponent(resourceReleases)
                    .addComponentFillVertically(new JPanel(), 0)
                    .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return resourceAcquisitions;
    }

    @NotNull
    public List<String> getResourceAcquisitions() {
        return splitByLineSeparator(resourceAcquisitions.getText());
    }

    @NotNull
    public List<String> getResourceReleases() {
        return splitByLineSeparator(resourceReleases.getText());
    }

    public void setResourceAcquisitions(List<String> acquisitions) {
        resourceAcquisitions.setText(getAsSingleText(acquisitions));
    }

    public void setResourceReleases(List<String> releases) {
        resourceReleases.setText(getAsSingleText(releases));
    }

    public List<String> splitByLineSeparator(String text) {
        String[] lines = text.split("\\r?\\n");
        return Arrays.asList(lines);
    }

    private String getAsSingleText(List<String> collection) {
        StringBuilder sb = new StringBuilder();
        for (String item : collection) {
            sb.append(item + System.lineSeparator());
        }
        return sb.toString();
    }
}
