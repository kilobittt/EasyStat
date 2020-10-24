package inozemtsev.coursework.easystat.swipes;

import android.view.MotionEvent;

public abstract class SwipeDetector {

    private boolean isStarted = false;
    private float startX = 0;
    private float startY = 0;
    private int minToachLen = 10;

    public abstract void onSwipeDetected(Direction direction);

    public SwipeDetector(int minToachLen) {
        this.minToachLen = minToachLen * 5;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                startX = event.getX();
                startY = event.getY();
                isStarted = true;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                break;
            }
            case MotionEvent.ACTION_UP:{
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                if(calcDist(dx, dy) >= minToachLen){
                    onSwipeDetected(Direction.get(calcAngle(dx, dy)));
                }
                startX = startY = 0;
                isStarted = false;
                break;
            }
            default:{
                startX = startY = 0;
                isStarted = false;
            }

        }

        return false;
    }

    private int calcAngle(float dx, float dy) {
        return (int) ((Math.atan2(dy, dx) + Math.PI) * 180 / Math.PI + 180) % 360;
    }

    private double calcDist(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    public enum Direction {
        UN_EXPT,
        LEFT,
        RIGHT,
        UP,
        DOWN;

        public static Direction get (int angle){
            Direction res = UN_EXPT;
            if(angle >= 45 && angle < 135){
                res = UP;
            }

            if(angle >= 135 && angle < 225){
                res = RIGHT;
            }

            if(angle >= 225 && angle < 315){
                res = DOWN;
            }

            if(angle >= 315 && angle <= 360
                    || angle >= 0 && angle < 45){
                res = LEFT;
            }
            return res;
        }
    }
}
