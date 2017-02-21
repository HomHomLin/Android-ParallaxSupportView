package homhomlib.lib.v12.parallax.sv;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Observable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Random;

/**
 * Created by Linhh on 16/3/27.
 */
public class ParallaxSupportView extends FrameLayout {

    private final Random mRandom = new Random();
//    private final Handler mHandler;

    private final ParallaxSupportViewDataObserver mObserver = new ParallaxSupportViewDataObserver();

    private int mIndex = 0;

    private final SparseArray<ViewHolder> mViewHolders = new SparseArray<>();
    private final SparseArray<ViewHolder> mCopyHolders = new SparseArray<>();

    private boolean mIsAttachedToWindow = false;

    private int mParallaxDuration = 10000;
    private int mFadeDuration = 500;

    private float mMaxScaleSize = 1.5F;
    private float mMinScaleSize = 1.0F;

    private ParallaxSupportViewProvider mProvider;

    private AnimInterceptor mAnimInterceptor;

    public static abstract class ViewHolder {
        public final View itemView;
        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }

    public interface AnimInterceptor{
        public boolean anim(View view);
    }

    public static abstract class ParallaxSupportViewProvider<VH extends ViewHolder>{

        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        private int mItemTypeCount = 1;

        private final static int NO_TYPE = 0;

        public abstract VH onCreateViewHolder(int type);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

        public int getItemTypeCount(){
            return mItemTypeCount;
        }

        /**
         * 根据pos来确定使用哪个type布局
         * @param position
         * @return
         */
        public int getItemType(int position){
            return NO_TYPE;
        }

        public final VH createViewHolder(int type){
            final VH holder = onCreateViewHolder(type);
            return holder;
        }

        public final void bindViewHolder(VH holder, int position) {
            onBindViewHolder(holder, position);
        }

