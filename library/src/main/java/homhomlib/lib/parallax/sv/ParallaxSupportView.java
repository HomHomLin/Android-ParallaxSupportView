package homhomlib.lib.parallax.sv;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

/**
 * Created by Linhh on 16/3/22.
 */
public class ParallaxSupportView extends FrameLayout {

    private final Handler mHandler;
    private int mActiveImageIndex = -1;

    private SparseArray<ViewHolder> mViewHolders;

    private final Random random = new Random();
    private int mSwapMs = 10000;
    private int mFadeInOutMs = 400;

//    private boolean mUseBlur = false;

    private float maxScaleFactor = 1.5F;
    private float minScaleFactor = 1.2F;

    private ParallaxSupportViewProvider mProvider;

    public static abstract class ViewHolder {
        public final View itemView;
        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }

    public static abstract class ParallaxSupportViewProvider<VH extends ViewHolder>{

        public abstract VH onCreateViewHolder(int position);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();
        
        public final VH createViewHolder(int position){
            final VH holder = onCreateViewHolder(position);
            return holder;
        }

        public final void bindViewHolder(VH holder, int position) {
            onBindViewHolder(holder, position);
        }
    }

    public void setProvider(ParallaxSupportViewProvider provider){
        mProvider = provider;
        viewsInvalid();
    }

    private Runnable mSwapImageRunnable = new Runnable() {
        @Override
        public void run() {
            swapImage();
            mHandler.postDelayed(mSwapImageRunnable, mSwapMs - mFadeInOutMs * 2);
        }
    };

    public ParallaxSupportView(Context context) {
        this(context, null);
    }

    public ParallaxSupportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxSupportView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler = new Handler();
    }

    private void swapImage() {
        if(mProvider == null){
            return;
        }
        if(mProvider.getItemCount() <= 0){
            return;
        }
        if(mActiveImageIndex == -1) {
            mActiveImageIndex = 0;
            final ViewHolder viewHolder = mViewHolders.get(mActiveImageIndex);
            mProvider.onBindViewHolder(viewHolder,mActiveImageIndex);
//            mProvider.onBindViewHolder(viewHolder,mActiveImageIndex);
            animate(viewHolder.itemView);
            return;
        }

//        mProvider.onBindViewHolder(viewHolder,i);

        int inactiveIndex = mActiveImageIndex;
        mActiveImageIndex = (1 + mActiveImageIndex) % mProvider.getItemCount();

        final ViewHolder activeViewHolder = mViewHolders.get(mActiveImageIndex);
        mProvider.onBindViewHolder(activeViewHolder,mActiveImageIndex);
        final ViewHolder inactiveHolder = mViewHolders.get(inactiveIndex);
        mProvider.onBindViewHolder(inactiveHolder,inactiveIndex);

        ViewHelper.setAlpha(activeViewHolder.itemView, 0.0f);
        animate(activeViewHolder.itemView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mFadeInOutMs);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(inactiveHolder.itemView, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(activeViewHolder.itemView, "alpha", 0.0f, 1.0f)
        );
        animatorSet.start();
    }

    private void start(View view, long duration, float fromScale, float toScale, float fromTranslationX, float fromTranslationY, float toTranslationX, float toTranslationY) {
        ViewHelper.setScaleX(view, fromScale);
        ViewHelper.setScaleY(view, fromScale);
        ViewHelper.setTranslationX(view, fromTranslationX);
        ViewHelper.setTranslationY(view, fromTranslationY);
        ViewPropertyAnimator propertyAnimator = ViewPropertyAnimator
                .animate(view)
                .translationX(toTranslationX)
                .translationY(toTranslationY)
                .scaleX(toScale)
                .scaleY(toScale)
                .setDuration(duration);

        propertyAnimator.start();
    }

    private float pickScale() {
        return this.minScaleFactor + this.random.nextFloat() * (this.maxScaleFactor - this.minScaleFactor);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
    }

    public void animate(View view) {
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
        start(view, this.mSwapMs, fromScale, toScale, fromTranslationX, fromTranslationY, toTranslationX, toTranslationY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mSwapImageRunnable);
    }

    private void viewsInvalid() {
        if(mProvider == null){
            throw new IllegalArgumentException("provider may not be null");
        }
        if(mViewHolders != null){
            mViewHolders.clear();
        }
        for (int i = 0; i < mProvider.getItemCount(); i++) {

            ViewHolder viewHolder = mProvider.onCreateViewHolder(i);

            if(mViewHolders == null){
                mViewHolders = new SparseArray<>();
            }

            mViewHolders.put(i, viewHolder);

            if(viewHolder != null){
                this.addView(viewHolder.itemView);
            }

        }
        mHandler.post(mSwapImageRunnable);
    }
}

