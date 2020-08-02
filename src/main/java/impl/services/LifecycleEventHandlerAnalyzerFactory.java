package impl.services;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import impl.analyzers.FullQualifiedClassAndMethodNamesAnalyzer;
import impl.analyzers.FullyQualifiedClassAndMethodName;
import impl.model.dstl.*;
import interfaces.ILifecycleEventHandlerAnalyzer;
import interfaces.ILifecycleEventHandlerAnalyzerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class LifecycleEventHandlerAnalyzerFactory implements ILifecycleEventHandlerAnalyzerFactory {
    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer() {
        HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, ResourceAcquisition>> map =
                new HashMap<>();

        map.put(FullyQualifiedClassAndMethodName.CameraOpen, e -> new CameraAcquired(e));
        map.put(FullyQualifiedClassAndMethodName.GnssRegister, e -> new BluetoothAcquired(e));

        return
                new CompoundResourceAcquisitionAnalyzer(
                        Arrays.asList(new FullQualifiedClassAndMethodNamesAnalyzer<>(map)));
    }

    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceRelease> createResourceReleaseAnalyzer() {
        HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, ResourceRelease>> map =
                new HashMap<>();

        map.put(FullyQualifiedClassAndMethodName.CameraRelease, e -> new CameraReleased(e));
        map.put(FullyQualifiedClassAndMethodName.GnssUnregister, e -> new BluetoothReleased(e));

        return
                new CompoundResourceReleaseAnalyzer(
                        Arrays.asList(new FullQualifiedClassAndMethodNamesAnalyzer<>(map)));
    }

    class CompoundResourceAcquisitionAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceAcquisition> {
        CompoundResourceAcquisitionAnalyzer(List<ILifecycleEventHandlerAnalyzer<ResourceAcquisition>> analyzers) {
            this.analyzers = analyzers;
        }

        @Override
        public List<ResourceAcquisition> analyze(PsiMethod method) {
            List<ResourceAcquisition> result = new ArrayList<>();
            for (ILifecycleEventHandlerAnalyzer<ResourceAcquisition> analyzer : analyzers) {
                for (ResourceAcquisition acquisition : analyzer.analyze(method)) {
                    result.add(acquisition);
                }
            }
            return result;
        }

        private final List<ILifecycleEventHandlerAnalyzer<ResourceAcquisition>> analyzers;
    }

    class CompoundResourceReleaseAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceRelease> {
        CompoundResourceReleaseAnalyzer(List<ILifecycleEventHandlerAnalyzer<ResourceRelease>> analyzers) {
            this.analyzers = analyzers;
        }

        @Override
        public List<ResourceRelease> analyze(PsiMethod method) {
            List<ResourceRelease> result = new ArrayList<>();
            for (ILifecycleEventHandlerAnalyzer<ResourceRelease> analyzer : analyzers) {
                for (ResourceRelease release : analyzer.analyze(method)) {
                    result.add(release);
                }
            }
            return result;
        }

        private final List<ILifecycleEventHandlerAnalyzer<ResourceRelease>> analyzers;
    }
}
