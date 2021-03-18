package com.telit.zhkt_three.Fragment.Interactive;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.telit.zhkt_three.CustomView.Bubble.VerticalSeekBar;
import com.telit.zhkt_three.CustomView.ColorPicker;
import com.telit.zhkt_three.CustomView.PaletteView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.Fragment.Dialog.UploadBoardDialog;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * author: qzx
 * Date: 2019/6/19 15:01
 * 新版白板界面
 * <p>
 */
public class NewWhiteBoardFragment extends Fragment implements View.OnClickListener, PaletteView.Callback {

    private Unbinder unbinder;
    @BindView(R.id.new_color_board_view)
    PaletteView paletteView;
    @BindView(R.id.new_color_pen_layout)
    FrameLayout new_color_pen_layout;
    @BindView(R.id.new_color_path)
    ImageView new_color_path;
    @BindView(R.id.new_color_eraser)
    ImageView new_color_eraser;
    @BindView(R.id.new_color_clean)
    ImageView new_color_clean;
    @BindView(R.id.new_color_backward)
    ImageView new_color_backward;
    @BindView(R.id.new_color_forward)
    ImageView new_color_forward;
    @BindView(R.id.new_color_save)
    ImageView new_color_save;

    //默认的初始字体大小
    private static final int DefaultPenSize = 15;
    //默认最小字体大小
    private static final int MinProgressValue = 2;

    //色盘属性的保存
    private SharedPreferences sp_board;

    //动态改变svg颜色的Path
    private VectorDrawableCompat.VFullPath contentPath;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    /**
     * 是否需要上传白板
     */
    private boolean needUpload = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_white_board_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        sp_board = getActivity().getSharedPreferences("board_color", MODE_PRIVATE);

        paletteView.setCallback(this);
        //处于画笔状态
        paletteView.setMode(PaletteView.Mode.DRAW);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new_color_path.setVisibility(View.GONE);
        } else {
            new_color_path.setVisibility(View.VISIBLE);
            VectorChildFinder vector = new VectorChildFinder(getContext(), R.drawable.ic_pen_path, new_color_path);
            //依据svg中的path的name找到要操作的<Path>
            contentPath = vector.findPathByName("content_path");
        }

        //获取画笔颜色---这里注意下，Color.WHITE=-1 0xFFFFFFFF就是-1
        int color = sp_board.getInt("font_color", -7);
        if (color == -7) {
            paletteView.setPenColor(Color.BLACK);
            if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                contentPath.setFillColor(Color.BLACK);
                new_color_path.invalidate();
            }
        } else {
            paletteView.setPenColor(color);
            if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                contentPath.setFillColor(color);
                new_color_path.invalidate();
            }
        }

        //获取画笔大小
        int fontSize = sp_board.getInt("font_size", -1);
        if (fontSize == -1) {
            sp_board.edit().putInt("font_size", DefaultPenSize).commit();
            paletteView.setPenRawSize(DefaultPenSize);
        } else {
            paletteView.setPenRawSize(fontSize);
        }

        new_color_pen_layout.setOnClickListener(this);
        new_color_eraser.setOnClickListener(this);
        new_color_clean.setOnClickListener(this);
        new_color_backward.setOnClickListener(this);
        new_color_forward.setOnClickListener(this);
        new_color_save.setOnClickListener(this);

        new_color_pen_layout.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
        preBgView = new_color_pen_layout;


        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //防止出现界面消失，网络请求才反馈到
        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment = null;
        }

        //如果颜色盘还没有消失的话
        if (colorPopup != null) {
            colorPopup.dismiss();
        }

        //防泄漏
        QZXTools.setmToastNull();
        super.onDestroyView();
    }

    @Override
    public void onUndoRedoStatusChanged() {
        //可以撤销说明就可以擦出和清屏幕
        new_color_backward.setEnabled(paletteView.canUndo());
        new_color_eraser.setEnabled(paletteView.canUndo());
        new_color_clean.setEnabled(paletteView.canUndo());
        new_color_forward.setEnabled(paletteView.canRedo());
    }

    private View preBgView = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_color_pen_layout:
                if (preBgView != null) {
                    preBgView.setBackground(null);
                }
                new_color_pen_layout.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
                preBgView = new_color_pen_layout;

                QZXTools.popToast(getActivity(), "画笔", false);
                paletteView.setMode(PaletteView.Mode.DRAW);
                popColorPan(v);
                break;
            case R.id.new_color_eraser:
                if (preBgView != null) {
                    preBgView.setBackground(null);
                }
                new_color_eraser.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
                preBgView = new_color_eraser;

                QZXTools.popToast(getActivity(), "橡皮擦", false);
                paletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.new_color_clean:
                if (preBgView != null) {
                    preBgView.setBackground(null);
                }
                new_color_clean.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
                preBgView = new_color_clean;

                QZXTools.popToast(getActivity(), "清空画板", false);
                paletteView.clear();
                break;
            case R.id.new_color_backward:
                if (preBgView != null) {
                    preBgView.setBackground(null);
                }
                new_color_backward.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
                preBgView = new_color_backward;

                QZXTools.popToast(getActivity(), "撤销一步", false);
                paletteView.undo();
                break;
            case R.id.new_color_forward:
                if (preBgView != null) {
                    preBgView.setBackground(null);
                }
                new_color_forward.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
                preBgView = new_color_forward;

                QZXTools.popToast(getActivity(), "反撤销一步", false);
                paletteView.redo();
                break;
            case R.id.new_color_save:
                //todo 白板保存背景是白的，可是显示却有问题
                QZXTools.popToast(getActivity(), "画板保存", false);

                TipsDialog tipsDialog = new TipsDialog();
                tipsDialog.setTipsStyle("是否想要一并上传白板资源", "仅保存", "上传", R.mipmap.upload_white_board);
                tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                    @Override
                    public void cancle() {
                        needUpload = false;
                        tipsDialog.dismissAllowingStateLoss();
                        whiteBoardSave();
                    }

                    @Override
                    public void confirm() {
                        needUpload = true;
                        tipsDialog.dismissAllowingStateLoss();
                        whiteBoardSave();
                    }
                });
                tipsDialog.show(getChildFragmentManager(), TipsDialog.class.getSimpleName());

