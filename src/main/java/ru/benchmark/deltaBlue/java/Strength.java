package ru.benchmark.deltaBlue.java;

/**
 * Created by evans on 19.03.14.
 */

/*
Strengths are used to measure the relative importance of constraints.
New strengths may be inserted in the strength hierarchy without
disrupting current constraints.  Strengths cannot be created outside
this class, so pointer comparison can be used for value comparison.
*/
class Strength {

    private int strengthValue;
    private String name;

    private Strength(int strengthValue, String name)
    {
        this.strengthValue= strengthValue;
        this.name= name;
    }

    public static boolean stronger(Strength s1, Strength s2)
    {
        return s1.strengthValue < s2.strengthValue;
    }

    public static boolean weaker(Strength s1, Strength s2)
    {
        return s1.strengthValue > s2.strengthValue;
    }

    public static Strength weakestOf(Strength s1, Strength s2)
    {
        return weaker(s1, s2) ? s1 : s2;
    }

    public static Strength strongest(Strength s1, Strength s2)
    {
        return stronger(s1, s2) ? s1 : s2;
    }

    // for iteration
    public Strength nextWeaker()
    {
        switch (strengthValue) {
            case 0: return weakest;
            case 1: return weakDefault;
            case 2: return normal;
            case 3: return strongDefault;
            case 4: return preferred;
            case 5: return strongPreferred;

            case 6:
            default:
                System.err.println("Invalid call to nextStrength()!");
                System.exit(1);
                return null;
        }
    }

    // Strength constants
    public final static Strength required       = new Strength(0, "required");
    public final static Strength strongPreferred= new Strength(1, "strongPreferred");
    public final static Strength preferred      = new Strength(2, "preferred");
    public final static Strength strongDefault  = new Strength(3, "strongDefault");
    public final static Strength normal	      = new Strength(4, "normal");
    public final static Strength weakDefault    = new Strength(5, "weakDefault");
    public final static Strength weakest	      = new Strength(6, "weakest");

    public void print()
    {
        System.out.print("strength[" + Integer.toString(strengthValue) + "]");
    }
}
