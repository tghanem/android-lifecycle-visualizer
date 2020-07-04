package windows;

import interfaces.IActivityViewProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;

    @Override
    public void display(Document viewDocument) {
        Element root = viewDocument.getDocumentElement();
    }

    public JPanel getContent() {
        return mainPanel;
    }
}
