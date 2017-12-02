package cisgvsu.biotowerdefense;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameSurfaceView extends SurfaceView {

    DrawingThread thread;
    Context context;
    private Game game;

    public GameSurfaceView (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public GameSurfaceView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Find the screen size
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //this.screenHeight = size.y;
        //this.screenWidth = size.x;

        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // Get the bitmaps that we'll draw
        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        Bitmap pill = BitmapFactory.decodeResource(getResources(), R.drawable.pill);

        // Scale the background
        bg = Bitmap.createScaledBitmap(bg, screenWidth, screenHeight, false);

        thread = new DrawingThread(getHolder(), bg, pill, screenWidth, screenHeight);

        // Set up SurfaceHolder for drawing
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                thread.setRunnable(false);
                while(true) {
                    try {
                        thread.join();
                    } catch(InterruptedException ie) {
                        //Try again and again and again
                    }
                    break;
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                thread.setRunnable(true);
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
        });
    }

    public void setGame(Game g) {
        this.game = g;
        this.thread.setGame(g);
    }

    class DrawingThread extends Thread {
        private SurfaceHolder holder;
        private Canvas canvas;
        private boolean run = false;
        private Bitmap bg;
        private Bitmap pillBmp;
        private Bitmap staphBmp;
        private Bitmap strepBmp;
        private Bitmap pneumoniaBmp;
        private int width;
        private int height;
        private Game game;

        // Variables for displaying score and money
        private Paint paintText;
        private int renderedScore;
        private String renderedScoreString;
        private int renderedMoney;
        private String renderedMoneyString;

        public DrawingThread(SurfaceHolder holder, Bitmap bg, Bitmap pillBmp, int width, int height) {
            this.paintText = new Paint();
            paintText.setTextSize(50);
            paintText.setColor(Color.DKGRAY);
            paintText.setTextAlign(Paint.Align.CENTER);

            this.holder = holder;
            this.bg = bg;
            this.pillBmp = pillBmp;
            this.width = width;
            this.height = height;

            staphBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bacteria_staph);
            strepBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bacteria_strep);
            pneumoniaBmp = BitmapFactory.decodeResource(getResources(), R.drawable.bacteria_pneumonia);

        }

        public void setRunnable(boolean run) {
            this.run = run;
        }

        public void setGame(Game g) {
            this.game = g;
        }

        private Bitmap getBmp(BacteriaType type) {
            switch (type) {
                case staph:
                    return staphBmp;
                case strep:
                    return strepBmp;
                case pneumonia:
                    return pneumoniaBmp;
                default:
                    return null;
            }
        }

        @Override
        public void run() {
            while (run) {
                canvas = null;
                try {
                    canvas = holder.lockCanvas(null);

                    synchronized (holder) {
                        draw(canvas);
                    }

                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        /**
         * Draw the background and the target.
         * @param canvas
         */
        public void draw(Canvas canvas) {
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                //Draw background
                canvas.drawBitmap(bg, 0, 0, null);

                //Locate and draw target
                if (this.game != null) {
                    CopyOnWriteArrayList<Bacteria> allBacteria = game.getAllBacteria();
                    for (Bacteria bac : allBacteria) {
                        if (!bac.isInitialPositionSet()) {
                            bac.setX(this.width + 10);
                            bac.setY(300);
                            bac.setInitialPositionSet(true);
                        }
                        canvas.drawBitmap(getBmp(bac.getType()), bac.getX(), bac.getY(), null);
                        if (!game.isPaused()) {
                            moveBacteria(bac);
                            game.checkForLoss();
                        }
                    }
                    //Log.d("BAC", "" + allBacteria.size());
                }

                //Update current pill positions
                for (Pill pill : game.getPills()) {
                    canvas.drawBitmap(pillBmp, pill.getX(), pill.getY(), null);
                    movePill(pill);
                }

                if (this.game != null && !game.towers.isEmpty()) {
                    //Draw new pill for each tower
                    for (AntibioticTower tower : game.towers) {
                        if(tower != null && tower.getShooting() && tower.addPill()) {
                            tower.setAddPill(false);
                            Pill pill = null;
                            switch (tower.getLocation()) {
                                //TODO EK: Start pill placement better
                                case 0:
                                    pill = new Pill(1500, 200, game.bacteriaToTower.get(tower).peek());
                                    break;
                                case 1:
                                    pill = new Pill(1000, 200, game.bacteriaToTower.get(tower).peek());
                                    break;
                                case 2:
                                    pill = new Pill(1000, 450, game.bacteriaToTower.get(tower).peek());
                                    break;
                                case 3:
                                    pill = new Pill(550, 450, game.bacteriaToTower.get(tower).peek());
                                    break;
                                case 4:
                                    pill = new Pill(300, 450, game.bacteriaToTower.get(tower).peek());
                                    break;
                            }
                            if (pill != null) {
                                CopyOnWriteArrayList<Pill> pills = game.getPills();
                                pills.add(pill);
                                game.setPills(pills);
                                canvas.drawBitmap(pillBmp, pill.getX(), pill.getY(), null);
                            }
                        }
                    }
                }

                canvas.drawText(getScoreString(), 150, 100, paintText);
                canvas.drawText(getMoneyString(), 500, 100, paintText);
                canvas.drawText(game.getResistanceString(), canvas.getWidth()/2, canvas.getHeight() - 50, paintText);
            }
        }

        public void moveBacteria(Bacteria bacteria) {
            if (bacteria.getX() > -100) {
                if ((bacteria.getX() > 925 && bacteria.getY() < 375) || bacteria.getY() > 750) {
                    bacteria.setX(bacteria.getX() - 5);
                } else {
                    bacteria.setY(bacteria.getY() + 5);
                }
            } else {
                bacteria.setOnScreen(false);
            }
        }

        private void movePill(Pill pill) {
            if (pill.getTargetBacteria() == null || !pill.getTargetBacteria().isOnScreen()) {
                //remove pill
                CopyOnWriteArrayList<Pill> pills = game.getPills();
                pills.remove(pill);
                game.setPills(pills);
            } else {
                pill.updatePosition();
            }
        }

        private String getScoreString() {
            //Only create new score string for new scores to help with garbage collector problems
            if (game.getScore() != this.renderedScore || this.renderedScoreString == null) {
                this.renderedScore = game.getScore();
                this.renderedScoreString = "Score: " + game.getScore();
            }
            return renderedScoreString;
        }

        private String getMoneyString() {
            //Only create new score string for new scores to help with garbage collector problems
            if (game.getMoney() != this.renderedMoney || this.renderedMoneyString == null) {
                this.renderedMoney = game.getMoney();
                this.renderedMoneyString = "Money: " + game.getMoney();
            }
            return renderedMoneyString;
        }
    }
}