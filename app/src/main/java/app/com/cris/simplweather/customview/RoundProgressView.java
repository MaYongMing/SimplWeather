package app.com.cris.simplweather.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import app.com.cris.simplweather.R;

/**
 * Created by Cris on 2017/6/19.
 */

public class RoundProgressView extends View {

    int color1;
    int color2;
    int color3;
    int valueTextColor;
    int titleTextColor;
    int mWidth;
    int mHeight;

    String titleString;
    float diameter;
    float curProValue;
    float maxValue;
    float percentTextSize;
    float titleTextSize;

    RectF mRectF;
    Paint mTotalPaint;
    Paint mCurProPaint;
    Paint mPercentTextPaint;
    Paint mTitlePaint;


    public RoundProgressView(Context context) {
        this(context,null);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        intialConfig(context,attrs);
        intialPaints();
    }

    private void intialConfig(Context context, AttributeSet attrs){

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.RoundProgressView);
        color1 = array.getColor(R.styleable.RoundProgressView_color1, Color.GREEN);
        color2 = array.getColor(R.styleable.RoundProgressView_color1, Color.YELLOW);
        color3 = array.getColor(R.styleable.RoundProgressView_color1, Color.RED);

        titleString = "";
        titleString = array.getString(R.styleable.RoundProgressView_title);
        diameter = array.getDimension(R.styleable.RoundProgressView_diameter, 20);//set view height and width, default is 20dp. View is square.
        curProValue = array.getFloat(R.styleable.RoundProgressView_progressValue, 0);
        maxValue = array.getFloat(R.styleable.RoundProgressView_maxValue,100);
        percentTextSize = array.getDimension(R.styleable.RoundProgressView_percentValueTextSize,48);
        valueTextColor = array.getColor(R.styleable.RoundProgressView_valueTextColor,Color.GRAY);
        titleTextColor = array.getColor(R.styleable.RoundProgressView_titleTextColor,Color.GRAY);
        titleTextSize = array.getDimension(R.styleable.RoundProgressView_titleTextSize,24);
        mWidth = mHeight = (int) diameter + dpToPx(6);
    }

    private void intialPaints(){

        mTotalPaint  = new Paint();
        mTotalPaint.setAntiAlias(true);
        mTotalPaint.setStyle(Paint.Style.STROKE);
        mTotalPaint.setStrokeWidth(dpToPx(2));
        mTotalPaint.setColor(Color.GRAY);
        mTotalPaint.setStrokeCap(Paint.Cap.ROUND);

        mCurProPaint = new Paint();
        mCurProPaint.setAntiAlias(true);
        mCurProPaint .setStyle(Paint.Style.STROKE);
        mCurProPaint .setStrokeWidth(dpToPx(2));
        mCurProPaint.setStrokeCap(Paint.Cap.ROUND);

        mPercentTextPaint = new Paint();
        mPercentTextPaint.setColor(valueTextColor);
        mPercentTextPaint.setTextSize( percentTextSize);
        mPercentTextPaint.setTextAlign(Paint.Align.CENTER);

        mTitlePaint = new Paint();
        mTitlePaint.setTextSize(titleTextSize);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setColor(titleTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMode,width), measureHeight(heightMode,height));
    }

    private int measureWidth(int mode, int size){
        switch (mode) {

            case MeasureSpec.EXACTLY:
                mWidth=size;
                break;

            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            default:
                break;
        }
        return (mWidth);
    }
    private int measureHeight(int mode, int size){
        switch (mode) {

            case MeasureSpec.EXACTLY:
                mHeight=size;
                break;

            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            default:
                break;
        }
        return (mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 8;
        int top = getTop()+8;
        int right = left + (int)diameter;
        int bottom = top + (int)diameter;

        mRectF = new RectF(left,top,right,bottom);

        canvas.drawArc(mRectF,120,300,false, mTotalPaint);

        mCurProPaint.setShader(new SweepGradient( left+diameter/2, top + diameter/2, new int[]{color1,color2,color3,color3},null));
        canvas.rotate(90,left+diameter/2, top + diameter/2);
        canvas.drawArc(mRectF, 30,curProValue/maxValue*300 , false,  mCurProPaint);
        canvas.rotate(-90,left+diameter/2, top + diameter/2);

        Rect bounds = new Rect();
        mPercentTextPaint.getTextBounds(String.format("%.0f",curProValue),0,1,bounds);
        canvas.drawText(String.format("%.0f",curProValue), left+diameter/2 , top + diameter/2 , mPercentTextPaint);

        canvas.drawText(titleString, left+diameter/2 , top + diameter/2+ bounds.height()*3/2 , mTitlePaint);

    }

    public void setCurrentValue(float currentValue) {
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        if (currentValue < 0) {
            currentValue = 0;
        }
        this.curProValue = currentValue;
        postInvalidate();
    }

    public void setTitle(String title) {

        this.titleString = title;
        postInvalidate();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    private  int dpToPx(float dpValue){
        float density  = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue*density + 0.5f);
    }

}
