package com.xiaomi.supercleanmaster.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaomi.mi_ramcleanup.R;
import com.xiaomi.supercleanmaster.base.BaseActivity;
import com.xiaomi.supercleanmaster.bean.AppProcessInfo;
import com.xiaomi.supercleanmaster.service.CoreService;
import com.xiaomi.supercleanmaster.utils.StorageUtil;
import com.xiaomi.supercleanmaster.utils.SystemBarTintManager;
import com.xiaomi.supercleanmaster.utils.T;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;

public class ShortCutActivity extends BaseActivity implements CoreService.OnPeocessActionListener {

    @BindView(R.id.layout_anim)
    RelativeLayout layoutAnim;

    @BindView(R.id.mRelativeLayout)
    RelativeLayout mRelativeLayout;

    private Rect rect;

    @BindView(R.id.clean_light_img)
    ImageView cleanLightImg;

    private CoreService mCoreService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(ShortCutActivity.this);
            mCoreService.cleanAllProcess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_cut);
        rect = getIntent().getSourceBounds();
        if (rect == null) {
            finish();
            return;
        }

        if (rect != null) {

            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, statusBarHeight = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {

                e1.printStackTrace();
            }

            layoutAnim.measure(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            int height = layoutAnim.getMeasuredHeight();
            int width = layoutAnim.getMeasuredWidth();

            RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) layoutAnim
                    .getLayoutParams();

            layoutparams.leftMargin = rect.left + rect.width() / 2 - width / 2;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.transparent);
                layoutparams.topMargin = rect.top + rect.height() / 2 - height
                        / 2;

            } else {
                layoutparams.topMargin = rect.top + rect.height() / 2 - height
                        / 2 - statusBarHeight;
            }

            mRelativeLayout.updateViewLayout(layoutAnim, layoutparams);
        }
        cleanLightImg.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.rotate_anim));
        bindService(new Intent(mContext, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onScanStarted(Context context) {

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {

    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        if (cacheSize > 0) {
            T.showLong(mContext, "一键清理 开源版,为您释放" + StorageUtil.convertStorage(cacheSize) + "内存");
        } else {
            T.showLong(mContext, "您刚刚清理过内存,请稍后再来~");
        }

        finish();
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