        public void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }


        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }


        public final boolean hasObservers() {
            return mObservable.hasObservers();
        }

    }

    public ParallaxSupportView(Context context) {
        this(context, null);
    }

    public ParallaxSupportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxSupportView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        mHandler = new Handler();
    }

    public void setProvider(ParallaxSupportViewProvider provider){

        if (mProvider != null) {
            mProvider.unregisterAdapterDataObserver(mObserver);
        }

        mProvider = provider;

        if (mProvider != null) {
            mProvider.registerAdapterDataObserver(mObserver);
        }

        viewsInvalid();
    }

    public void setAnimInterceptor(AnimInterceptor animInterceptor){
        mAnimInterceptor = animInterceptor;
    }

    public ParallaxSupportViewProvider getProvider(){
        return mProvider;
    }

    private Runnable mParallaxImageRunnable = new Runnable() {
        @Override
        public void run() {
            swapImage();
            ParallaxSupportView.this.postDelayed(mParallaxImageRunnable, mParallaxDuration);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void swapImage() {

        if(mProvider == null){
            return;
        }

        if(mProvider.getItemCount() <= 0){
            return;
        }

        if(this.getChildCount() > 1)
            this.removeViewAt(0);

        int type = mProvider.getItemType(mIndex);

        ViewHolder viewHolder = mViewHolders.get(type);

        if(viewHolder.itemView.getParent() == null) {
            mProvider.bindViewHolder(viewHolder, mIndex);
            this.addView(viewHolder.itemView);
        }else{
//            if(mCopyHolders == null){
//                mCopyHolders = new SparseArray<>();
//            }
            ViewHolder copy_viewHolder = mCopyHolders.get(type);
            if(copy_viewHolder == null){
                copy_viewHolder = mProvider.createViewHolder(type);
                mCopyHolders.put(type , copy_viewHolder);
            }
            mProvider.bindViewHolder(copy_viewHolder, mIndex);
            this.addView(copy_viewHolder.itemView);
        }

        mIndex = (1 + mIndex) % mProvider.getItemCount();

        View oldView = this.getChildAt(0);
        View newView = this.getChildAt(1);

        if(newView == null){
            //没有新图，说明是第一次
            animate(oldView);
            return;
        }

        newView.setAlpha(0.0F);
//        oldView.setAlpha(1.0F);
//
//        newView.animate()
//                .alpha(1.0F)
//                .setDuration(mFadeDuration);
//        oldView.animate()
//                .alpha(0.0F)
//                .setDuration(mFadeDuration);

        animate(newView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mFadeDuration);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(oldView, "alpha", 1.0F, 0.0F),
                ObjectAnimator.ofFloat(newView, "alpha", 0.0F, 1.0F)
        );
        animatorSet.start();

    }

    public void setFadeDuration(int duration){
        mFadeDuration = duration;
    }

    public int getFadeDuration(){
        return mFadeDuration;
    }

    public void setParallaxDuration(int duration){
        mParallaxDuration = duration;
    }

    public int getParallaxDuration(){
        return mParallaxDuration;
    }

    public void setMinScaleSize(int minScaleSize){
        mMinScaleSize = minScaleSize;
    }

    public void setMaxScaleSize(int maxScaleSize){
        mMaxScaleSize = maxScaleSize;
    }

    private void startParallax(View view, long duration, float fromScale, float toScale, float fromTranslationX, float fromTranslationY, float toTranslationX, float toTranslationY) {
        view.setScaleX(fromScale);
        view.setScaleY(fromScale);
        view.setTranslationX(fromTranslationX);
        view.setTranslationY(fromTranslationY);
        view.animate()
                .translationX(toTranslationX)
                .translationY(toTranslationY)
                .scaleX(toScale)
                .scaleY(toScale)
                .setDuration(duration);
    }

    private float pickScale() {
        return mMinScaleSize + mRandom.nextFloat() * (mMaxScaleSize - mMinScaleSize);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0F) * (mRandom.nextFloat() - 0.5F);
    }

    private void animate(View view) {
        if(view == null){
            return;
        }
        if(mAnimInterceptor != null && mAnimInterceptor.anim(view)){
            return;
        }
        float fromScale = pickScale();
        float toScale = pickScale();
        float fromTranslationX = pickTranslation(view.getWidth(), fromScale);
//        if(fromTranslationX == 0){
//            fromTranslationX = -70;//默认
//        }

        float fromTranslationY = pickTranslation(view.getHeight(), fromScale);
//        if(fromTranslationY == 0){
//            fromTranslationY = -20;//默认
//        }
        float toTranslationX = pickTranslation(view.getWidth(), toScale);
//        if(toTranslationX == 0){
//            toTranslationX = 60;
//        }
        float toTranslationY = pickTranslation(view.getHeight(), toScale);
//        if(toTranslationY == 0){
//            toTranslationY = 40;
//        }
        startParallax(view, mParallaxDuration - mFadeDuration * 2, fromScale, toScale, fromTranslationX, fromTranslationY, toTranslationX, toTranslationY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
        release();
    }

    private void release() {
        mAnimInterceptor = null;
        mProvider = null;
        this.removeCallbacks(mParallaxImageRunnable);
        if(mCopyHolders != null){
            for(int i = 0; i < mCopyHolders.size(); i ++){
                mCopyHolders.get(i).itemView.clearAnimation();
            }
            mCopyHolders.clear();
//            mCopyHolders = null;
        }
        if(mViewHolders != null){
            for(int i = 0; i < mViewHolders.size(); i ++){
                mViewHolders.get(i).itemView.clearAnimation();
            }
            mViewHolders.clear();
//            mViewHolders = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
    }

    private void viewsInvalid() {

        this.removeCallbacks(mParallaxImageRunnable);

        if(this.getChildCount() > 0){
            this.removeAllViews();
        }

        if(mProvider == null){
            throw new IllegalArgumentException("provider may not be null");
        }

        if(mViewHolders != null){
            mViewHolders.clear();
        }

        if(mCopyHolders != null){
            mCopyHolders.clear();
        }

//        if(mViewHolders == null){
//            mViewHolders = new SparseArray<>();
//        }

        for (int type = 0; type < mProvider.getItemTypeCount(); type ++ ){
            ViewHolder viewHolder = mProvider.createViewHolder(type);
//            if(mViewHolders == null){
//                mViewHolders = new SparseArray<>();
//            }

            mViewHolders.put(type, viewHolder);
        }

        this.post(mParallaxImageRunnable);
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }
    }

    private class ParallaxSupportViewDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            //refersh View
            viewsInvalid();
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }
}

