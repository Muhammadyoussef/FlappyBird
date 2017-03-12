import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.cs.flappybird.FlappyBird;

public class Bird {

    //Variables
    SpriteBatch batch;
    private Texture[] birds;
    private int birdXCoordinate;
    private int birdYCoordinate;
    private int birdState;
    private final int wingsUp = 0;
    private final int wingsDown = 1;
    private int birdVelocity;
    private final int deltaBirdVelocity = -25;
    private Circle birdCircle;

    //constructors
    public Bird(SpriteBatch batch, Texture[]birds){
        this.batch = batch;
        this.birds = birds;
    }

    //Methods
    public void setBirdCoordinates(int state){

        birdXCoordinate = (FlappyBird.getScreenWidth()/2)  - (birds[state].getWidth()/2);
        birdYCoordinate = (FlappyBird.getScreenHeight()/2) - (birds[state].getHeight()/2);
    }
    public void drawBird(){
        //Draw the "Bird Object" to be in the center of the screen
        batch.draw(birds[birdState], birdXCoordinate, birdYCoordinate);
        FlyingAnimation();
        //render bird shape (Circle) for collision detection
        createBirdShape();
    }
    public void FlyingAnimation() {

        if (birdState == wingsUp){
            birdState = wingsDown;
        }else {
            birdState = wingsUp;
        }
    }
    public void createBirdShape(){

        int xCoordinate = FlappyBird.getScreenWidth()/2;
        int yCoordinate = birdYCoordinate + birds[birdState].getHeight()/2;
        int radius      = birds[birdState].getWidth()/2;

        birdCircle.set(xCoordinate, yCoordinate, radius);
    }

    //Setters
    public void setBirdCircle(Circle birdCircle) {
        this.birdCircle = birdCircle;
    }
    public void setBirdState(int birdState) {
        this.birdState = birdState;
    }
    public void setBirdVelocity(int birdVelocity) {
        this.birdVelocity = birdVelocity;
    }
    public void setBirdXCoordinate(int birdXCoordinate) {
        this.birdXCoordinate = birdXCoordinate;
    }
    public void setBirdYCoordinate(int birdYCoordinate) {
        this.birdYCoordinate = birdYCoordinate;
    }

    //Getters
    public Circle getBirdCircle() {
        return birdCircle;
    }
    public int getBirdState() {
        return birdState;
    }
    public int getBirdVelocity() {
        return birdVelocity;
    }
    public int getBirdXCoordinate() {
        return birdXCoordinate;
    }
    public int getBirdYCoordinate() {
        return birdYCoordinate;
    }
    public int getDeltaBirdVelocity() {
        return deltaBirdVelocity;
    }
    public int getWingsDown() {
        return wingsDown;
    }
    public int getWingsUp() {
        return wingsUp;
    }
}
