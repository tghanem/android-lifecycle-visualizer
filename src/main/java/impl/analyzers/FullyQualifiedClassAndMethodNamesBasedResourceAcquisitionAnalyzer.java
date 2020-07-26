package impl.analyzers;

import com.intellij.psi.PsiElement;
import impl.model.dstl.ResourceAcquisition;

import java.util.HashSet;

public class FullyQualifiedClassAndMethodNamesBasedResourceAcquisitionAnalyzer
        extends FullQualifiedClassAndMethodNamesAnalyzerBase<ResourceAcquisition> {

    public FullyQualifiedClassAndMethodNamesBasedResourceAcquisitionAnalyzer(
            HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch) {

        super(methodCallsToMatch);
    }

    @Override
    protected ResourceAcquisition createMethodCallExpressionHolder(PsiElement methodCallExpression) {
        return new ResourceAcquisition(methodCallExpression);
    }
}
