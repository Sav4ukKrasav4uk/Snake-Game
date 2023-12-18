//set PATH_TO_FX="C:\javafx-sdk-17.0.9\lib"
//javac --module-path %PATH_TO_FX% --add-modules javafx.controls SnakeGame.java
//java --module-path %PATH_TO_FX% --add-modules javafx.controls SnakeGame

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

class Position{
    private int x;
    private int y;

    public Position(){
        this.x = SnakeGame.rows;
        this.y = SnakeGame.columns;
    }

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setX(int x){
        this.x = (x + SnakeGame.rows) % SnakeGame.rows;
    }
    public void setY(int y){
        this.y = (y + SnakeGame.columns) % SnakeGame.columns;
    }
}

public class SnakeGame extends Application{
    private final int windowSize = 630;
    static final int rows = 15;
    static final int columns = 15;
    private final int block = windowSize / rows;

    private int appleX;
    private int appleY;
    private boolean generateAccess = true;
    // W - 1, A - 2, S - 3, D - 4
    private int direction = 4;
    private int score = 0;
    private int speed = 18;
    private int highestScore = 0;

    private final ArrayList<Position> snake = new ArrayList<>();
    private AnimationTimer animation;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake Game");
        primaryStage.setResizable(false);
        Group root = new Group();
        Canvas canvas = new Canvas(windowSize, windowSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Button button = new Button();
        button.setText("RESTART");
        root.getChildren().add(canvas);
        root.getChildren().add(button);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        button.setOnAction(e -> restart(gc));
        primaryStage.show();

        scene.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            if (code == KeyCode.W && direction != 3){
                direction = 1;
            }
            else if (code == KeyCode.A && direction != 4){
                direction = 2;
            }
            else if (code == KeyCode.S && direction != 1){
                direction = 3;
            }
            else if (code == KeyCode.D && direction != 2){
                direction = 4;
            }
            //System.out.println(code.toString());
        });

        startNewGame(gc);
    }

    private void restart(GraphicsContext gc){
        snake.clear();
        direction = 4;
        score = 0;
        speed = 18;
        generateAccess = true;
        animation.stop();
        startNewGame(gc);
    }

    private void startNewGame(GraphicsContext gc){
        for (int i = 2; i >= 0; i--){ // 3
            snake.add(new Position(i, 7));
        }

        animation = new AnimationTimer(){
            long last = 0;
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    initGame(gc);
                    return;
                }

                if (now - last > Integer.MAX_VALUE / speed) {
                    last = now;
                    initGame(gc);
                }
            }
        };
        animation.start();
    }

    private void initGame(GraphicsContext gc){
        if (gameOver(gc)){
            return;
        }

        drawBackground(gc);
        drawSnake(gc);
        drawApple(gc);
        showScore(gc);

        for (int i = snake.size() - 1; i >= 1; i--){
            snake.get(i).setX(snake.get(i - 1).getX());
            snake.get(i).setY(snake.get(i - 1).getY());
        }

        switch (direction) {
            case 1 -> moveUp();
            case 2 -> moveLeft();
            case 3 -> moveDown();
            case 4 -> moveRight();
        }

        eatApple();
    }

    private void showScore(GraphicsContext gc){
        gc.setFill(Color.web("252A34"));
        gc.setFont(new Font("Consolas", 30));
        gc.fillText("SCORE: " + score, (double) windowSize / 2 - 60, 30);
        gc.fillText("HIGH: " + highestScore, (double) windowSize / 2 + 170, 30);
    }

    private void drawApple(GraphicsContext gc){
        if (generateAccess){
            appleX = new Random().nextInt(rows);
            appleY = new Random().nextInt(columns);

            for (int i = 0; i < snake.size(); i++){
                if (appleX == snake.get(i).getX() && appleY == snake.get(i).getY()){
                    drawApple(gc);
                    break;
                }
            }

            generateAccess = false;
        }

        gc.setFill(Color.web("FF2400"));
        gc.fillOval(appleX * block, appleY * block, block, block);
    }

    private void drawSnake(GraphicsContext gc){
        //head
        gc.setFill(Color.web("4471E6"));
        gc.fillRoundRect(snake.get(0).getX() * block, snake.get(0).getY() * block, block, block, 25, 25);
        //body
        gc.setFill(Color.web("5080FE"));
        for (int i = 1; i < snake.size(); i++){
            gc.fillRoundRect(snake.get(i).getX() * block, snake.get(i).getY() * block, block, block, 25, 25);
        }
    }

    private void drawBackground(GraphicsContext gc){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                if ((i + j) % 2 == 0){
                    gc.setFill(Color.web("8ECC39")); // dark
                }
                else {
                    gc.setFill(Color.web("A8D949")); // light
                }
                gc.fillRect(i * block, j * block, block, block);
            }
        }
    }

    private void eatApple(){
        if (snake.get(0).getX() == appleX && snake.get(0).getY() == appleY){
            snake.add(new Position());
            generateAccess = true;
            score++;
            highestScore = Math.max(highestScore, score);
            speed++;
        }
    }

    private void moveUp(){
        snake.get(0).setY(snake.get(0).getY() - 1);
    }
    private void moveLeft(){
        snake.get(0).setX(snake.get(0).getX() - 1);
    }
    private void moveDown(){
        snake.get(0).setY(snake.get(0).getY() + 1);
    }
    private void moveRight(){
        snake.get(0).setX(snake.get(0).getX() + 1);
    }

    private boolean gameOver(GraphicsContext gc){
        for (int i = 1; i < snake.size(); i++){
            if (snake.get(0).getX() == snake.get(i).getX() && snake.get(0).getY() == snake.get(i).getY()){
                gc.setFill(Color.web("750E21"));
                gc.setFont(new Font("Consolas.bold", 60));
                gc.fillText("GAME OVER", (double)windowSize / 2 - 150, (double)windowSize / 2);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args){
        launch(args);
    }
}