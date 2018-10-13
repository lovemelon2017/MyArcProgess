package xiaofu.com.arccustomprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ArcProgress extends View {

    private float time = 2000l;
    private ValueAnimator mAnimator;
    private Paint mPaint;
    private Paint dotPaint;
    private Paint textPoint;
    private float mStrokeWidth;
    private String defaultColor = "#f4f4f4"; //默认圆圈 颜色
    private String bgColor = "#99ff00ed";
    private String dotOutColor = "#ffffff";  //头外层颜色
    private String dotInColor = "#fbdfc5";   //头内层颜色
    private int mWidth, mHeight;
    private float progress = 0f;
    private boolean isDefaultCircle = true;  //是否绘制底部全圆 还是部分圆
    private int startAngle = -90;  //12钟点 起始点
    private int angleBg = 330;  //部分圆大小
    int arcRadius; //半径
    private int headOutRadius = 15;
    private int headInRadius = 12;
    private String typeString = "5"; //头文字

    public ArcProgress(Context context) {
        super(context);
        init(context);
    }

    public ArcProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArcProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mStrokeWidth = dipToPx(12);
        mPaint = new Paint();
        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        mPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.FILL);
        textPoint = new Paint();
        textPoint.setStrokeWidth(dipToPx(1));
        textPoint.setTextSize(dipToPx(15));
        textPoint.setColor(Color.WHITE);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 获取总的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        arcRadius = getMeasuredWidth() / 2 - dipToPx(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //默认灰色底层环形
        drawDefaultCircle(canvas);
        //颜色进度环形
        drawColorCircle(canvas);
        //绘制进度大圆点 头
        drawHead(canvas);
    }


    /**
     * 绘制进度头
     *
     * @param canvas
     */
    private void drawHead(Canvas canvas) {
        //底部圆圈
        canvas.translate(mWidth / 2, mHeight / 2);
        MyPoint coordinate = center2Point((int) progress, arcRadius);
        float x = coordinate.x;
        float y = coordinate.y;
        dotPaint.setColor(Color.parseColor(dotOutColor));
        canvas.drawCircle(x, y, dipToPx(headOutRadius), dotPaint);
        dotPaint.setColor(Color.parseColor(dotInColor));
        canvas.drawCircle(x, y, dipToPx(headInRadius), dotPaint);
        //上部文字
        canvas.drawText(typeString, x - dipToPx(headInRadius / 3), y + dipToPx(headInRadius / 3), textPoint);

    }

    /**
     * 绘制颜色的圆环
     *
     * @param canvas
     */
    private void drawColorCircle(Canvas canvas) {
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.parseColor(bgColor));
        RectF rectF = new RectF(dipToPx(headOutRadius / 2f) + mStrokeWidth / 2, dipToPx(headOutRadius / 2f) + mStrokeWidth / 2,
                mWidth - dipToPx(headOutRadius / 2f) - mStrokeWidth / 2, mHeight - dipToPx(headOutRadius / 2f) - mStrokeWidth / 2);
        canvas.drawArc(rectF, -90, progress, false, mPaint);

    }

    /**
     * 绘制默认灰色环形
     *
     * @param canvas
     */
    private void drawDefaultCircle(Canvas canvas) {
        // 绘制圆环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.parseColor(defaultColor));
        if (isDefaultCircle) {
            RectF rBg = new RectF(dipToPx(headOutRadius / 2f) + mStrokeWidth / 2, dipToPx(headOutRadius / 2f) + mStrokeWidth / 2,
                    mWidth - dipToPx(headOutRadius / 2f) - mStrokeWidth / 2, mHeight - dipToPx(headOutRadius / 2f) - mStrokeWidth / 2);
            //画部分
            canvas.drawArc(rBg, startAngle, angleBg, false, mPaint);
        } else {
            float cx = mWidth / 2.0f;
            float cy = mHeight / 2.0f;
            //画全圆圈
            canvas.drawCircle(cx, cy, arcRadius, mPaint);
        }
    }

    /**
     * 计算圆心半径到圆周上的某个点坐标
     * 起始点 正上方 -90°
     *
     * @param angle  度数
     * @param radius 半径
     * @return
     */
    private MyPoint center2Point(int angle, int radius) {
        MyPoint myPoint = new MyPoint();
        myPoint.x = (float) (radius * Math.cos((angle - 90) * Math.PI / 180));
        myPoint.y = (float) (radius * Math.sin((angle - 90) * Math.PI / 180));
        return myPoint;
    }

    /**
     * 设置当前显示的进度条
     *
     * @param progress
     */
    public void setProgress(float progress) {
        runAnimate(progress);
    }

    /**
     * 开始执行动画
     *
     * @param targetProgress 最终到达的进度
     */
    public void runAnimate(float targetProgress) {
        // 运行之前，先取消上一次动画
        cancelAnimate();
        mAnimator = ValueAnimator.ofFloat(0, targetProgress);
        // 设置差值器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                progress = value;
                invalidate();
            }
        });
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setDuration((long) (time * targetProgress / 360));
        mAnimator.start();
    }

    /**
     * 取消动画
     */
    public void cancelAnimate() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    public void setmStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }


    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    class MyPoint {
        float y = 0;
        float x = 0;
    }
}
