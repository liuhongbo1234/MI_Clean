package com.xiaomi.supercleanmaster.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xiaomi.library.enums.QuickReturnType;
import com.xiaomi.library.listeners.QuickReturnListViewOnScrollListener;
import com.xiaomi.mi_ramcleanup.R;
import com.xiaomi.supercleanmaster.adapter.RublishMemoryAdapter;
import com.xiaomi.supercleanmaster.base.BaseSwipeBackActivity;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.xiaomi.supercleanmaster.engine.CacheInfoProvider;
import com.xiaomi.supercleanmaster.model.CacheInfo;
import com.xiaomi.supercleanmaster.model.CacheListItem;
import com.xiaomi.supercleanmaster.model.StorageSize;
import com.xiaomi.supercleanmaster.service.CleanerService;
import com.xiaomi.supercleanmaster.utils.StorageUtil;
import com.xiaomi.supercleanmaster.utils.SystemBarTintManager;
import com.xiaomi.supercleanmaster.utils.UIElementsHelper;
import com.xiaomi.supercleanmaster.widget.textcounter.CounterView;
import com.xiaomi.supercleanmaster.widget.textcounter.formatters.DecimalFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;

public class RubbishCleanActivity extends BaseSwipeBackActivity {


    ActionBar ab;
    protected static final int SCANING = 5;

    protected static final int SCAN_FINIFSH = 6;
    protected static final int PROCESS_MAX = 8;
    protected static final int PROCESS_PROCESS = 9;

    private static final int INITIAL_DELAY_MILLIS = 300;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    Resources res;
    int ptotal = 0;
    int pprocess = 0;


    private CleanerService mCleanerService;

    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;
    private Handler handler;
    private Vector<CacheInfo> cacheInfos = new Vector<>();

    @BindView(R.id.listview)
    ListView mListView;

    @BindView(R.id.empty)
    TextView mEmptyView;

    @BindView(R.id.header)
    RelativeLayout header;


    @BindView(R.id.textCounter)
    CounterView textCounter;
    @BindView(R.id.sufix)
    TextView sufix;

    @BindView(R.id.progressBar)
    View mProgressBar;
    @BindView(R.id.progressBarText)
    TextView mProgressBarText;

    RublishMemoryAdapter rublishMemoryAdapter;

    List<CacheListItem> mCacheListItem;

    @BindView(R.id.bottom_lin)
    LinearLayout bottom_lin;

    @BindView(R.id.clear_button)
    Button clearButton;


//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mCleanerService = ((CleanerService.CleanerServiceBinder) service).getService();
////            mCleanerService.setOnActionListener(RubbishCleanActivity.this);
//
////              updateStorageUsage();
//
//            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
//                mCleanerService.scanCache();
//            }
//        }

//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        mCleanerService.setOnActionListener(null);
//        mCleanerService = null;
//    }
//};

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rublish_clean);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //     applyKitKatTranslucency();

//        StikkyHeaderBuilder.stickTo(mListView).setHeader(header)
//                .minHeightHeaderPixel(0).build();
        res=getResources();


        int footerHeight=mContext.getResources().getDimensionPixelSize(R.dimen.footer_height);

        mListView.setEmptyView(mEmptyView);


        CacheInfoProvider cacheInfoProvider=new CacheInfoProvider(handler,cacheInfos,this);
        Vector<CacheInfo> v=cacheInfoProvider.getCacheInfos();
        mCacheListItem=new ArrayList<>();
        for(int i=0;i<v.size();i++){
        CacheInfo c=v.get(i);
        mCacheListItem.add(new CacheListItem(c.getPackageName(),c.getName(),c.getIcon(),Long.parseLong(c.getCacheSize())));
        }


        rublishMemoryAdapter=new RublishMemoryAdapter(mContext,mCacheListItem);
        mListView.setAdapter(rublishMemoryAdapter);
        mListView.setOnItemClickListener(rublishMemoryAdapter);
        mListView.setOnScrollListener(new QuickReturnListViewOnScrollListener(QuickReturnType.FOOTER,null,0,bottom_lin,footerHeight));
