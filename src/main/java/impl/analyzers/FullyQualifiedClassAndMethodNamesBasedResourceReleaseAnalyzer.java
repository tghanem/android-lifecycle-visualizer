package impl.analyzers;

import com.intellij.psi.PsiElement;
import impl.model.dstl.ResourceRelease;

import java.util.HashSet;

public class FullyQualifiedClassAndMethodNamesBasedResourceReleaseAnalyzer
        extends FullQualifiedClassAndMethodNamesAnalyzerBase<ResourceRelease> {

    public FullyQualifiedClassAndMethodNamesBasedResourceReleaseAnalyzer(
            HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch) {

        super(methodCallsToMatch);
    }

    @Override
    protected ResourceRelease createMethodCallExpressionHolder(PsiElement methodCallExpression) {
        return new ResourceRelease(methodCallExpression);
    }
}
