package com.example.jnidemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Paint.Style.*;
/**
 * **********************************原理分析*******************************************
 *
 * ==>1.是由三个部分组成
 * 1）.最外面的的圈  可以自定义颜色 边框   color outerColor  ;borderWidth   dimension
 * 2>.上层圆圈       可以自定义颜色   color innerColor
 * 3）. 步数         字体大小,字体颜色   stepTextSize ;stepTextColor
 *
 * ==>2.实现
 * 2.1  获取自定义属性
 * 2.2   布局中使用
 * 2.3    自定义View中获取属性
 *
 * ==>3.重写OnMeaSure()  宽度！=高度  取最小值  确保一个正方形
 * ==>4.画内圆弧，外圆弧，文字
 * ==>5.其他
 * **********************************原理分析*********************************************
 */

public class QQStepView extends View {
    private Paint mpaint;
    //外层圆圈
    private int innerColor;
    //获取宽度
    private int borderWidth;
    //获取外面圆弧颜色
    private int outerColor;
    //获取文字颜色
    private int StepColor;
    //获取文字大小
    private int stepSize;

    /**
     * 内置圆
     */
    private int mStepMax = 0;  //最大步数
    private int mCureentStep = 0;//当前步数
    private Paint innerPaint;


    //文字画笔
    private Paint textPaint;

    //new QQStepView时候使用
    public QQStepView(Context context) {
        super(context);
    }

    //当需要在xml中声明此控件，则需要实现此构造函数。并且在构造函数中把自定义的属性与控件的数据成员连接起来。
    public QQStepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.QQStepView);
        //获取宽度
        borderWidth = ta.getDimensionPixelSize(R.styleable.QQStepView_borderWidth, 2);
        //获取里面圆圈颜色
        innerColor = ta.getColor(R.styleable.QQStepView_innerColor, Color.parseColor("#d62917"));
        //获取外面圆弧颜色
        outerColor = ta.getColor(R.styleable.QQStepView_outerColor, Color.parseColor("#d62917"));
        //获取文字颜色
        StepColor = ta.getColor(R.styleable.QQStepView_stepTextColor, Color.parseColor("#d62917"));
        //获取文字大小
        stepSize = ta.getDimensionPixelSize(R.styleable.QQStepView_stepTextSize, 16);
        ta.recycle();

        //外层圆  paint 初始化
        mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setStrokeWidth(borderWidth);
        mpaint.setStrokeCap(Paint.Cap.ROUND);
        mpaint.setColor(outerColor);
        mpaint.setStyle(STROKE);

        //外层圆  paint 初始化
        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setStrokeWidth(borderWidth);
        innerPaint.setStrokeCap(Paint.Cap.ROUND);
        innerPaint.setColor(innerColor);
        innerPaint.setStyle(STROKE);

        //文字
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(stepSize);
        textPaint.setColor(StepColor);
        textPaint.setStyle(STROKE);
    }

    // 自定义属性的时候使用
    public QQStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    //测量位置
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //调用者 1.在布局文件中可能是warp_content   2.宽度！=高度
        //获取模式AT_MOST

        //宽度！=高度  取最小值  确保一个正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //三目运算符 width>height?height:width
        setMeasuredDimension(width > height ? height : width, width > height ? height : width);
    }


    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1. 绘制外圆弧   问题：圆弧闭合  边缘没显示完整 ： 描边有宽度     borderWidth    圆弧
        //矩形的宽width = right - left ，高height = bottom - top；
        RectF rectF = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getWidth() - borderWidth / 2);
        canvas.drawArc(rectF, 135, 270, false, mpaint);

        //2.绘制内圆弧   不能写死  百分比，是使用者设置的 从外面传
        if (mStepMax == 0) return;
        //sweepAngle
        float sweepAngle = (float) mCureentStep / mStepMax;
        RectF innerRectF = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getWidth() - borderWidth / 2);
        canvas.drawArc(innerRectF, 135, sweepAngle * 270, false, innerPaint);


        //绘制文字
        String steptext = mCureentStep + "";
        Rect textBounds = new Rect();
        textPaint.getTextBounds(steptext, 0, steptext.length(), textBounds);
        int dx = getWidth() / 2 - textBounds.width() / 2;
        //基线BaseLines
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top)/2 - fontMetricsInt.bottom;
        int baseLine = getHeight()/2 + dy;
        /**
         * @param text The text to be drawn
         * * @param x The x-coordinate of the origin of the text being drawn
         * * @param y The y-coordinate of the baseline of the text being drawn
         * * @param paint The paint used for the text (e.g. color, size, style)
         */
        canvas.drawText(steptext, dx, baseLine, textPaint);
    }


    //5.写几个方法让他动起来  防止异步，加锁
    public synchronized void setmStepMax(int mStepMax) {
        this.mStepMax = mStepMax;
    }


    public synchronized void setmCureenStep(int mCureenStep) {
        this.mCureentStep = mCureenStep;
        //不断绘制  onDraw()
        /**
         *     p.invalidateChild(this, damage);
         *     p -->  父View
         */
        invalidate();
    }
}
