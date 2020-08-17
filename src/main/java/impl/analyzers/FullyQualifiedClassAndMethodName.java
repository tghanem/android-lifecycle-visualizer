package impl.analyzers;

import java.util.Objects;

public class FullyQualifiedClassAndMethodName {
    public FullyQualifiedClassAndMethodName(
            String fullyQualifiedClassName,
            String methodName) {

        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodName = methodName;
    }

    public static FullyQualifiedClassAndMethodName valueOf(String fullyQualifiedMethodName) {
        int indexOfLastDot = fullyQualifiedMethodName.lastIndexOf('.');

        return
                new FullyQualifiedClassAndMethodName(
                        fullyQualifiedMethodName.substring(0, indexOfLastDot),
                        fullyQualifiedMethodName.substring(indexOfLastDot + 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        FullyQualifiedClassAndMethodName that = (FullyQualifiedClassAndMethodName) o;

        return fullyQualifiedClassName.equals(that.fullyQualifiedClassName) &&
                methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedClassName, methodName);
    }

    private final String fullyQualifiedClassName;
    private final String methodName;
}
