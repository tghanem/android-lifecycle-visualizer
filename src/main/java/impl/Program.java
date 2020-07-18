package impl;

import impl.graphics.LifecycleHandlerCollection;
import impl.model.dstl.LifecycleAwareComponent;
import windows.ActivityForm;

import javax.swing.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Program {
    public static void main(String[] args) throws Exception {
        String filePath =
                "D:\\Projects\\Omni-Notes\\omniNotes\\src\\main\\java\\it\\feio\\android\\omninotes\\MainActivity.java";

        Optional<LifecycleAwareComponent> parseResult =
                new ActivityFileParser()
                        .parse(
                                new String(Files.readAllBytes(Paths.get(filePath)), Charset.forName("utf-8")),
                                "MainActivity");

        LifecycleHandlerCollection collection =
                new LifecycleHandlerCollection(
                    parseResult
                            .get()
                            .getLifecycleEventHandlers());

        ActivityForm form = new ActivityForm();

        form.display(collection);

        JFrame frame = new JFrame();
        frame.setSize(600, 800);
        frame.add(form.getContent());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
