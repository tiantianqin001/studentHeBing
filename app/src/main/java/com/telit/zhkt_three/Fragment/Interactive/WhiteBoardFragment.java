package com.telit.zhkt_three.Fragment.Interactive;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.telit.zhkt_three.CustomView.ColorPicker;
import com.telit.zhkt_three.CustomView.PaletteView;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * author: qzx
 * Date: 2019/5/10 17:37
 * <p>
 * 这个fagment废弃了
 */
public class WhiteBoardFragment extends Fragment implements View.OnClickListener, PaletteView.Callback, ColorPicker.OnSeekColorListener {
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

    //色盘属性的保存
    private SharedPreferences sp_board;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_white_board_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        sp_board = getActivity().getSharedPreferences("board_color", MODE_PRIVATE);

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

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_pen:
                QZXTools.popToast(getActivity(), "画笔", false);
                paletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.select_pen_size:
                popupPenSize(v);
                break;
            case R.id.select_erase:
                QZXTools.popToast(getActivity(), "橡皮擦", false);
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
                QZXTools.popToast(getActivity(), "大端", false);
                paletteView.setPenRawSize(30);
                break;
            case R.id.pen_size_middle:
                QZXTools.popToast(getActivity(), "中端", false);
                paletteView.setPenRawSize(15);
                break;
            case R.id.pen_size_small:
                QZXTools.popToast(getActivity(), "小端", false);
                paletteView.setPenRawSize(7);
                break;
        }
    }

    private PopupWindow penSizePopup;

    private void popupPenSize(View v) {
        if (penSizePopup != null) {
            penSizePopup.dismiss();
        }
        View penSizeView = LayoutInflater.from(getActivity()).inflate(R.layout.board_pen_size_layout, null);
        penSizePopup = new PopupWindow(penSizeView, 120, 220);
        penSizePopup.setBackgroundDrawable(new ColorDrawable());
        penSizePopup.setOutsideTouchable(true);

        ImageView img_big = penSizeView.findViewById(R.id.pen_size_big);
        ImageView img_middle = penSizeView.findViewById(R.id.pen_size_middle);
        ImageView img_small = penSizeView.findViewById(R.id.pen_size_small);

        img_big.setOnClickListener(this);
        img_middle.setOnClickListener(this);
        img_small.setOnClickListener(this);

        penSizePopup.showAsDropDown(v, -(120 / 2 - v.getWidth() / 2), 0);

    }


    private PopupWindow colorPopup;

    private View preColorView = null;

    private void popupColor(View v) {
        if (colorPopup != null) {
            colorPopup.dismiss();
        }
        View colorView = LayoutInflater.from(getActivity()).inflate(R.layout.board_color_layout, null);
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

}
