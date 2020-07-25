package impl;

import com.intellij.psi.impl.source.PsiMethodImpl;
import impl.graphics.ActivityMetadataToRender;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import windows.ActivityForm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {
    public static void main(String[] args) throws Exception {
        try {
            List<ResourceAcquisition> acquisitions =
                    new ArrayList<>();

            acquisitions.add(
                    new ResourceAcquisition(
                            null));

            acquisitions.add(
                    new ResourceAcquisition(
                            null));

            List<ResourceRelease> releases =
                    new ArrayList<>();

            releases.add(
                    new ResourceRelease(
                            null));

            LifecycleEventHandler onResume =
                    new LifecycleEventHandler(
                            null,
                            acquisitions,
                            new ArrayList<>());

            LifecycleEventHandler onPause =
                    new LifecycleEventHandler(
                            null,
                            new ArrayList<>(),
                            releases);

            ActivityForm form = new ActivityForm();

            form.display(
                    new ActivityMetadataToRender(
                        null,
                        Arrays.asList(onResume, onPause)));

            JFrame frame = new JFrame();
            frame.setSize(600, 800);
            frame.add(form.getContent());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
