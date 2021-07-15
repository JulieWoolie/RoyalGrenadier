package net.forthecrown.grenadier.types.pos;

/**
 * A class which holds 3 string suggestions for suggest coordinates.
 */
public class CoordinateSuggestion {
    /**
     * The default ~ ~ ~ coordinate suggestion
     */
    public static final CoordinateSuggestion DEFAULT = new CoordinateSuggestion("~", "~", "~");

    private final String x;
    private final String y;
    private final String z;

    public CoordinateSuggestion(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordinateSuggestion(double x, double y, double z){
        this(x + "", y + "", z + "");
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    @Override
    public String toString() {
        return x + ' ' + y + ' ' + z;
    }
}
