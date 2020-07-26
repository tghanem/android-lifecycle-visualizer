package impl.services;

import com.intellij.psi.PsiMethod;
import impl.analyzers.*;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.ILifecycleEventHandlerAnalyzer;
import interfaces.ILifecycleEventHandlerAnalyzerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LifecycleEventHandlerAnalyzerFactory implements ILifecycleEventHandlerAnalyzerFactory {
    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer() {
        return
                new CompoundResourceAcquisitionAnalyzer(
                        Arrays.asList(createFullyQualifiedClassAndMethodNameResourceAcquisitionAnalyzer()));
    }

    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceRelease> createResourceReleaseAnalyzer() {
        return
                new CompoundResourceReleaseAnalyzer(
                        Arrays.asList(createFullyQualifiedClassAndMethodNameResourceReleaseAnalyzer()));
    }

    private ILifecycleEventHandlerAnalyzer<ResourceAcquisition> createFullyQualifiedClassAndMethodNameResourceAcquisitionAnalyzer() {
        HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch =
                new HashSet<>();

        methodCallsToMatch.add(
                new FullyQualifiedClassAndMethodName(
                        "android.hardware.Camera",
                        "open"));

        methodCallsToMatch.add(
                new FullyQualifiedClassAndMethodName(
                        "com.google.android.things.userdriver.UserDriverManager",
                        "registerGnssDriver"));

        return new FullyQualifiedClassAndMethodNamesBasedResourceAcquisitionAnalyzer(methodCallsToMatch);
    }

    private ILifecycleEventHandlerAnalyzer<ResourceRelease> createFullyQualifiedClassAndMethodNameResourceReleaseAnalyzer() {
        HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch =
                new HashSet<>();

        methodCallsToMatch.add(
                new FullyQualifiedClassAndMethodName(
                        "android.hardware.Camera",
                        "release"));

        methodCallsToMatch.add(
                new FullyQualifiedClassAndMethodName(
                        "com.google.android.things.userdriver.UserDriverManager",
                        "unregisterGnssDriver"));

        return new FullyQualifiedClassAndMethodNamesBasedResourceReleaseAnalyzer(methodCallsToMatch);
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
