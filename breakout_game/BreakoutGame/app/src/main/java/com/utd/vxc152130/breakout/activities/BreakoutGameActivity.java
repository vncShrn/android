package com.utd.vxc152130.breakout.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.utd.vxc152130.breakout.R;
import com.utd.vxc152130.breakout.models.Ball;
import com.utd.vxc152130.breakout.models.Brick;
import com.utd.vxc152130.breakout.models.Paddle;
import com.utd.vxc152130.breakout.models.Score;
import com.utd.vxc152130.breakout.other.BrickType;
import com.utd.vxc152130.breakout.other.Constants;
import com.utd.vxc152130.breakout.other.Helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*************************************************************************************************************
 * Main Activity that displays game area<br>
 * Dynamic layout contains Toolbar and BreakOutView
 * @author Vincy Shrine
 * @created 11 /27/2017
 */
public class BreakoutGameActivity extends AppCompatActivity {

    private static final int REQUESTCODE_ASK_PERMISSIONS = 100;
    /**
     * Custom view of the game area
     */
    BreakoutView breakoutView;
    /**
     * On clicking scores icon, DisplayScoresActivity activity is invoked
     */
    ImageView scoresImage;

    /**
     * On clicking about icon, About activity is invoked
     */
    ImageView aboutImage;
    /**
     * Seeker to adjust ball speed
     */
    SeekBar ballSpeedSeekbar;
    /**
     * Seeker changes the fps value, it is 50 by default
     */
    public static long fps = Constants.FPS_DEFAULT;
    /**
     * Toggle button toggles game sound state, ON by default
     */
    boolean sound_on = true;
    /**
     * The Bundle.
     */
    Bundle bundle = new Bundle();
    /**
     * The Sound toggle.
     */
    ToggleButton sound_toggle;
    /**
     * The Sound pool.
     */
    SoundPool soundPool;
    /**
     * The Lives.
     */
    int lives = Constants.TOTAL_LIVES;
    /**
     * The Df.
     */
    SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy");
    /**
     * To differentiate if game thread is already started. To avoid starting thread again
     */
    boolean isGameThreadStarted = false;
    private ArrayList<Score> scoresList = new ArrayList<>();
    private int bgImage = R.drawable.clouds;

