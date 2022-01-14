import org.apache.catalina.User;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class Test {

    static class TestClass{

        public Optional<User> getUser(){
            return null;
        }
    }

    public static void main(String[] args) {
        test();
    }
    public static void test(){
        TestClass testClass=new TestClass();

        Method declaredMethod = testClass.getClass().getDeclaredMethods()[0];
        ParameterizedTypeImpl genericParameterTypes = (ParameterizedTypeImpl)declaredMethod.getGenericReturnType();
        Type actualTypeArgument = genericParameterTypes.getActualTypeArguments()[0];
        boolean equals = actualTypeArgument.getClass().equals(User.class);
        System.out.println(equals);
        System.out.println(genericParameterTypes.getTypeName());
        System.out.println(genericParameterTypes.getClass());
        System.out.println(genericParameterTypes.getClass().equals(declaredMethod.getReturnType()));
        System.out.println(declaredMethod.getReturnType());
        System.out.println(declaredMethod.getGenericReturnType());
        ParameterizedType genericReturnType =(ParameterizedType) declaredMethod.getGenericReturnType();
        System.out.println(genericReturnType.getTypeName());
        Type[] actualTypeArguments = genericReturnType.getActualTypeArguments();
        System.out.println(Arrays.toString(actualTypeArguments));
        Class<?> returnType = declaredMethod.getReturnType();

    }
}