//                if (preBgView != null) {
//                    preBgView.setBackground(null);
//                }
//
//                new_color_more.setBackground(getResources().getDrawable(R.drawable.shape_color_panel_click_bg));
//                preBgView = new_color_more;
//
//                popMorePan(v);
                break;
            //色盘
            case R.id.new_color_close:
                colorPopup.dismiss();
                break;
            case R.id.new_color_white_layout:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(Color.WHITE);
                if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                    contentPath.setFillColor(Color.WHITE);
                    new_color_path.invalidate();
                }

                preColorView = v;
                sp_board.edit().putInt("font_color", 0xFFFFFFFF).commit();
                break;
            case R.id.new_color_gray_layout:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(0xFFB5B5B5);

                if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                    contentPath.setFillColor(0xFFB5B5B5);
                    new_color_path.invalidate();
                }

                preColorView = v;
                sp_board.edit().putInt("font_color", 0xFFB5B5B5).commit();
                break;
            case R.id.new_color_black_layout:
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                v.setSelected(true);
                paletteView.setPenColor(Color.BLACK);

                if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                    contentPath.setFillColor(Color.BLACK);
                    new_color_path.invalidate();
                }

                preColorView = v;
                sp_board.edit().putInt("font_color", 0xFF000000).commit();
                break;
            //更多
