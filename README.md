# Wuziqi
照着慕课网上的android五子棋教程视频敲的代码，这里上传到自己的github仓库里面。以便将来想看了可以回到这里看看。
思想主要是自定义View。
重写View的onMeasure(...)和onDraw(...)方法

在onMeasure(...)获取控件的宽高：控件为正方形，取宽高中小的那个。
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
    
    
在onDraw(...)使用Canvas,Paint画棋盘（十行十列），棋子,检查游戏是否已经结束。
  @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }
    
在onTouchEvent(...)方法中记录下用户点击对应的棋盘上的坐标点，然后重绘这个自定义控件(invalidate())
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
