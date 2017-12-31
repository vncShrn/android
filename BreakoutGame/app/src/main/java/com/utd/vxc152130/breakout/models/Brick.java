package com.utd.vxc152130.breakout.models;

import android.graphics.RectF;

import com.utd.vxc152130.breakout.other.BrickType;

/*************************************************************************************************************
 * Data model for Brick
 * @author Vincy Shrine
 * @created 11/27/2017
 *************************************************************************************************************/
public class Brick {
    private RectF bounds;
    private boolean isVisible = true;

    /*gives the type of brick*/
    private BrickType type;

    /*gives the number of hits required inorder to become invisible*/
    private int hits;

    /**
     * Instantiates a new Brick.
     *
     * @param type   the type
     * @param row    the row
     * @param column the column
     * @param width  the width
     * @param height the height
     */
    public Brick(BrickType type, int row, int column, int width, int height) {

        int padding = 1;

        bounds = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);

        this.type = type;
        this.hits = type.getNumberOfHits();
    }

    /**
     * Get bounds bounds f.
     *
     * @return the bounds f
     */
    public RectF getBounds() {
        return this.bounds;
    }

    /**
     * Set invisible.
     */
    public void setInvisible() {
        isVisible = false;
    }

    /**
     * Get visibility boolean.
     *
     * @return the boolean
     */
    public boolean getVisibility() {
        return isVisible;
    }

    /**
     * Get hits int.
     *
     * @return the int
     */
    public int getHits() {
        return hits;
    }

    /**
     * Decrement hits.
     */
    public void decrementHits() {
        hits--;
    }

    /**
     * Get type brick type.
     *
     * @return the brick type
     */
    public BrickType getType() {
        return type;
    }
}