package com.telit.zhkt_three.Activity.HomeWork;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.ColorPicker;
import com.telit.zhkt_three.CustomView.PaletteView;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 白板保存文件路径：
 * /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/Pictures/board_yyyyMMdd_HHmmss.jpg
 */
public class WhiteBoardActivity extends BaseActivity
        implements View.OnClickListener, PaletteView.Callback, ColorPicker.OnSeekColorListener {

    private Unbinder unbinder;

    @BindView(R.id.board_view)
    PaletteView paletteView;

    @BindView(R.id.select_pen)
    ImageView select_pen;
    @BindView(R.id.select_pen_size)
    ImageView select_penSize;
    @BindView(R.id.select_erase)
    ImageView select_erase;
    @BindView(R.id.select_color)
    ImageView select_color;
    @BindView(R.id.select_undo)
    ImageView select_undo;
    @BindView(R.id.select_redo)
    ImageView select_redo;
    @BindView(R.id.select_clear)
    ImageView select_clear;
    @BindView(R.id.select_save)
    TextView select_save;
    //色盘属性的保存
    private SharedPreferences sp_board;
    private String questionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_white_board_layout);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);
        String extraInfo = getIntent().getStringExtra("extra_info");
        if (!TextUtils.isEmpty(extraInfo)) {
            questionId = extraInfo;
        }
        sp_board = getSharedPreferences("board_color", MODE_PRIVATE);

        paletteView.setCallback(this);
        //处于画笔状态
        paletteView.setMode(PaletteView.Mode.DRAW);

        //绘制中的使能状态
        select_undo.setEnabled(false);
        select_redo.setEnabled(false);
        select_erase.setEnabled(false);
        select_clear.setEnabled(false);

        select_pen.setOnClickListener(this);
        select_penSize.setOnClickListener(this);
        select_erase.setOnClickListener(this);
        select_color.setOnClickListener(this);
        select_undo.setOnClickListener(this);
        select_redo.setOnClickListener(this);
        select_clear.setOnClickListener(this);
        select_save.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        QZXTools.setmToastNull();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_pen:
                QZXTools.popToast(this, "画笔", false);
                paletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.select_pen_size:
                popupPenSize(v);
                break;
            case R.id.select_erase:
                QZXTools.popToast(this, "橡皮擦", false);
                paletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.select_color:
                popupColor(v);
                break;
            case R.id.select_undo:
                paletteView.undo();
                break;
            case R.id.select_redo:
                paletteView.redo();
                break;
            case R.id.select_clear:
                paletteView.clear();
                break;

            //颜色盘
            case R.id.img_black:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(Color.BLACK);
                preColorView = v;
                sp_board.edit().putInt("font_color", Color.BLACK).commit();
                break;
            case R.id.img_gray:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(Color.GRAY);
                preColorView = v;
                sp_board.edit().putInt("font_color", Color.GRAY).commit();
                break;
            case R.id.img_white:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(Color.WHITE);
                preColorView = v;
                sp_board.edit().putInt("font_color", Color.WHITE).commit();
                break;

            //笔粗细
            case R.id.pen_size_big:
                QZXTools.popToast(this, "大端", false);
                paletteView.setPenRawSize(30);
                break;
            case R.id.pen_size_middle:
                QZXTools.popToast(this, "中端", false);
                paletteView.setPenRawSize(15);
                break;
            case R.id.pen_size_small:
                QZXTools.popToast(this, "小端", false);
                paletteView.setPenRawSize(7);
                break;
            case R.id.select_save:
                //白板保存
                TipsDialog tipsDialog = new TipsDialog();
                tipsDialog.setTipsStyle("是否保存白板？", "取消", "保存", -1);
                tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                    @Override
                    public void cancle() {
                        tipsDialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void confirm() {
                        tipsDialog.dismissAllowingStateLoss();
                        //保存白板
                        Bitmap bitmap = getCanvasSnapshot(paletteView);

                        if (bitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            //使用JPEG不行，全黑
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] bytes = byteArrayOutputStream.toByteArray();

                            try {
                                byteArrayOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //保存的路径
                            String savePath = QZXTools.getExternalStorageForFiles(WhiteBoardActivity.this, Environment.DIRECTORY_PICTURES);

                            try {

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                                String dateStr = simpleDateFormat.format(new Date());

                                File file = new File(savePath, "board_" + dateStr + ".jpg");

                                QZXTools.logE("whiteboard savePath=" + savePath, null);

                                if (file != null && !file.exists()) {
                                    boolean isSuccess = file.createNewFile();
                                    if (isSuccess) {
                                        QZXTools.logE("is successful", null);
                                    } else {
                                        QZXTools.logE("is failure", null);
                                        return;
                                    }
                                } else {
                                    QZXTools.logE("is existed", null);
                                }

                                FileOutputStream fos = new FileOutputStream(file);

                                fos.write(bytes);

                                fos.close();

                                ExtraInfoBean extraInfoBean = new ExtraInfoBean();
                                extraInfoBean.setFlag(Constant.Subjective_Board_Callback);
                                extraInfoBean.setFilePath(file.getAbsolutePath());
                                extraInfoBean.setQuestionId(questionId);

                                EventBus.getDefault().post(extraInfoBean, Constant.Subjective_Board_Callback);

                                WhiteBoardActivity.this.finish();


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                break;
        }
    }

    private PopupWindow penSizePopup;

    private void popupPenSize(View v) {
        if (penSizePopup != null) {
            penSizePopup.dismiss();
        }
        View penSizeView = LayoutInflater.from(this).inflate(R.layout.board_pen_size_layout, null);
        penSizePopup = new PopupWindow(penSizeView, 140, 320);
        penSizePopup.setBackgroundDrawable(new ColorDrawable());
        penSizePopup.setOutsideTouchable(true);

        ImageView img_big = penSizeView.findViewById(R.id.pen_size_big);
        ImageView img_middle = penSizeView.findViewById(R.id.pen_size_middle);
        ImageView img_small = penSizeView.findViewById(R.id.pen_size_small);

        img_big.setOnClickListener(this);
        img_middle.setOnClickListener(this);
        img_small.setOnClickListener(this);

        penSizePopup.showAsDropDown(v, -(140 / 2 - v.getWidth() / 2), 0);

    }


    private PopupWindow colorPopup;

    private View preColorView = null;

    private void popupColor(View v) {
        if (colorPopup != null) {
            colorPopup.dismiss();
        }
        View colorView = LayoutInflater.from(this).inflate(R.layout.board_color_layout, null);
        colorPopup = new PopupWindow(colorView,
                500, 350);

        colorPopup.setBackgroundDrawable(new ColorDrawable());
        colorPopup.setOutsideTouchable(true);

        ImageView img_black = colorView.findViewById(R.id.img_black);
        ImageView img_gray = colorView.findViewById(R.id.img_gray);
        ImageView img_white = colorView.findViewById(R.id.img_white);

        img_black.setOnClickListener(this);
        img_gray.setOnClickListener(this);
        img_white.setOnClickListener(this);

        ColorPicker colorPicker = colorView.findViewById(R.id.colorPicker);
        colorPicker.setOnSeekColorListener(this);

        int color = sp_board.getInt("font_color", -1);
        if (color == -1) {
            sp_board.edit().putInt("font_color", Color.BLACK).commit();
        }

        switch (color) {
            case Color.BLACK:
                img_black.performClick();
                break;
            case Color.GRAY:
                img_gray.performClick();
                break;
            case Color.WHITE:
                img_white.performClick();
                break;
            default:
                colorPicker.setColor(color);
                colorPicker.setTouchCirclePosition(
                        sp_board.getInt("font_color_x", 0),
                        sp_board.getInt("font_color_y", 0));
                colorPicker.invalidate();
                break;
        }

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        colorPopup.showAsDropDown(v, -(500 / 2 - v.getWidth() / 2), 0);
    }

    @Override
    public void onSeekColorListener(int color) {
        sp_board.edit().putInt("font_color", color).commit();
        if (preColorView != null) {
            preColorView.setSelected(false);
        }
        paletteView.setPenColor(color);
    }

    @Override
    public void curMarkPosition(int x, int y) {
        sp_board.edit().putInt("font_color_x", x).commit();
        sp_board.edit().putInt("font_color_y", y).commit();
    }

    @Override
    public void onUndoRedoStatusChanged() {
        //可以撤销说明就可以擦出和清屏幕
        select_undo.setEnabled(paletteView.canUndo());
        select_clear.setEnabled(paletteView.canUndo());
        select_erase.setEnabled(paletteView.canUndo());

        select_redo.setEnabled(paletteView.canRedo());
    }

    /**
     * 保存时对当前绘图板的图片进行快照
     */
    public Bitmap getCanvasSnapshot(View v) {
        //方式一
        QZXTools.logE("view width=" + v.getMeasuredWidth() + ";height=" + v.getMeasuredHeight(), null);
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);

        //方式二
//        paletteView.setDrawingCacheEnabled(true);
//        paletteView.buildDrawingCache(true);
//        Bitmap bmp = paletteView.getDrawingCache(true);
//        if (null == bmp) {
//            QZXTools.logE("getCanvasSnapshot getDrawingCache == null", null);
//        }
        return bitmap;
    }

    /**
     * 对图片生成一备份
     */
    private Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth() + 15;
        int bmpSrcHeight = bmpSrc.getHeight() + 15;

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrc.getWidth(), bmpSrc.getHeight(),
                Bitmap.Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }

        return bmpDest;
    }

}
