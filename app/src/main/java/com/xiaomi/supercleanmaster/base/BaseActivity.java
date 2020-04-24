package com.xiaomi.supercleanmaster.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.fragment.app.FragmentActivity;

import com.xiaomi.supercleanmaster.dialog.ProgressDialogFragment;

import butterknife.ButterKnife;

public class BaseActivity extends FragmentActivity {
    /**
     * 屏幕的宽度，高度，密度
     */
    protected int mScreenWidth;
    protected int mScreenHeigth;
    protected float mDensity;
    protected Context mContext;
    protected String LogName; // 打印的名称

    private static String mDialogTag = "basedialog";

    ProgressDialogFragment mProgressDialogFragment;

    protected Boolean isfinish = false;
    protected ActivityTack tack = ActivityTack.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeigth = metrics.heightPixels;
        mDensity = metrics.density;
        LogName = this.getClass().getSimpleName();
        tack.addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    /**
     * 通过Class跳转页面
     **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有bundle通过Class跳转界面
     **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action跳转页面
     */
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 含有Bundle通过Action跳转页面
     */
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void finish() {
        super.finish();
        tack.removeActivity(this);
    }

    public void showDialogLoading() {
        showDialogLoading(null);
    }

    public void showDialogLoading(String msg) {
        if (mProgressDialogFragment == null) {
            mProgressDialogFragment = ProgressDialogFragment.newInstance(0, null);
        }
        if (msg != null) {
            mProgressDialogFragment.setMessage(msg);
        }
        mProgressDialogFragment.show(getFragmentManager(), mDialogTag);
    }

    public void dismissDialogLoading() {
        if (mProgressDialogFragment != null) {
            mProgressDialogFragment.dismiss();
        }
    }
}
