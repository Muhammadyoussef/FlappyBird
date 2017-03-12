package com.cs.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import java.util.Random;

import javax.naming.Context;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch; //Draws batched quads using indices.

	//Textures
	private Texture startButton;
	private Texture background;
	private Texture topTube;
	private Texture bottomTube;
	private Texture[] birds;
	private Texture gameOverTexture;

	//Bird Variables
	private int birdXCoordinate;
	private int birdYCoordinate;
	private int birdState;
	private final int wingsUp = 0;
	private final int wingsDown = 1;
	private int birdVelocity;
	private final int deltaBirdVelocity = -25;
	private Circle birdCircle;

	//Tubes Variables
	private final int numberOfTubes = 4;
	private float[] tubesXCoordinate = new float[numberOfTubes];
	private float topTubeYCoordinate;
	private float bottomTubeYCoordinate;
	private float[] tubesOffset = new float[numberOfTubes];
	private float maxTubeOffset;
	private final float tubesVelocity = 6;
	private final float tubesGap = 500;
	private float distanceBetweenTubes;
	private Rectangle[] topTubeRectangles;
	private Rectangle[] bottomTubeRectangles;
	private int scoringTube;

	//Font
	private BitmapFont font;
	private final int fontSize = 10;
	private int fontXCoordinates;
	private int fontYCoordinates;

	//Game states
	private int gameState = 0;
	private final int initialScreen = 0;
	private final int playingMode  = 1;
	private final int gameOver = 2;
	
	//Other Variables
	private static int screenWidth;   public static int getScreenHeight() {

		return screenHeight;
	}
	private static int screenHeight;  public static int getScreenWidth() {

		return screenWidth;
	}
	private final int gravity = 2;
	private Random randomGenerator;
	private static int score;
	private int replayCounter = 1;

	//onCreate-Like method
	@Override
	public void create () {

		//Initializing SpriteBatch
		batch = new SpriteBatch();

		//Initializing Textures
		startButton = new Texture("start.png");
		background  = new Texture("bg.png");
		topTube		= new Texture("toptube.png");
		bottomTube  = new Texture("bottomtube.png");
		birds       = new Texture[2];
		birds[0]    = new Texture("bird.png");
		birds[1]	= new Texture("bird2.png");
		gameOverTexture = new Texture("game_over.png");

		//Initializing Variables
		screenWidth  = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		randomGenerator = new Random();
		distanceBetweenTubes = screenWidth/2 + 100;
		maxTubeOffset = screenHeight/2 - tubesGap/2 - 100;

		//Initializing coordinates
		setBirdCoordinates(birdState);
		setTubesXCoordinate();
		setTobTubeYCoordinates();
		setBottomTubeYCoordinates();

		//Initializing Shapes
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		//Initializing Font
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(fontSize);
		fontXCoordinates = screenWidth/2 - 50;
		fontYCoordinates = screenHeight - 100;

	}

	//responsible for rendering every th52ing on the screen (Will be called continuously)
	@Override
	public void render () {

		batch.begin();

		//Render the background
		drawBackground();

		if (gameState == initialScreen){

			drawStartButton();
			drawBird();

			// to kick start the game
			if (Gdx.input.justTouched()){
				gameState = playingMode;
				birdVelocity = deltaBirdVelocity;
			}

		//if the user started to play
		} else if (gameState == playingMode) {

			//will get called every time the screen it tapped
			if (Gdx.input.justTouched()){

				birdVelocity = deltaBirdVelocity;
			}

			drawTubes();
			drawBird();
			drawScore();

			checkForCollision();

		} else if (gameState == gameOver){

			//Stop everything and show game over message
			drawGameOver();

			// to kick start the game again
			if (Gdx.input.justTouched()){

				if(replayCounter < 0)
				{
					resetTheGame();
					gameState = playingMode;
					birdVelocity = deltaBirdVelocity;
				}
				replayCounter--;
			}
		}


		batch.end();
	}

	//Bird, topTube and bottomTube Coordinates
	private void setBirdCoordinates(int state){

		birdXCoordinate = (screenWidth/2)  - (birds[state].getWidth()/2);
		birdYCoordinate = (screenHeight/2) - (birds[state].getHeight()/2);
	}
	private void setTubesXCoordinate(){

		for (int i = 0; i < numberOfTubes; i++){

			tubesOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (screenHeight - tubesGap - 200);
			tubesXCoordinate[i] = (screenWidth/2)  - (topTube.getWidth()/2) + screenWidth + (i * distanceBetweenTubes);
		}
	}
	private void setTobTubeYCoordinates(){

		topTubeYCoordinate = (screenHeight/2) + (tubesGap/2);
	}
	private void setBottomTubeYCoordinates(){

		bottomTubeYCoordinate = (screenHeight/2) - (tubesGap/2) - (bottomTube.getHeight());
	}

	//Draw every thing on screen
	private void drawStartButton(){

		int xCoordinate = screenWidth/2  - startButton.getWidth()/2;
		int yCoordinate = screenHeight/2 - startButton.getHeight()/2 - startButton.getHeight() *2;
		batch.draw(startButton, xCoordinate,yCoordinate);
	}
	private void drawBackground(){

		//Draw the background to fill the entire screen
		batch.draw(background, 0, 0, screenWidth, screenHeight);
	}
	private void drawTubes() {

		for (int i = 0; i < numberOfTubes; i++) {

			if (tubesXCoordinate[i] < -topTube.getWidth()) {

				tubesXCoordinate[i] += numberOfTubes * distanceBetweenTubes;
				tubesOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (screenHeight - tubesGap - maxTubeOffset);

			} else {

				tubesXCoordinate[i] = tubesXCoordinate[i] - tubesVelocity;
			}
			batch.draw(topTube, tubesXCoordinate[i], topTubeYCoordinate + tubesOffset[i]);
			batch.draw(bottomTube, tubesXCoordinate[i], bottomTubeYCoordinate + tubesOffset[i]);

			//render Tube shape (Rectangle) for collision detection
			createTubeShape(i);

		}
	}
	private void drawBird(){
		//Draw the "Bird Object" to be in the center of the screen
		batch.draw(birds[birdState], birdXCoordinate, birdYCoordinate);
		FlyingAnimation();
		//render bird shape (Circle) for collision detection
		createBirdShape();
	}
	private void drawScore() {

		validateScore();
		font.draw(batch, String.valueOf(score),fontXCoordinates, fontYCoordinates);
	}
	private void drawGameOver(){

		int gameOverXCoordinate = screenWidth/2  - gameOverTexture.getWidth()/2;
		int gameOverYCoordinate = screenHeight/2 - gameOverTexture.getHeight()/2;
		int scoreXCoordinate	= screenWidth/2  - font.getRegion().getRegionWidth()/2 - 100;
		int scoreYCoordinate	= screenHeight/2 + gameOverTexture.getHeight() + 50;

		batch.draw(gameOverTexture, gameOverXCoordinate, gameOverYCoordinate);
		font.draw(batch, "Score "+String.valueOf(score),scoreXCoordinate, scoreYCoordinate);
		drawStartButton();
	}

	//bird flying animation & movement
	private void FlyingAnimation() {

		if (birdState == wingsUp){
			birdState = wingsDown;
		}else {
			birdState = wingsUp;
		}
	}
	private void play() {
		//increase velocity and decrease the yCoordinate
		birdVelocity += gravity;
		birdYCoordinate -= birdVelocity;
	}

	//create shapes (Circles & rectangles) for collision detection
	private void createBirdShape(){

		int xCoordinate = screenWidth/2;
		int yCoordinate = birdYCoordinate + birds[birdState].getHeight()/2;
		int radius      = birds[birdState].getWidth()/2;

		birdCircle.set(xCoordinate, yCoordinate, radius);
	}
	private void createTubeShape(int i){

		topTubeRectangles[i] = new Rectangle(tubesXCoordinate[i],topTubeYCoordinate + tubesOffset[i]
				,topTube.getWidth(),topTube.getHeight());

		bottomTubeRectangles[i] = new Rectangle(tubesXCoordinate[i], bottomTubeYCoordinate + tubesOffset[i]
				,bottomTube.getWidth(),bottomTube.getHeight());
	}
	private void checkForCollision(){

		if(birdYCoordinate > 0){
			//Game play movement
			play();

		}else {
			gameState = gameOver;
		}

		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i])
					|| Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

				gameState = gameOver;

			}
		}
	}

	//keep track of score
	private void validateScore() {

		if (tubesXCoordinate[scoringTube] < screenWidth/2 ){

			score++;
			Gdx.app.log("Score: ", String.valueOf(score));

			if (scoringTube < numberOfTubes - 1){

				scoringTube++;
			}else {

				scoringTube = 0;
			}
		}
	}

	//rests the game
	private void resetTheGame() {

		//resetCoordinates
		setBirdCoordinates(birdState);
		setTubesXCoordinate();
		setTobTubeYCoordinates();
		setBottomTubeYCoordinates();

		//reset variables
		score = 0;
		scoringTube = 0;
		birdVelocity = 0;
		replayCounter = 1;

	}

}