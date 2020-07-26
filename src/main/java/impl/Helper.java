package impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import interfaces.consumers.PsiMethodCallExpressionConsumer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class Helper {
    public static void processMethodCallExpressions(
            PsiElement parent,
            PsiMethodCallExpressionConsumer consumer) {

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(parent, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiElement methodReferenceExpression =
                    methodCallExpression.getFirstChild();

            if (methodReferenceExpression instanceof PsiReferenceExpression) {
                PsiReferenceExpression referenceExpression =
                        (PsiReferenceExpression) methodReferenceExpression;

                if (referenceExpression.getQualifierExpression() != null) {
                    if (referenceExpression.getQualifierExpression().getType() != null) {
                        String qualifierName =
                                referenceExpression
                                        .getQualifierExpression()
                                        .getType()
                                        .getCanonicalText();

                        String[] methodName =
                                referenceExpression
                                        .getQualifiedName()
                                        .split("\\.");

                        if (methodName.length > 1) {
                            consumer.process(
                                    methodCallExpression,
                                    qualifierName,
                                    methodName[methodName.length - 1]);
                        }
                    }
                }
            }
        }

    }

    public static Optional<Element> findFirst(
            NodeList nodeList,
            Predicate<Element> predicate) {

        AtomicReference<Optional<Element>> result =
                new AtomicReference<>(Optional.empty());

        processChildElements(
                nodeList,
                element -> {
                    if (predicate.test(element)) {
                        result.set(Optional.of(element));
                        return false;
                    }
                    return true;
                });

        return result.get();
    }

    public static void processChildElements(
            NodeList nodeList,
            Function<Element, Boolean> processElement) {

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node instanceof Element) {
                if (!processElement.apply((Element) node)) {
                    break;
                }
            }
        }
    }

}
