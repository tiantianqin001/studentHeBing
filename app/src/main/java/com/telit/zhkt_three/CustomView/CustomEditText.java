package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;


/**
 * author: qzx
 * Date: 2019/3/25 18:07
 * <p>
 * 通过getResources().getDimension换算
 * <p>
 * 使用示例如下：
 * <android.support.design.widget.TextInputLayout
 * android:layout_width="@dimen/dp_300"
 * android:layout_height="wrap_content">
 * <com.zbv.newdesttop.view.CustomEditText
 * android:id="@+id/login_pwd_edit"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * android:hint="输入密码"
 * android:inputType="textPassword"
 * zbv:pwdMode="true" />
 * </android.support.design.widget.TextInputLayout>
 * <p>
 * <p>
 * bug：也不算bug
 * dialog类型触摸点不正确
 * <p>
 * 想修改左侧的图片的，但是没有实现？？？
 * <p>
 * 限制字数用maxLength
 * 单行：maxLine=1且inputType=text/singleLine=true
 */
public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    private boolean isShowExist = false;
    private boolean isPWDMode = false;

    private Drawable deleteDraw;
    private Drawable userDraw;
    private Drawable pwdDraw;
    private Drawable eyeDraw_close;
    private Drawable eyeDraw_open;

    private boolean canShowPwd = false;

    private int iconWidth_left;
    private int iconPadding_left;
    private int iconWidth_right;
    private int iconPadding_right;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        iconWidth_left = (int) getResources().getDimension(R.dimen.x42);
        iconPadding_left = (int) getResources().getDimension(R.dimen.x30);

        iconWidth_right = (int) getResources().getDimension(R.dimen.x51);
        iconPadding_right = (int) getResources().getDimension(R.dimen.x21);

        initData(context, attrs, defStyleAttr);
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText, defStyleAttr, 0);
        //默认不回显用户名
        isShowExist = typeArray.getBoolean(R.styleable.CustomEditText_existShow, false);
        //默认非密码模式
        isPWDMode = typeArray.getBoolean(R.styleable.CustomEditText_pwdMode, false);
        typeArray.recycle();

        userDraw = context.getResources().getDrawable(R.mipmap.username);
        userDraw.setBounds(iconPadding_left, 0, iconWidth_left + iconPadding_left, iconWidth_left);
        pwdDraw = context.getResources().getDrawable(R.mipmap.password);
        pwdDraw.setBounds(iconPadding_left, 0, iconWidth_left + iconPadding_left, iconWidth_left);

        deleteDraw = context.getResources().getDrawable(R.mipmap.delete);
        deleteDraw.setBounds(-iconPadding_right, 0, iconWidth_right - iconPadding_right, iconWidth_right);
        eyeDraw_close = context.getResources().getDrawable(R.mipmap.eye_close);
        eyeDraw_close.setBounds(-iconPadding_right, 0, iconWidth_right - iconPadding_right, iconWidth_right);
        eyeDraw_open = context.getResources().getDrawable(R.mipmap.eye_open);
        eyeDraw_open.setBounds(-iconPadding_right, 0, iconWidth_right - iconPadding_right, iconWidth_right);

        //文本输入的padding
        setCompoundDrawablePadding(iconPadding_left);

        if (isPWDMode) {
            setCompoundDrawables(pwdDraw, null, null, null);
        } else {
            setCompoundDrawables(userDraw, null, null, null);
            if (isShowExist) {
                SharedPreferences sp = getContext().getSharedPreferences("save_username", Context.MODE_PRIVATE);
                String username = sp.getString("username", "");
                setText(username);
                setSelection(username.length());
            }
        }

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    if (isPWDMode) {
                        setCompoundDrawables(pwdDraw, null, eyeDraw_close, null);
                    } else {
                        setCompoundDrawables(userDraw, null, deleteDraw, null);
                    }
                } else {
                    if (isPWDMode) {
                        setCompoundDrawables(pwdDraw, null, null, null);
                    } else {
                        setCompoundDrawables(userDraw, null, null, null);
                    }
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //得到手指离开EditText时的X Y坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                //创建一个长方形
                Rect rect = new Rect();
                //让长方形的宽等于edittext的宽，让长方形的高等于edittext的高
                getLocalVisibleRect(rect);
                //把长方形缩短至右边30dp，约等于（padding+图标分辨率）
                rect.left = rect.right - (iconPadding_right + iconWidth_right);

//                QZXTools.logD("left=" + rect.left + ";right=" + rect.right + ";top=" + rect.top + "bottom=" + rect.bottom + ";x=" + x + ";y=" + y);

                //如果x和y坐标在长方形当中，说明你点击了右边的xx图片,清空输入框
                if (rect.contains(x, y)) {
                    if (isPWDMode) {
                        canShowPwd = !canShowPwd;
                        if (canShowPwd) {
                            setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            //注意：这个得在setInputType之后调用才会生效
                            setCompoundDrawables(pwdDraw, null, eyeDraw_open, null);
                        } else {
                            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            setCompoundDrawables(pwdDraw, null, eyeDraw_close, null);
                        }
                        setSelection(getText().toString().length());
                    } else {
                        setText("");
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
