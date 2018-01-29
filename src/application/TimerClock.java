package application;

import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class TimerClock {
    AnimationTimer animateTimer;
    long startTime;
    public TimerClock(Label errorLabel) {
         startTime = System.currentTimeMillis();
         animateTimer = new AnimationTimer() {
             @Override
             public void handle(long now) {
                 long currentTime = System.currentTimeMillis();
                 long millis = currentTime - startTime;
                 errorLabel.setTextFill(Color.DARKORANGE);
                 errorLabel.setText("Generuoja...   " + formattedTime(millis));
             }
         };
     }
    public void start() {
        animateTimer.start();
    }
    
    public void stop() {
        animateTimer.stop();
    }
    
    public void successMessage(Label errorLabel) {
        long currentTime = System.currentTimeMillis();
        long millis = currentTime - startTime;
        errorLabel.setTextFill(Color.GREEN);
        if (millis < 2 * 60 * 1000 ) {
            errorLabel.setText("Sugeneravo per: " + formattedTime(millis) + "!");
        } else {
            errorLabel.setText("Sugeneravo per: " + formattedTime(millis));
        }
        
    }
    
    private String formattedTime(long millis) {
        String formattedTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return formattedTime;
    }
    
 }
