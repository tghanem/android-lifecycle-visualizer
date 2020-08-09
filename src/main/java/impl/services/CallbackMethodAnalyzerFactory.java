package impl.services;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import impl.analyzers.FullyQualifiedClassAndMethodNamesAnalyzer;
import impl.analyzers.FullyQualifiedClassAndMethodName;
import impl.model.dstl.*;
import interfaces.ICallbackMethodAnalyzer;
import interfaces.ICallbackMethodAnalyzerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class CallbackMethodAnalyzerFactory implements ICallbackMethodAnalyzerFactory {
    @Override
    public ICallbackMethodAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer() {
        HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, ResourceAcquisition>> map =
                new HashMap<>();

        map.put(FullyQualifiedClassAndMethodName.CameraOpen, e -> new CameraAcquired(e));
        map.put(FullyQualifiedClassAndMethodName.GnssRegister, e -> new BluetoothAcquired(e));

        return
                new CompoundResourceAcquisitionAnalyzer(
                        Arrays.asList(new FullyQualifiedClassAndMethodNamesAnalyzer<>(map)));
    }

    @Override
    public ICallbackMethodAnalyzer<ResourceRelease> createResourceReleaseAnalyzer() {
        HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, ResourceRelease>> map =
                new HashMap<>();

        map.put(FullyQualifiedClassAndMethodName.CameraRelease, e -> new CameraReleased(e));
        map.put(FullyQualifiedClassAndMethodName.GnssUnregister, e -> new BluetoothReleased(e));

        return
                new CompoundResourceReleaseAnalyzer(
                        Arrays.asList(new FullyQualifiedClassAndMethodNamesAnalyzer<>(map)));
    }

    class CompoundResourceAcquisitionAnalyzer implements ICallbackMethodAnalyzer<ResourceAcquisition> {
        CompoundResourceAcquisitionAnalyzer(List<ICallbackMethodAnalyzer<ResourceAcquisition>> analyzers) {
            this.analyzers = analyzers;
        }

        @Override
        public List<ResourceAcquisition> analyze(PsiMethod method) {
            List<ResourceAcquisition> result = new ArrayList<>();
            for (ICallbackMethodAnalyzer<ResourceAcquisition> analyzer : analyzers) {
                for (ResourceAcquisition acquisition : analyzer.analyze(method)) {
                    result.add(acquisition);
                }
            }
            return result;
        }

        private final List<ICallbackMethodAnalyzer<ResourceAcquisition>> analyzers;
    }

    class CompoundResourceReleaseAnalyzer implements ICallbackMethodAnalyzer<ResourceRelease> {
        CompoundResourceReleaseAnalyzer(List<ICallbackMethodAnalyzer<ResourceRelease>> analyzers) {
            this.analyzers = analyzers;
        }

        @Override
        public List<ResourceRelease> analyze(PsiMethod method) {
            List<ResourceRelease> result = new ArrayList<>();
            for (ICallbackMethodAnalyzer<ResourceRelease> analyzer : analyzers) {
                for (ResourceRelease release : analyzer.analyze(method)) {
                    result.add(release);
                }
            }
            return result;
        }

        private final List<ICallbackMethodAnalyzer<ResourceRelease>> analyzers;
    }
}
