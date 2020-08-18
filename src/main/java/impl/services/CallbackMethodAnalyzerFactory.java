package impl.services;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import impl.analyzers.FullyQualifiedClassAndMethodName;
import impl.analyzers.FullyQualifiedClassAndMethodNamesAnalyzer;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import impl.settings.AppSettingsState;
import interfaces.ICallbackMethodAnalyzer;
import interfaces.ICallbackMethodAnalyzerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CallbackMethodAnalyzerFactory implements ICallbackMethodAnalyzerFactory {
    @Override
    public ICallbackMethodAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer() {
        return
                new CompoundResourceAcquisitionAnalyzer(
                        Arrays.asList(
                                new FullyQualifiedClassAndMethodNamesAnalyzer<>(
                                        toHashMap(
                                                AppSettingsState.getInstance().resourceAcquisitions,
                                                (name, expression) -> new ResourceAcquisition(expression, name)))));
    }

    @Override
    public ICallbackMethodAnalyzer<ResourceRelease> createResourceReleaseAnalyzer() {
        return
                new CompoundResourceReleaseAnalyzer(
                        Arrays.asList(
                                new FullyQualifiedClassAndMethodNamesAnalyzer<>(
                                        toHashMap(
                                                AppSettingsState.getInstance().resourceReleases,
                                                (name, expression) -> new ResourceRelease(expression, name)))));
    }

    private <T> HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, T>> toHashMap(
            Collection<String> serializedItems,
            BiFunction<String, PsiMethodCallExpression, T> createResourceDescription) {

        HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, T>> map =
                new HashMap<>();

        for (String item : serializedItems) {
            String[] tokens = item.split("=");

            if (tokens.length == 2) {
                map.put(
                        FullyQualifiedClassAndMethodName.valueOf(tokens[1]),
                        e -> createResourceDescription.apply(tokens[0], e));
            }
        }

        return map;
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
