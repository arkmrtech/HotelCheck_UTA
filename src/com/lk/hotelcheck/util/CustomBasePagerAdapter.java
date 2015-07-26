package com.lk.hotelcheck.util;



import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * 
 * 类名称：CustomBasePagerAdapter
 * 类描述：带有视图缓存的PagerAdapter
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年7月16日 下午2:48:14
 * 修改备注：
 * @version 1.0.0
 *
 */
public abstract class CustomBasePagerAdapter extends PagerAdapter {
    
    private List<SoftReference<View>> mRecycleView;
    protected boolean mIsViewDestory = false;

    @Override
    public final boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
    
    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        if (null != mRecycleView && !mRecycleView.isEmpty()) {
            convertView = mRecycleView.remove(0).get();
        } 
        convertView = getView(position, convertView, container);
        if (null != convertView) {
            container.addView(convertView);
        }
        return convertView;
    }
    
    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        if (null != object && object instanceof View) {
        	mIsViewDestory = true;
            View convertView = (View) object;
            container.removeView(convertView);
            if (null == mRecycleView) {
				mRecycleView = new ArrayList<SoftReference<View>>();
            }
			mRecycleView.add(new SoftReference<View>(convertView));
        }
    }
    
	public void recycle() {
		if (null != mRecycleView && !mRecycleView.isEmpty()) {
			mRecycleView.clear();
		}
	}
    
    /**
     * 构建视图
     * getView(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @param position
     * @param convertView
     * @param parent
     * @return 
     *View
     * @exception 
     * @since  1.0.0
     */
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
