package com.xiaomi.supercleanmaster.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.health.PackageHealthStats;
import android.util.Log;

import com.xiaomi.supercleanmaster.model.CacheInfo;
import com.xiaomi.supercleanmaster.utils.TextFormater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

public class CacheInfoProvider {
    private Handler handler;
    private PackageManager packageManager;
    private Vector<CacheInfo> cacheInfos;
    private int size = 0;

    public CacheInfoProvider(Handler handler, Vector<CacheInfo> cacheInfos, Context context) {
        // 拿到一个包管理器
        packageManager = context.getPackageManager();
        this.handler = handler;
        this.cacheInfos = cacheInfos;
        try {
            initCacheInfos();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void initCacheInfos() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
        List<PackageInfo> packageInfos = packageManager
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        size = packageInfos.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            CacheInfo cacheInfo = new CacheInfo();
            // 拿到包名
            String packageName = packageInfo.packageName;
            cacheInfo.setPackageName(packageName);
            // 拿到应用程序的信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            // 拿到应用程序的程序名
            String name = applicationInfo.loadLabel(packageManager).toString();
            cacheInfo.setName(name);
            // 拿到应用程序的图标
            Drawable icon = applicationInfo.loadIcon(packageManager);
            cacheInfo.setIcon(icon);

//            Method method = PackageManager.class.getMethod(
//                    "getPackageSizeInfo", new Class[] { String.class,
//                            IPackageStatsObserver.class });
//            method.invoke(packageManager,
//                    new Object[] { cacheInfo.getPackageName(),
//                            new IPackageStatsObserver.Stub()
//                            {
//                                @Override
//                                public void onGetStatsCompleted(
//                                        PackageStats pStats, boolean succeeded)
//                                        throws RemoteException
//                                {
//                                    long cacheSize = pStats.cacheSize;
//                                    long codeSize = pStats.codeSize;
//                                    long dataSize = pStats.dataSize;
//
//                                    cacheInfo.setCacheSize(TextFormater
//                                            .dataSizeFormat(cacheSize));
//                                    cacheInfo.setCodeSize(TextFormater
//                                            .dataSizeFormat(codeSize));
//                                    cacheInfo.setDataSize(TextFormater
//                                            .dataSizeFormat(dataSize));
//
////                                    cacheInfos.add(cacheInfo);
//                                }
//                            } });


//            cacheInfo.setCacheSize("123456");

            initPackageSizeInfo(cacheInfo);
            cacheInfos.add(cacheInfo);
//            initDataSize(cacheInfo, i);
        }
    }

    /**
     * 通过AIDL的方法来获取到应用的缓存信息，getPackageSizeInfo是PackageManager里面的一个私有方法来的
     * 我们通过反射就可以调用到它的了，但是这个方法里面会传递一个IPackageStatsObserver.Stub的对象
     * 里面就可能通过AIDL来获取我们想要的信息了
     * <p>
     * 因为这样的调用是异步的，所以当我们完成获取完这些信息之后，我们就通过handler来发送一个消息
     * 来通知我们的应用，通过getCacheInfos来获取到我们的Vector
     * <p>
     * 为什么要用Vector呢，因为下面的方法是异步的，也就是有可能是多线程操作，所以我们就用了线程安全的Vector
     *
     * @param cacheInfo
     * @param position
     */
    private void initDataSize(final CacheInfo cacheInfo, final int position) {
        try {
            Method method = PackageManager.class.getMethod(
                    "getPackageSizeInfo", new Class[]{String.class,
                            IPackageStatsObserver.class});
            method.invoke(packageManager,
                    new Object[]{cacheInfo.getPackageName(),
                            new IPackageStatsObserver.Stub() {
                                @Override
                                public void onGetStatsCompleted(
                                        PackageStats pStats, boolean succeeded)
                                        throws RemoteException {
                                    System.out.println("onGetStatsCompleted" + position);
                                    long cacheSize = pStats.cacheSize;
                                    long codeSize = pStats.codeSize;
                                    long dataSize = pStats.dataSize;

                                    cacheInfo.setCacheSize(TextFormater
                                            .dataSizeFormat(cacheSize));
                                    cacheInfo.setCodeSize(TextFormater
                                            .dataSizeFormat(codeSize));
                                    cacheInfo.setDataSize(TextFormater
                                            .dataSizeFormat(dataSize));

                                    cacheInfos.add(cacheInfo);

                                    if (position == (size - 1)) {
                                        // 当完全获取完信息之后，发送一个成功的消息
                                        // 1对应的就是CacheClearActivity里面的FINISH
                                        handler.sendEmptyMessage(1);
                                    }
                                }
                            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector<CacheInfo> getCacheInfos() {
        return cacheInfos;
    }

    public void setCacheInfos(Vector<CacheInfo> cacheInfos) {
        this.cacheInfos = cacheInfos;
    }

    public void initPackageSizeInfo(final CacheInfo cacheInfo) {
        // TODO Auto-generated method stub
        Method getPackageSizeInfo = null;
        try {
            // 通过反射机制获得该隐藏函数
            getPackageSizeInfo = PackageManager.class.getClass().getMethod("getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        SecurityPackageStatsObserver mSecurityPackageStatsObserver = new SecurityPackageStatsObserver();
        if (getPackageSizeInfo != null) {
            try {
                // 调用该函数，并且给其分配参数 ，待调用流程完成后会回调SecurityPackageStatsObserver类的函数
                getPackageSizeInfo.invoke(PackageManager.class, cacheInfo.getPackageName(), new IPackageStatsObserver.Stub() {

                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        long mCacheSize = pStats.cacheSize;
                        long mCodeSize = pStats.codeSize;
                        long mDataSize = pStats.dataSize;
                        cacheInfo.setCacheSize(TextFormater.dataSizeFormat(mCacheSize)+123456);
                        cacheInfo.setCodeSize(TextFormater.dataSizeFormat(mCodeSize));
                        cacheInfo.setDataSize(TextFormater.dataSizeFormat(mDataSize));

                        long mTotalSize = mCacheSize + mCodeSize + mDataSize;
                        Log.d("liuhongbo", "cacheSize = " + mCacheSize + "; codeSize = " + mCodeSize + "; dataSize = " + mDataSize
                                + "; totalSize = " + mTotalSize);
                    }

                });
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class SecurityPackageStatsObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            // TODO Auto-generated method stub
            long mCacheSize = pStats.cacheSize;
            long mCodeSize = pStats.codeSize;
            long mDataSize = pStats.dataSize;
            long mTotalSize = mCacheSize + mCodeSize + mDataSize;
            Log.d("liuhongbo", "cacheSize = " + mCacheSize + "; codeSize = " + mCodeSize + "; dataSize = " + mDataSize
                    + "; totalSize = " + mTotalSize);
        }
    }

}