//        bindService(new Intent(mContext,CleanerService.class),
//        mServiceConnection,Context.BIND_AUTO_CREATE);
        }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // TODO Auto-generated method stub
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
//
//    }
//
//    @Override
//    public void onScanStarted(Context context) {
//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mProgressBarText.setText(R.string.scanning);
//        showProgressBar(true);
//    }
//
//    @Override
//    public void onScanProgressUpdated(Context context, int current, int max) {
//        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));
//    }
//
//    @Override
//    public void onScanCompleted(Context context, List<CacheListItem> apps) {
//        showProgressBar(false);
//        mCacheListItem.clear();
//        mCacheListItem.addAll(apps);
//        rublishMemoryAdapter.notifyDataSetChanged();
//        header.setVisibility(View.GONE);
//        if (apps.size() > 0) {
//            header.setVisibility(View.VISIBLE);
//            bottom_lin.setVisibility(View.VISIBLE);
//
//            long medMemory = mCleanerService != null ? mCleanerService.getCacheSize() : 0;
//
//            StorageSize mStorageSize = StorageUtil.convertStorageSize(medMemory);
//            textCounter.setAutoFormat(false);
//            textCounter.setFormatter(new DecimalFormatter());
//            textCounter.setAutoStart(false);
//            textCounter.setStartValue(0f);
//            textCounter.setEndValue(mStorageSize.value);
//            textCounter.setIncrement(5f); // the amount the number increments at each time interval
//            textCounter.setTimeInterval(50); // the time interval (ms) at which the text changes
//            sufix.setText(mStorageSize.suffix);
//            //  textCounter.setSuffix(mStorageSize.suffix);
//            textCounter.start();
//        } else {
//            header.setVisibility(View.GONE);
//            bottom_lin.setVisibility(View.GONE);
//        }
//
//        if (!mAlreadyScanned) {
//            mAlreadyScanned = true;
//
//        }
//    }
//
//    @Override
//    public void onCleanStarted(Context context) {
//        if (isProgressBarVisible()) {
//            showProgressBar(false);
//        }
//
//        if (!RubbishCleanActivity.this.isFinishing()) {
//            showDialogLoading();
//        }
//    }
//
//    @Override
//    public void onCleanCompleted(Context context, long cacheSize) {
//        dismissDialogLoading();
//        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(
//                mContext, cacheSize)), Toast.LENGTH_LONG).show();
//        header.setVisibility(View.GONE);
//        bottom_lin.setVisibility(View.GONE);
//        mCacheListItem.clear();
//        rublishMemoryAdapter.notifyDataSetChanged();
//    }
//
//    /**
//     * Apply KitKat specific translucency.
//     */
//    private void applyKitKatTranslucency() {
//
//        // KitKat translucent navigation/status bar.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
//            mTintManager.setStatusBarTintEnabled(true);
//            mTintManager.setNavigationBarTintEnabled(true);
//            // mTintManager.setTintColor(0xF00099CC);
//
//            mTintManager.setTintDrawable(UIElementsHelper
//                    .getGeneralActionBarBackground(this));
//
//            getActionBar().setBackgroundDrawable(
//                    UIElementsHelper.getGeneralActionBarBackground(this));
//
//        }
//
//    }
//
//    @OnClick(R.id.clear_button)
//    public void onClickClear() {
//
//        if (mCleanerService != null && !mCleanerService.isScanning() &&
//                !mCleanerService.isCleaning() && mCleanerService.getCacheSize() > 0) {
//            mAlreadyCleaned = false;
//
//            mCleanerService.cleanCache();
//        }
//    }
//
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }
//
//    private boolean isProgressBarVisible() {
//        return mProgressBar.getVisibility() == View.VISIBLE;
//    }
//
//    private void showProgressBar(boolean show) {
//        if (show) {
//            mProgressBar.setVisibility(View.VISIBLE);
//        } else {
//            mProgressBar.startAnimation(AnimationUtils.loadAnimation(
//                    mContext, android.R.anim.fade_out));
//            mProgressBar.setVisibility(View.GONE);
//        }
//    }
//
//    public void onDestroy() {
//        unbindService(mServiceConnection);
//        super.onDestroy();
//        }
        }
