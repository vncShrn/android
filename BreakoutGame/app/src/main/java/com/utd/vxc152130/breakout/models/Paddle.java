package com.utd.vxc152130.breakout.models;

import android.graphics.RectF;

import com.utd.vxc152130.breakout.other.Constants;

/*************************************************************************************************************
 * Data model class for Paddle
 * @author Vincy Shrine
 * @created 1/27/2017
 **************************************************************************************************************/

public class Paddle {
    // RectF is an object that holds four coordinates - (left, top, right bottom)
    private RectF bounds;
    // How long and high our paddle will be
    private float width;
    private float height;
    //this will hold the screen width and the height of the screen
    private int screenWidth;
    private int screenHeight;
    // X is the far left of the rectangle which forms our paddle
    private float x;
    // Y is the top coordinate
    private float y;
    // This will hold the pixels per second speed that the paddle will move
    private float paddleSpeed;
    //Ways in which the paddle can move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    /**
     * Instantiates a new Paddle.
     *
     * @param screenX the screen x
     * @param screenY the screen y
     */
    public Paddle(int screenX, int screenY) {
        width = screenX/2-100;
        height = Constants.PADDLE_HEIGHT;
        // Start paddle in roughly the sceen centre
        x = screenX / 2 - width / 2;
        y = screenY - (150 + 200);
        //(left, top, right bottom)
        bounds = new RectF(x, y, x + width, y + height);
        screenWidth = screenX;
        screenHeight = screenY - 200;
        // How fast is the paddle in pixels per second
        paddleSpeed = 500;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Get rectangle rectf.
     *
     * @return the rectf
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * Reset padde (left, top, right bottom)
     *
     * @param screenX the screen x
     * @param screenY the screen y
     */
    public void reset(float screenX, float screenY) {
        bounds.left = screenX / 2 - width / 2;
        bounds.top = screenY - 235;
        bounds.right = bounds.left + width;
        bounds.bottom = bounds.top + height;
    }

    /**
     * Set movement state as the paddle goes left, right or nowhere
     *
     * @param state the state
     */
    public void setMovementState(int state) {
        paddleMoving = state;
    }

    /**
     * Update method of paddle<br>
     * Determines if the paddle needs to move left or right <br>
     * ie) changes the coordinates in bounds
     *
     * @param fps the fps
     */
    public void update(long fps) {
        float tempLeftX = x - paddleSpeed / fps;
        if (paddleMoving == LEFT && tempLeftX >= 5) {
            x = tempLeftX;
        }
        float tempRightX = x + paddleSpeed / fps;
        if (paddleMoving == RIGHT && (tempRightX + width <= screenWidth - 5)) {
            x = tempRightX;
        }
        bounds.left = x;
        bounds.right = x + width;
    }
}