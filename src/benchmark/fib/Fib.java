package benchmark.fib;

/**
 * Evgeny Vanslov
 * vans239@gmail.com
 */
public class Fib {

    public static int fibStaticTernary(int n) {
        return (n >= 2) ? fibStaticTernary(n - 1) + fibStaticTernary(n - 2) : 1;
    }

    public static int fibStaticIf(int n) {
        if (n >= 2)
            return fibStaticIf(n - 1) + fibStaticIf(n - 2);
        else
            return 1;
    }

    int fibTernary(int n) {
        return (n >= 2) ? fibStaticTernary(n - 1) + fibStaticTernary(n - 2) : 1;
    }

    int fibIf(int n) {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2);
        else
            return 1;
    }

    public static void main(String[] args) {

        Fib.fibStaticTernary(40);
        long start = System.currentTimeMillis();
        Fib.fibStaticTernary(40);
        System.out.println("Java(static ternary): "
                + (System.currentTimeMillis() - start) + "ms");

        Fib.fibStaticIf(40);
        start = System.currentTimeMillis();
        Fib.fibStaticIf(40);
        System.out.println("Java(static if): "
                + (System.currentTimeMillis() - start) + "ms");


        new Fib().fibTernary(40);   //why not jit?
        start = System.currentTimeMillis();
        new Fib().fibTernary(40);
        System.out.println("Java(instance ternary): "
                + (System.currentTimeMillis() - start) + "ms");

        new Fib().fibIf(40);
        start = System.currentTimeMillis();
        new Fib().fibIf(40);
        System.out.println("Java(instance if): "
                + (System.currentTimeMillis() - start) + "ms");
    }
}