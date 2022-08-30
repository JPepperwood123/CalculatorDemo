package polynomial;

/**
 * NumIndiv holds any individual number who is a rational number so that it
 * can be divided into a numerator and denominator. NaN is a special NumIndiv whose denomiator is zero.
 *
 */
public final class NumIndiv extends Number implements Comparable<NumIndiv> {
    // Representation invariant for every NumIndiv n:
    //   n.denominator >= 0 && n.denominator > 0 and ==> r.numerator and r.denominator are reduced to its lowest forms)


    private final int numerator, denominator;

    public static final NumIndiv NaN = new NumIndiv(1, 0);

    public static final NumIndiv zero = new NumIndiv(0);

    /**
     * Constructor with numerator value only
     */
    public NumIndiv(int n) {
        numerator = n;
        denominator = 1;
        checkRepInv();
    }

    /**
     * Constructor with numerator and denominator value
     */
    public NumIndiv(int n, int d) {
        if (d != 0) {
            // Reduce individual number to the lowest form
            n /= gcd(n, d);
            d /= gcd(n, d);

            if(d < 0) {
                n = -n;
                d = -d;
            }
        }

        numerator = n;
        denominator = d;

        checkRepInv();
    }

    /**
     * If the representation invariant is violated, throws an exception
     */
    private void checkRepInv() {
        assert (denominator >= 0) : "Denominator can't be negative";

        if (denominator > 0) {
            int thisGcd = gcd(numerator, denominator);
            assert (thisGcd == 1 || thisGcd == -1) : "Not in lowest form";
        }
    }

    @Override
    public int compareTo(NumIndiv o) {
        return 0;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }
}