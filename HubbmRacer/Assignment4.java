import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class Assignment4 extends Application {
    private Pane root=new Pane();
    private double maxVelocity=1;
    private int score=0;
    private int Level=1;
    private Text scoreText=new Text();
    private double currentX=240;
    private int LastX=300;
    private int initialY=-2100;
    private Car[] cars=new Car[20];
    private Bush[] bushes=new Bush[20];
    private Car my_car=new Car(315,600,70,115,false,Color.RED);
    private double roadY=-900000;
    private boolean goRight,goLeft,goUp;
    private double delta=3.6;
    private double velocity=0;
    private Random rn=new Random();
    Scene scene=new Scene(createContent());

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("\t\tHUBBM-RACER");
        try {
            startGame(primaryStage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    public void startGame(Stage stage){

        scene.setFill(Color.DARKGRAY);
        SideLine right=new SideLine(0,0,100,800);
        SideLine left=new SideLine(500,0,100,800);
        Line roadline = new Line(300,roadY, 240, 800);
        roadline.setFill(Color.BLACK);
        roadline.setStrokeWidth(10);
        roadline.getStrokeDashArray().addAll(20d, 30d);
        for (int i = 0; i <20 ; i++) {
            cars[i]=new Car(randomX(),initialY,70,100,false,Color.YELLOW);
            cars[i].setFill(Color.YELLOW);
            bushes[i]=new Bush(randomBush(),initialY,20,Color.DARKGREEN);
            initialY+=125;
        }
        initialY=-1720;
        my_car.setFill(Color.RED);
        root.getChildren().addAll(roadline);
        root.getChildren().addAll(cars);

        root.getChildren().addAll(right,left,my_car);
        root.getChildren().addAll(bushes);
        scorBox();
        Level();
        velocityControl();
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long now) {


                for (Car temp:cars

                     ) {
                    rescore(scoreText);

                    if (temp.getY()>600){
                        if (temp.getY()<=600+velocity){
                            score+=Level;
                            Level();
                            velocityControl();
                        }
                        temp.setFill(Color.GREEN);
                    }

                    if (my_car.getBoundsInParent().intersects(temp.getBoundsInParent())){

                        my_car.setFill(Color.BLACK);
                        temp.setFill(Color.BLACK);
                        this.stop();
                        goRight=false;
                        goLeft=false;
                        stopGame(stage);

                    }
                    if (temp.getY()>800){
                        temp.setFill(Color.YELLOW);
                        temp.setY(initialY);
                        temp.setX(randomX());


                    }
                    temp.move(velocity);

                }

                for (Bush temp2:bushes
                     ) {
                    temp2.moveBush(velocity);
                    if (temp2.getCenterY()>800){
                        temp2.setCenterY(initialY);
                        temp2.setCenterX(randomBush());
                    }



                }
                roadY+=velocity;
                if (roadY>0)
                    roadY=-90000;
                roadline.relocate(roadline.getStartX(),roadY);






            }
        };
        animationTimer.start();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()){

                    case RIGHT: goRight=true;
                        break;
                    case LEFT: goLeft=true;
                        break;
                    case UP: goUp=true;
                        break;

                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                switch (keyEvent.getCode()){

                    case RIGHT: goRight=false;
                        break;
                    case LEFT: goLeft=false;
                        break;
                    case UP: goUp=false;
                        break;



                }




            }
        });

        AnimationTimer timer=new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if (goLeft){
                        currentX-=delta;
                        if (currentX<100){
                            currentX=100;
                        }
                    }

                    if (goRight) {
                        currentX+=delta;
                        if (currentX>430){
                            currentX=430;
                        }

                    }
                    if (goUp){
                        velocity+=0.5;
                        if (velocity>maxVelocity){
                            velocity=maxVelocity;
                        }
                    }
                    if (!goUp){
                        velocity-=0.1;
                        if (velocity<=0){
                            velocity=0;
                        }
                    }

                    my_car.relocate(currentX,my_car.getY());
                }
        };
        timer.start();

    }
    private static class Car extends Rectangle{
        private Color color;
        boolean dead=false;

        public Car(int x, int y, int width, int height, boolean dead,Color color) {
            super(x, y, width, height);
            this.dead = dead;
            this.setFill(color);

        }
        public void move(double delta){
            setY(getY()+delta);
        }

    }
    private static class Bush extends Circle{

        Color color;
        double centerX;
        double centerY;
        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
        double radius;

        public Bush(double centerX, double centerY, double radius, Color color) {
            super(centerX, centerY, radius, color);
            this.color = color;
            this.centerX=centerX;
            this.centerY=centerY;

        }
        public void moveBush(double delta){
           setCenterY(getCenterY()+delta);

        }
    }

    private static class SideLine extends Rectangle{

        private  double lineX;
        private double lineY;
        public SideLine( double lineX, double lineY,double width, double height) {
            super(lineX, lineY, width, height);
            this.setFill(Color.FORESTGREEN);
        }
    }
    private Parent createContent(){
        root.setPrefSize(600,800);
        return root;
    }

    public int randomX(){
        int Low=100,High=430;
        int Result;
        Result=rn.nextInt(High-Low)+Low;
        boolean bool=false;

        while (bool==false){

            if (Math.abs(Result-LastX)>=152){
                bool=true;
            }
            else{
                Result=rn.nextInt(High-Low)+Low;
            }
        }
        LastX=Result;

    return Result;
    }

    public int randomBush(){
        int Lucky=rn.nextInt(2)+1;
        int Low=20,High=80;
        int Result;
        switch (Lucky){
            case 1:
                Low=20;
                High=80;
                break;
            case 2:
                Low=520;
                High=580;
                break;
        }
        Result=rn.nextInt(High-Low)+Low;
        return Result;
    }
    public void stopGame(Stage stage){
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        Text t = new Text();
        t.setEffect(ds);
        t.setCache(true);
        t.setX(60.0f);
        t.setY(200.0f);
        t.setFill(Color.RED);
        t.setText("\t GAME OVER!\n\tYour Score: "+score+"\nPress ENTER to restart!");
        t.setFont(Font.font("veranda", FontWeight.EXTRA_BOLD,FontPosture.ITALIC, 45));
        root.getChildren().addAll(t);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()){
                    case ENTER:
                        Platform.runLater( () -> new Assignment4().start( stage) );
                        break;




                }
            }
        });
    }
    public void scorBox(){

        scoreText.setX(10);
        scoreText.setY(20);
        scoreText.setFont(Font.font("veranda",FontWeight.NORMAL, FontPosture.ITALIC,20));
        root.getChildren().addAll(scoreText);
    }
    public void rescore(Text text){
        text.setText("Score: "+score+"\nLevel: "+Level);
    }
    public void velocityControl(){
        switch (Level){
            case 1:
                maxVelocity=6;
                break;
            case 2:
                maxVelocity=6.5;
                break;
            case 3:
                maxVelocity=7;
                break;
            case 4:
                maxVelocity=7.5;
                break;
            case 5:
                maxVelocity=8;
                break;
            case 6:
                maxVelocity=8.5;
                break;
            case 7:
                maxVelocity=9;
                break;
            case 8:
                maxVelocity=9.25;
                break;
            case 9:maxVelocity=9.75;
                break;
            case 10:
                maxVelocity=10.25;
                break;
            case 11:
                maxVelocity=11;
                break;
            case 12:
                maxVelocity=11.25;
                break;
            case 13:
                maxVelocity=11.5;
                break;
            case 14:
                maxVelocity=11.75;
                break;
            case 15:
                maxVelocity=12;
                break;
        }



    }
    public void Level(){
        if (score<8){
            Level=1;
        }
        else if (score>=8 && score<22){
            Level=2;
        }
        else if (score>=22 && score<56){
            Level=3;
        }
        else if (score>=56 &&score<94){
            Level=4;
        }
        else if (score>=94&& score<144){
            Level=5;
        }
        else if (score>=144 && score<204){
            Level=6;
        }
        else if (score>=204 && score<274){
            Level=7;
        }
        else if (score>=274 && score<354){
            Level=8;
        }
        else if (score>=354 &&score<444){
            Level=9;
        }
        else if (score>=444 && score<644){
            Level=10;
        }
        else if (score>=644 && score<866){
            Level=11;
        }
        else if (score>=866 &&score<1010){
            Level=12;
        }
        else if (score>=1010 && score<1348){
            Level=13;
        }
        else if (score>=1348&& score<1700){
            Level=14;
        }
        else {
            Level=15;
        }
    }
}
