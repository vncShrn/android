package com.utd.vxc152130.breakout.other;

/*************************************************************************************************************
 * Enum for brick type contains # hits, color, value
 * @author Vincy Shrine
 * @created 11/27/2017
 **************************************************************************************************************/
public enum BrickType {

    /**
     * Red brick type
     */
    RED(7, 1,"#FF9D0A0A"),
    /**
     * Orange brick type.
     */
    ORANGE(5, 2,"#EFD98207"),
    /**
     * Green brick type.
     */
    GREEN(3, 1, "#FF196D17"),
    /**
     * Yellow brick type.
     */
    YELLOW(1, 1, "#FFFDD835");

    private final int brickValue;
    private final int numberOfHits;
    private String colorValue;

    /**
     * Gets brick value.
     * @return the brick value
     */
    public int getBrickValue() {
        return brickValue;
    }

    /**
     * Gets number of hits.
     * @return the number of hits
     */
    public int getNumberOfHits() {
        return numberOfHits;
    }

    /**
     * Constructor for enum
     * @param val
     * @param hits
     */
    BrickType(int val, int hits, String colorValue) {
        this.brickValue = val;
        this.numberOfHits = hits;
        this.colorValue = colorValue;
    }

    public String getColorValue() {
        return colorValue;
    }
}
