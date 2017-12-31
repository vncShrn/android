package com.utd.vxc152130.breakout.models;

import com.utd.vxc152130.breakout.other.Constants;
import java.util.Random;

/*************************************************************************************************************
 * Data model for Ball
 * @author Vincy Shrine
 * @created 11 /27/2017
 *************************************************************************************************************/
public class Ball {
    /**
     * The X velocity.
     */
    public float xVelocity;
    /**
     * The Y velocity.
     */
    public float yVelocity;
    /**
     * The Center x.
     */
    public float centerX;
    /**
     * The Center y.
     */
    public float centerY;
    /**
     * The Radius.
     */
    public float radius;

    /**
     * Instantiates a new Ball.
     *
     * @param screenX the screen x
     * @param screenY the screen y
     */
    public Ball(int screenX, int screenY) {
        xVelocity = Constants.INITIAL_VELOCITY_X;
        yVelocity = Constants.INITIAL_VELOCITY_Y;
        radius = screenX / 40;
    }

    /**
     * Updates the ball position based on velocity fps.
     *
     * @param fps the fps
     */
    public void update(long fps) {
        centerX = centerX + (xVelocity / fps);
        centerY = centerY + (yVelocity / fps);
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public float getX() {
        return centerX;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public float getY() {
        return centerY;
    }

    /**
     * Gets radius.
     *
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Reverse y velocity.
     */
    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    /**
     * Reverse x velocity.
     */
    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }


    /**
     * Sets random x velocity.
     */
    public void setRandomXVelocity() {
        Random generator = new Random();
        int picked = 7 - generator.nextInt(8);
        if (picked == 0) {
            reverseXVelocity();
        }
    }

    /**
     * Clear obstacle and reset centerY to y.
     *
     * @param y the y
     */
    public void clearObstacleY(float y) {
        centerY = y;
    }

    /**
     * Clear obstacle and reset centerX to x.
     *
     * @param x the x
     */
    public void clearObstacleX(float x) {
        centerX = x;
    }

    /**
     * Reset ball position to start state.
     *
     * @param x the x
     * @param y the y
     */
    public void reset(int x, int y) {
        centerX = x / 2;
        centerY = y - (255);
    }
}

