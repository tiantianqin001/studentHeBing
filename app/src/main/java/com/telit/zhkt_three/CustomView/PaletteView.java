package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wensefu on 17-3-21.
 * <br/> 橡皮擦的操作也可以回撤
 */
public class PaletteView extends View {

    private static final int DEFAULT_SIZE = 15;

    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    private static final int MAX_CACHE_STEP = 100;

    private List<PathDrawingInfo> mDrawingList;
    private List<PathDrawingInfo> mRemovedList;

    private Xfermode mXferModeClear;
    private Xfermode mXferModeDraw;
    private int mDrawSize;
    private int mPenAlpha = 255;

    private boolean mCanEraser;

    private Callback mCallback;

    public enum Mode {
        DRAW,
        ERASER
    }

    private Mode mMode = Mode.DRAW;

    private Context mContext;

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    //仅仅为了更新Activity中的UI
    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void init() {
        setDrawingCacheEnabled(true);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        //绘制的画笔粗细Size
        mDrawSize = DEFAULT_SIZE;
        //擦出的橡皮粗细Size
//        mEraserSize = DEFAULT_SIZE;

        mPaint.setStrokeWidth(mDrawSize);

        mPaint.setColor(0XFF000000);
        mXferModeDraw = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mXferModeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(mXferModeDraw);
    }

    /**
     * 双缓冲机制处理
     */
    private void initBuffer() {
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    public Mode getMode() {
        return mMode;
    }

    /**
     * 设置画笔的Xfermode模式以及画笔粗细的尺寸
     */
    public void setMode(Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Mode.DRAW) {
                mPaint.setXfermode(mXferModeDraw);

            } else {
                mPaint.setXfermode(mXferModeClear);
            }
            mPaint.setStrokeWidth(mDrawSize);
        }
    }

    public void setPenRawSize(int size) {
        mDrawSize = size;
        mPaint.setStrokeWidth(mDrawSize);
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    private void reDraw() {
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathDrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    public int getPenColor() {
        return mPaint.getColor();
    }

    public int getPenSize() {
        return mDrawSize;
    }

    public void setPenAlpha(int alpha) {
        mPenAlpha = alpha;
        if (mMode == Mode.DRAW) {
            mPaint.setAlpha(alpha);
        }
    }

    public int getPenAlpha() {
        return mPenAlpha;
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo() {
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    /**
     * 回退刚撤销一步，然后重画刷新界面
     */
    public void redo() {
       // QZXTools.logE("redo list=" + mDrawingList.size(), null);
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            PathDrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    /**
     * 撤销上一步的绘制，然后重画刷新界面
     */
    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
//        QZXTools.logE("undo list=" + mDrawingList.size(), null);
        if (size > 0) {
            PathDrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            refresh();
        }
    }

    /**
     * 用于页面切换的刷新重画
     */
    public void refresh() {
        reDraw();
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }

    /**
     * 每一步绘制结束抬手后保存绘制记录信息
     */
    private void saveDrawingPath() {
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();

        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
    }


    @SuppressWarnings("all")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        QZXTools.logE("drawingBoards onTouchEvent " + event.getAction(), null);
        if (!isEnabled()) {
            return false;
        }
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }

                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }
                mBufferCanvas.drawPath(mPath, mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:

                if (mMode == Mode.DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();

                break;
        }
        return true;
    }

    //-------------------------------保存绘制的操作信息
    private abstract static class DrawingInfo {
        Paint paint;

        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo {

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }
    //-------------------------------保存绘制的操作信息
}