    /**
     * On create of Main activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check file permissions and populate memory of contacts
        checkStorageAccessPermissions();

        /*******************************************************************************************
         *  Create layout dynamically during runtime -Begin
         ******************************************************************************************/
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        breakoutView = new BreakoutView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // set Layout_Width and Layout_Height
        LinearLayout.LayoutParams layoutForOuter = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutForOuter);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // toolbar
        View settingsToolbar = inflater.inflate(R.layout.toolbar_game_settings, layout, false);
        // TITLE
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutForEmpty = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        titleLayout.setLayoutParams(layoutForEmpty);
        titleLayout.setBackgroundColor(Color.BLACK);
        //TITLE image
        RelativeLayout.LayoutParams imageparam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutForEmpty.gravity = Gravity.CENTER;
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.breakout_title);
        imageView.setPadding(20, 20, 20, 20);
        imageView.setLayoutParams(imageparam);
        titleLayout.addView(imageView);
        // build complete layout
        layout.addView(settingsToolbar);
        layout.addView(titleLayout);
        layout.addView(breakoutView);
        setContentView(layout);
        /*******************************************************************************************
         *  Create layout dynamically during runtime -End
         ******************************************************************************************/

        // get view objects
        ballSpeedSeekbar = (SeekBar) findViewById(R.id.seekBar);
        scoresImage = (ImageView) findViewById(R.id.scoresImage);
        aboutImage = (ImageView) findViewById(R.id.helpImage);
        sound_toggle = (ToggleButton) findViewById(R.id.toggleSoundButton);
        scoresImage.setOnClickListener(createScoresImageClickListener());
        aboutImage.setOnClickListener(createAboutImageClickListener());
        sound_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSound(v);
            }
        });
        ballSpeedSeekbar.setMax(Constants.MAX_BALL_SPEED);
        ballSpeedSeekbar.setProgress(Constants.FPS_DEFAULT);
        ballSpeedSeekbar.setOnSeekBarChangeListener(createSeekListener());
    }

    /*************************************************************************************************************
     * Breakout View inner runnable class containing game logic
     * @author Vincy Shrine
     * @created 11 /27/2017
     ************************************************************************************************************/
    class BreakoutView extends SurfaceView implements Runnable, SensorEventListener {

        /**
         * The Holder used for Paint and Canvas
         */
        SurfaceHolder ourHolder;
        /**
         * The Playing or not value
         */
        volatile boolean playing;
        /**
         * The Paused.
         */
        boolean paused = true;
        /**
         * The Canvas.
         */
        Canvas canvas;
        /**
         * The Paint.
         */
        Paint paint;
        // This variable tracks the game frame rate
        // public static fps;
        //Pause lock to control game thread
        private Object pauseLock;
        private boolean mPause;
        /**
         * The Screen x in pixels
         */
        int screenX;
        /**
         * The Screen y.
         */
        int screenY;
        /**
         * The Screen top left.
         */
        int screenTopLeft = 0 + 200;
        //Sensor Manager for the Accelerometer
        private SensorManager sensorMgr;
        private Sensor accelerometer;
        private Sensor lightSensor;
        private long lastBallSpeedUpdatedAt = -1;
        /**
         * The Max possible score.
         */
        int maxScore = 0;
        /**
         * The player's Paddle.
         */
        Paddle paddle;
        /**
         * The Ball.
         */
        Ball ball;
        /**
         * The Initial touch xcoordinate.
         */
        float initialTouch = 0;
        /**
         * The Bricks.
         */
        Brick[] bricks = new Brick[100];
        /**
         * The Num bricks.
         */
        int numBricks = 0;
        /* For sound FX */
        /**
         * The Hit paddle.
         */
        int hit_paddle = -1;
        /**
         * The Hit wall.
         */
        int hit_wall = -1;
        /**
         * The Game completed.
         */
        int game_completed = -1;
        /**
         * The Lose life id.
         */
        int loseLifeID = -1;
        /**
         * The Hit brick.
         */
        int hit_brick = -1;
        /**
         * The Score.
         */
        int score = 0;
        private boolean speedDoubled = false;

        /**
         * Instantiates a new Breakout view.
         *
         * @param context the context
         */
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);
            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();
            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y - 200;
            paddle = new Paddle(screenX, screenY);
            // Create a ball
            ball = new Ball(screenX, screenY);
            //Initialize the pause lock
            pauseLock = new Object();
            mPause = false;
            // Load the sounds
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            try {
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                // Load our fx in memory ready for use
                AssetFileDescriptor descriptor = assetManager.openFd("hit_wall.wav");
                hit_wall = soundPool.load(descriptor, 0);
                hit_paddle = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("lose_life.wav");
                loseLifeID = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("hit_brick.wav");
                hit_brick = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("game_complete.wav");
                game_completed = soundPool.load(descriptor, 0);
            } catch (IOException e) {
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }
            sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            lightSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensorMgr.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            createBricksAndRestart();
        }

        /**
         * Reset ball and paddle.
         */
        public void resetBallAndPaddle() {
            // Put the ball back to the start
            ball.reset(screenX, screenY);
            paddle.reset(screenX, screenY);
            // also reset speed of ball
            fps = Constants.FPS_DEFAULT;
            ball.xVelocity = Constants.INITIAL_VELOCITY_X;
            ball.yVelocity = Constants.INITIAL_VELOCITY_Y;
            if (ballSpeedSeekbar != null)
                ballSpeedSeekbar.setProgress((int) fps);
            paused = true;
        }

        /**
         * Create bricks and restart.
         */
        public void createBricksAndRestart() {
            // Put the ball back to the start
            ball.reset(screenX, screenY);
            paddle.setWidth(screenX / 2 - 200);
            paddle.reset(screenX, screenY);
            int brickWidth = screenX / 10;
            int brickHeight = screenX / 20;
            maxScore = 0;
            // Build a wall of bricks with 10 bricks per row, 2 rows of same color, and a total of 4 colors, so a total of 80 bricks
            numBricks = 0;
            BrickType type = BrickType.RED;
            for (int column = 0; column < 10; column++) {
                for (int row = 0; row < 2; row++) {
                    bricks[numBricks++] = new Brick(type, row, column, brickWidth, brickHeight);
                }
            }
            maxScore += type.getBrickValue() * type.getNumberOfHits() * 20;
            type = BrickType.ORANGE;
            for (int column = 0; column < 10; column++) {
                for (int row = 2; row < 4; row++) {
                    bricks[numBricks++] = new Brick(type, row, column, brickWidth, brickHeight);
                }
            }
            maxScore += type.getBrickValue() * type.getNumberOfHits() * 20;
            type = BrickType.GREEN;
            for (int column = 0; column < 10; column++) {
                for (int row = 4; row < 6; row++) {
                    bricks[numBricks++] = new Brick(type, row, column, brickWidth, brickHeight);
                }
            }
            maxScore += type.getBrickValue() * type.getNumberOfHits() * 20;
            type = BrickType.YELLOW;
            for (int column = 0; column < 10; column++) {
                for (int row = 6; row < 8; row++) {
                    bricks[numBricks++] = new Brick(type, row, column, brickWidth, brickHeight);
                }
            }
            maxScore += type.getBrickValue() * type.getNumberOfHits() * 20;
            // if game over reset scores and lives
            score = 0;
            lives = Constants.TOTAL_LIVES;
            ball.xVelocity = Constants.INITIAL_VELOCITY_X;
            ball.yVelocity = Constants.INITIAL_VELOCITY_Y;
            speedDoubled = false;
            if (ballSpeedSeekbar != null)
                ballSpeedSeekbar.setProgress(Constants.FPS_DEFAULT);
            fps = Constants.FPS_DEFAULT;
            paused = true;
        }

        /**
         * Game thread
         */
        @Override
        public void run() {
            while (playing) {
                synchronized (pauseLock) {
                    while (mPause) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Update the frame
                if (!paused) {
                    update();
                }
                // Draw the frame
                draw();
            }
        }

        /**
         * Update Movement, collision detection
         */
        //
        public void update() {
            // Move the paddle if required
            paddle.update(fps);
            ball.update(fps);
            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (intersects(bricks[i].getBounds(), ball)) {
                        if (bricks[i].getType() == BrickType.ORANGE && paddle.getWidth() > Constants.PADDLE_MIN_WIDTH) {
                            paddle.setWidth(paddle.getWidth() - 20);
                            if (!speedDoubled && ballSpeedSeekbar != null && (fps < Constants.FPS_DEFAULT + 15)) {
                                int ballSpeed = ((int) fps + 15);
                                ballSpeedSeekbar.setProgress(ballSpeed);
                                fps = fps + 15;
                                ball.update(fps);
                                speedDoubled = true;
                            }
                        }
                        bricks[i].decrementHits();
                        score += (bricks[i].getType()).getBrickValue();
                        if (bricks[i].getHits() == 0) {
                            bricks[i].setInvisible();
                        }
                        ball.reverseYVelocity();
                        if (sound_on)
                            soundPool.play(hit_brick, 1, 1, 0, 0, 1);
                    }
                }
            }
            // Check for ball colliding with paddle
            if (intersects(paddle.getBounds(), ball)) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getBounds().top - ball.getRadius() - 2);
                if (sound_on)
                    soundPool.play(hit_paddle, 1, 1, 0, 0, 1);
            }
            // Bounce the ball back when it hits the bottom of screen
            if (ball.getY() + ball.getRadius() > screenY - 200) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - ball.getRadius() - 2);
                if (sound_on)
                    soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                // Lose a life
                lives--;
                if (lives == 0) {
                    paused = true;
                    Intent intent = new Intent(getApplicationContext(), SubmitScoreActivity.class);
                    Score scoreExtra = new Score(df.format(new Date()), new Integer(score));
                    intent.putExtra("gameScoreObject", scoreExtra);
                    intent.putExtra("isWon", false);
                    if (sound_on)
                        soundPool.play(game_completed, 1, 1, 0, 0, 1);
                    createBricksAndRestart();
                    startActivity(intent);
                } else {
                    resetBallAndPaddle();
                }
            }
            // Bounce the ball back when it hits the top of screen
            if (ball.getY() - ball.getRadius() < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(ball.getRadius() + 12);
                if (sound_on)
                    soundPool.play(hit_wall, 1, 1, 0, 0, 1);
            }
            // If the ball hits left wall bounce
            if (ball.getX() - ball.getRadius() < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(ball.getRadius() + 2);
            }
            // If the ball hits right wall bounce
            if (ball.getX() + ball.getRadius() > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - ball.getRadius() - 22);
            }
            // Pause if cleared screen
            if (score == maxScore) {
                paused = true;
                Intent intent = new Intent(getApplicationContext(), SubmitScoreActivity.class);
                score = (lives < Constants.TOTAL_LIVES) ? score - ((Constants.TOTAL_LIVES - lives) * 5) : score;
                Score scoreExtra = new Score(df.format(new Date()), new Integer(score));
                intent.putExtra("gameScoreObject", scoreExtra);
                intent.putExtra("isWon", true);
                if (sound_on)
                    soundPool.play(game_completed, 1, 1, 0, 0, 1);
                createBricksAndRestart();
                startActivity(intent);
            }
        }

        /**
         * Draw the newly updated scene (Ball, paddle, bricks etc.)
         */
        public void draw() {
            // Make sure drawing surface is valid or it crashes
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();
                // Draw the background color
                canvas.drawColor(Color.BLACK);
                // Draw bg
                Drawable bgDrawable = getResources().getDrawable(bgImage);
                // left, top, right, bottom
                bgDrawable.setBounds(0, 0, screenY, screenX + 150);
                bgDrawable.draw(canvas);
                // Choose the brush color for drawing
                paint.setColor(Color.LTGRAY);
                paint.setAntiAlias(true);
                // Draw the paddle
                canvas.drawRect(paddle.getBounds(), paint);
                // Choose the brush color for drawing
                paint.setColor(Color.LTGRAY);
                // Draw the ball
                canvas.drawCircle(ball.centerX, ball.centerY, ball.radius, paint);
                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                // Draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        paint.setColor(Color.parseColor(bricks[i].getType().getColorValue()));
                        canvas.drawRect(bricks[i].getBounds(), paint);
                    }
                }
                // Set brush for drawing text
                Paint textPaint = new Paint();
                textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                textPaint.setColor(Color.argb(255, 255, 255, 255));
                textPaint.setTextSize(30);
                canvas.drawText("SCORE " + score, 0, screenY - 150, textPaint);
                canvas.drawText("BEST SCORE " + Helper.getBestScore(), screenX / 2 - 130, screenY - 150, textPaint);
                canvas.drawText("LIVES " + lives, screenX - 110, screenY - 150, textPaint);
                // Has the player cleared the screen?
                if (score == maxScore) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }
                // Has the player lost?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU LOST!", 10, screenY / 2, paint);
                }
                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * Ball intersects brick or not
         *
         * @param brick the brick
         * @param ball  the ball
         * @return the boolean
         */
        public boolean intersects(RectF brick, Ball ball) {
            // grow the rectangle by the circle radius and check if the center of the circle is contained by the rectangle
            // Copy rect in another RectF instance if you need it later
            RectF tempBrick = new RectF(brick);
            tempBrick.inset(-ball.getRadius(), -ball.getRadius());
            if (tempBrick.contains(ball.centerX, ball.centerY)) {
                // Intersection
                return true;
            }
            return false;
        }

        /**
         * Pause. when game is paused/stopped by the user, lock the game thread
         */
        public void pause() {
            //Unregistering the sensor manager
            sensorMgr.unregisterListener(this);
            mPause = true;
        }

        /**
         * Resume.
         */
        public void resume() {
            //Registering the sensor manager
            sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            playing = true;
            if (!isGameThreadStarted) {
                isGameThreadStarted = true;
                Thread gameThread = new Thread(this);
                gameThread.start();
            } else {
                synchronized (pauseLock) {
                    mPause = false;

                    pauseLock.notifyAll();
                }
            }
        }

        /**
         * Action on detecting screen touch
         *
         * @param motionEvent
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    initialTouch = motionEvent.getX();
                    break;
                //Player is moving the finger on the screen
                case MotionEvent.ACTION_MOVE:
                    float x = motionEvent.getX();
                    if (x - initialTouch > 0)
                        paddle.setMovementState(paddle.RIGHT);
                    else if (x - initialTouch < 0)
                        paddle.setMovementState(paddle.LEFT);
                    else {
                        if (x - paddle.getBounds().left <= 0)
                            paddle.setMovementState(paddle.LEFT);
                        if (x - paddle.getBounds().right >= 0)
                            paddle.setMovementState(paddle.RIGHT);
                    }
                    break;
                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

        /**
         * makes the ball round
         *
         * @param Rval the rval
         * @param Rpl  the rpl
         * @return the float
         */
        public float Round(float Rval, int Rpl) {
            float p = (float) Math.pow(10, Rpl);
            Rval = Rval * p;
            float tmp = Math.round(Rval);
            return (float) tmp / p;
        }

        /**
         * event driven callback invoked on device shift left/right and on light change
         *
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long timeNow = System.currentTimeMillis();
                // only allow update once in every 100ms.
                if ((timeNow - lastBallSpeedUpdatedAt) > 100) {
                    lastBallSpeedUpdatedAt = timeNow;
                    float x = event.values[SensorManager.DATA_X];
                    float prevVelocity = ball.xVelocity;
                    if (Round(x, 4) > 5.0000) {
                        //Shake to left
                        ball.xVelocity -= 150;
                    } else if (Round(x, 4) < -5.0000) {
                        //Shake to right
                        ball.xVelocity += 150;
                    } else {
                        ball.xVelocity = prevVelocity;
                    }
                }
            } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                if (event.values[0] < Constants.DAYLIGHT_THRESHOLD && bgImage == R.drawable.clouds) {
                    // Change bgImage to spooky
                    bgImage = R.drawable.creepy_trees;
                } else if (event.values[0] > Constants.DAYLIGHT_THRESHOLD && bgImage == R.drawable.creepy_trees) {
                    // change bgImage to daylight
                    bgImage = R.drawable.clouds;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Purposely left unimplemented
        }
    }
    // End of BreakoutView inner class

    /**
     * on Resume of MainActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
        sound_toggle.setChecked(bundle.getBoolean("VolumeToggleButtonState", false));

    }

    /**
     * On pause of MainActivity
     */
    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
        bundle.putBoolean("VolumeToggleButtonState", sound_on);
    }

    /**
     * Toggle sound.
     *
     * @param view the view
     */
    public void toggleSound(View view) {
        sound_on = sound_toggle.isChecked();
        if (sound_on == true) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sound ON",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Sound OFF",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    /**
     * on click of scores image
     *
     * @return
     */
    private View.OnClickListener createScoresImageClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakoutView.pause();
                startActivity(new Intent(getApplicationContext(), DisplayScoresActivity.class));
            }
        };
    }

    /**
     * on click of scores image
     *
     * @return
     */
    private View.OnClickListener createAboutImageClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakoutView.pause();
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        };
    }

    /**
     * seekbar instance
     *
     * @return
     */
    private SeekBar.OnSeekBarChangeListener createSeekListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            int seekValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekValue = progress;
                fps = (99 - seekValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                fps = (99 - seekValue);
                Toast toast = Toast.makeText(getApplicationContext(), "Ball speed set to:" + seekValue,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        };
    }

    /**
     * This method checks if the user has given access to access the storage media of the phone.
     * If not, it asks the user to give permission to access the storage media
     * and hence Contacts text file can be created and accessed.
     *
     * @author Vincy Shrine
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkStorageAccessPermissions() {
        int hasWriteScoresPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        if (hasWriteScoresPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTCODE_ASK_PERMISSIONS);
            return;
        } else {
            Helper.setScoresFile(getApplicationContext());
        }
    }

    /**
     * This method is called once user accepts or denies to give storage permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @author Vincy Shrine
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Helper.setScoresFile(getApplicationContext());
                } else {
                    // Permission Denied
                    Toast.makeText(this, "STORAGE_ACCESS Denied. Please restart the app and allow access!!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * This is the last called method of the activity
     */
    @Override
    protected void onStop() {
        breakoutView.pause();
        Helper.writeScoresToFile();
        super.onStop();
    }

    /**
     * When user presses back button on main game screen
     */
    @Override
    public void onBackPressed() {
        exitAction();
    }

    /**
     * Exit action on user pressing back button
     */
    private void exitAction() {
        breakoutView.pause();
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to quit this game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}