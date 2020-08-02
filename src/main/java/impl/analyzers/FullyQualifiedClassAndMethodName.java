package impl.analyzers;

import java.util.Objects;

public class FullyQualifiedClassAndMethodName {
    public static final FullyQualifiedClassAndMethodName CameraOpen =
            new FullyQualifiedClassAndMethodName(
                    "android.hardware.Camera",
                    "open");

    public static final FullyQualifiedClassAndMethodName CameraRelease =
            new FullyQualifiedClassAndMethodName(
                    "android.hardware.Camera",
                    "release");

    public static final FullyQualifiedClassAndMethodName GnssRegister =
            new FullyQualifiedClassAndMethodName(
                    "com.google.android.things.userdriver.UserDriverManager",
                    "registerGnssDriver");

    public static final FullyQualifiedClassAndMethodName GnssUnregister =
            new FullyQualifiedClassAndMethodName(
                    "com.google.android.things.userdriver.UserDriverManager",
                    "unregisterGnssDriver");

    public FullyQualifiedClassAndMethodName(
            String fullyQualifiedClassName,
            String methodName) {

        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.methodName = methodName;
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
