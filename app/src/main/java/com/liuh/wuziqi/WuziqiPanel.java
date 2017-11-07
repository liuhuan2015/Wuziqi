package com.liuh.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huan on 2017/11/6 10:02.
 */

public class WuziqiPanel extends View {
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;
    private Paint mPaint = new Paint();
    //棋子
    private Bitmap mWhitePiece, mBlackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhitePointList = new ArrayList<Point>();
    private ArrayList<Point> mBlackPointList = new ArrayList<Point>();

    //游戏结束
    private boolean mIsGameOver = false;
    private boolean mIsWhiteWinner = false;

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener gameOverListener) {
        this.gameOverListener = gameOverListener;
    }

    public WuziqiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        initPaint();
    }

    private void initPaint() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);

    }

    //测量，使控件为正方形
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        //在某些情况下，widthMode或heightMode为MeasureSpec.UNSPECIFIED的时候
        //对应取到的widthSize和heightSize有可能会为0
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //没有用super，用的这个
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
        int mPieceHeight = (int) (mLineHeight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, mPieceHeight, mPieceHeight, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, mPieceHeight, mPieceHeight, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        //画棋盘
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - (lineHeight / 2));
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhitePointList.size(); i < n; i++) {
            Point whitePoint = mWhitePointList.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) * 1.0f / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) * 1.0f / 2) * mLineHeight, null);
        }
        for (int i = 0, n = mBlackPointList.size(); i < n; i++) {
            Point whitePoint = mBlackPointList.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) * 1.0f / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) * 1.0f / 2) * mLineHeight, null);
        }
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhitePointList);
        boolean blackWin = checkFiveInLine(mBlackPointList);
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            if (gameOverListener != null) {
                gameOverListener.gameover(text);
            }

//            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

//            AlertDialog alertDialog=new AlertDialog.Builder(getContext());
        }
    }

    /**
     * 五子连线有四种可能，横向，纵向，左斜，右斜
     * 当落下白棋或者黑棋时，遍历其所在集合，判断其中每一个子的位置是否能构成五个子一条线
     *
     * @param mPointList
     * @return
     */
    private boolean checkFiveInLine(List<Point> mPointList) {
        for (Point point : mPointList) {
            int x = point.x;
            int y = point.y;
            boolean win = checkHorizontal(x, y, mPointList);
            if (win) return true;
            win = checkVertical(x, y, mPointList);
            if (win) return true;
            win = checkLeftDiagonal(x, y, mPointList);
            if (win) return true;
            win = checkRightDiagonal(x, y, mPointList);
            if (win) return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> mPointList) {
        int count = 1;
        //横向左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x - i, y))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        //横向右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x + i, y))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> mPointList) {
        int count = 1;
        //纵向上方
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x, y - i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        //横向右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x, y + i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //左斜
    private boolean checkLeftDiagonal(int x, int y, List<Point> mPointList) {
        int count = 1;
        //左斜左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x - i, y - i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        //左斜右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x + i, y + i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //右斜
    private boolean checkRightDiagonal(int x, int y, List<Point> mPointList) {
        int count = 1;
        //右斜左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x - i, y + i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        //右斜右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (mPointList.contains(new Point(x + i, y - i))) {
                count++;
            } else break;
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) return false;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point mPoint = getValidPoint(x, y);
            if (mWhitePointList.contains(mPoint) || mBlackPointList.contains(mPoint)) {
                return false;
            }

            if (mIsWhite) {
                mWhitePointList.add(mPoint);
            } else {
                mBlackPointList.add(mPoint);
            }
            mIsWhite = !mIsWhite;
            invalidate();
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITEARRAY = "instance_whitearray";
    private static final String INSTANCE_BLACKARRAY = "instance_blackarray";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITEARRAY, mWhitePointList);
        bundle.putParcelableArrayList(INSTANCE_BLACKARRAY, mBlackPointList);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhitePointList = bundle.getParcelableArrayList(INSTANCE_WHITEARRAY);
            mBlackPointList = bundle.getParcelableArrayList(INSTANCE_BLACKARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * 再来一局
     */
    public void restartGame() {
        mWhitePointList.clear();
        mBlackPointList.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }
}