//            case R.id.new_board_save:
//                QZXTools.popToast(getActivity(), "画板保存", false);
//
//                TipsDialog tipsDialog = new TipsDialog();
//                tipsDialog.setTipsStyle("是否想要一并上传白板资源", "仅保存", "上传", R.mipmap.upload_white_board);
//                tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
//                    @Override
//                    public void cancle() {
//                        needUpload = false;
//                        tipsDialog.dismiss();
//                        whiteBoardSave();
//                    }
//
//                    @Override
//                    public void confirm() {
//                        needUpload = true;
//                        tipsDialog.dismiss();
//                        whiteBoardSave();
//                    }
//                });
//                tipsDialog.show(getChildFragmentManager(), TipsDialog.class.getSimpleName());
//                break;
//            case R.id.new_board_file_receive:
//                QZXTools.popToast(getActivity(), "文件接收", false);
//                ReceiveFilesDialog receiveFilesDialog = new ReceiveFilesDialog();
//                receiveFilesDialog.show(getChildFragmentManager(), ReceiveFilesDialog.class.getSimpleName());
//                break;

        }
    }

    /**
     * 白板保存
     */
    private void whiteBoardSave() {
        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        Bitmap bitmap = Bitmap.createBitmap(paletteView.getWidth(), paletteView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paletteView.draw(canvas);

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
            String savePath = QZXTools.getExternalStorageForFiles(getContext(), Environment.DIRECTORY_PICTURES);

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

                //更新到相册
                QZXTools.popToast(getContext(), "保存成功", false);

                //更新到相册
                QZXTools.savePictureToSystemDCIM(getContext(), file, "");

                if (circleProgressDialogFragment != null) {
                    circleProgressDialogFragment.dismissAllowingStateLoss();
                    circleProgressDialogFragment = null;
                }
                if (needUpload) {
                    //弹出上传弹框
                    UploadBoardDialog uploadBoardDialog = new UploadBoardDialog();
                    uploadBoardDialog.setActualSaveFilePath(file.getAbsolutePath());
                    uploadBoardDialog.show(getChildFragmentManager(), UploadBoardDialog.class.getSimpleName());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PopupWindow colorPopup;
    //选中的前一个view
    private View preColorView = null;

    /**
     * 弹出色调盘、画笔大小设置弹框
     */
    private void popColorPan(View v) {
        if (colorPopup != null) {
            colorPopup.dismiss();
        }
        View colorView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_white_board_color_layout, null);
        int colorWidth = getResources().getDimensionPixelSize(R.dimen.y549);
        int colorHeight = getResources().getDimensionPixelSize(R.dimen.x348);
        colorPopup = new PopupWindow(colorView, colorWidth, colorHeight);

        colorPopup.setBackgroundDrawable(new ColorDrawable());
        colorPopup.setOutsideTouchable(true);

        RelativeLayout white_layout = colorView.findViewById(R.id.new_color_white_layout);
        RelativeLayout gray_layout = colorView.findViewById(R.id.new_color_gray_layout);
        RelativeLayout black_layout = colorView.findViewById(R.id.new_color_black_layout);

        ImageView img_close = colorView.findViewById(R.id.new_color_close);

        img_close.setOnClickListener(this);
        white_layout.setOnClickListener(this);
        gray_layout.setOnClickListener(this);
        black_layout.setOnClickListener(this);

        ColorPicker colorPicker = colorView.findViewById(R.id.new_colorPicker);
        colorPicker.setOnSeekColorListener(new ColorPicker.OnSeekColorListener() {
            @Override
            public void onSeekColorListener(int color) {
                //保存颜色
                sp_board.edit().putInt("font_color", color).commit();
                if (preColorView != null) {
                    preColorView.setSelected(false);
                }
                paletteView.setPenColor(color);

                if (contentPath != null && new_color_path.getVisibility() == View.VISIBLE) {
                    contentPath.setFillColor(color);
                    new_color_path.invalidate();
                }
            }

            @Override
            public void curMarkPosition(int x, int y) {
                //保存色盘的选中区域
                sp_board.edit().putInt("font_color_x", x).commit();
                sp_board.edit().putInt("font_color_y", y).commit();
            }
        });

        TextView textView = colorView.findViewById(R.id.new_color_pen_size);

        VerticalSeekBar verticalSeekBar = colorView.findViewById(R.id.new_color_seekbar_vertical);

        int fontSize = sp_board.getInt("font_size", -1);
        if (fontSize == -1) {
            sp_board.edit().putInt("font_size", DefaultPenSize).commit();
            textView.setText(DefaultPenSize + "");
            paletteView.setPenRawSize(DefaultPenSize);
            verticalSeekBar.setProgress(DefaultPenSize - MinProgressValue);
        } else {
            textView.setText(fontSize + "");
            paletteView.setPenRawSize(fontSize);
            verticalSeekBar.setProgress(fontSize - MinProgressValue);
        }

        //设置移动滑块
        verticalSeekBar.setThumb(R.drawable.ic_slide);
        verticalSeekBar.setThumbSizePx(getResources().getDimensionPixelSize(R.dimen.x30), getResources().getDimensionPixelSize(R.dimen.x30));
        //设置垂直seekbar背景
        verticalSeekBar.setProgressBG(R.drawable.shape_vertical_seekbar_bg);
        verticalSeekBar.setmProgressBgSizePx(getResources().getDimensionPixelSize(R.dimen.x12), getResources().getDimensionPixelSize(R.dimen.x195));
        //设置垂直seekbar滑动监听
        verticalSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {
                QZXTools.logE("onStart progress=" + progress, null);
            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                QZXTools.logE("onProgress progress=" + progress, null);

                //实际的零为MinProgressValue开始的
                int actualProgress = progress + MinProgressValue;

                textView.setText(actualProgress + "");

                paletteView.setPenRawSize(actualProgress);

                //保存字体大小
                sp_board.edit().putInt("font_size", actualProgress).commit();
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {
                QZXTools.logE("onStop progress=" + progress, null);
            }
        });

        int color = sp_board.getInt("font_color", -7);

        QZXTools.logE("pop color=" + color, null);

        if (color == -1) {
            sp_board.edit().putInt("font_color", 0xFF000000).commit();
        }

        switch (color) {
            case 0xFF000000:
                black_layout.performClick();
                break;
            case 0xFFB5B5B5:
                gray_layout.performClick();
                break;
            case 0xFFFFFFFF:
                white_layout.performClick();
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
        colorPopup.showAsDropDown(v, -colorWidth, -colorHeight / 2 + v.getHeight());
    }

    private PopupWindow morePop;

    /**
     * 弹出更多内容选择弹框
     */
    private void popMorePan(View v) {
        if (morePop != null) {
            morePop.dismiss();
        }
        View moreView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_white_board_more_layout, null);
        int moreWidth = getResources().getDimensionPixelSize(R.dimen.y300);
        int moreHeight = getResources().getDimensionPixelSize(R.dimen.x126);
        morePop = new PopupWindow(moreView, moreWidth, moreHeight);

        morePop.setBackgroundDrawable(new ColorDrawable());
        morePop.setOutsideTouchable(true);

        LinearLayout new_board_save = moreView.findViewById(R.id.new_board_save);
        LinearLayout new_board_file_receive = moreView.findViewById(R.id.new_board_file_receive);

        new_board_save.setOnClickListener(this);
        new_board_file_receive.setOnClickListener(this);

        morePop.showAsDropDown(v, -moreWidth, -(moreHeight + v.getHeight()) / 2);
    }
}
