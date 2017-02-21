package lib.homhom.psv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import homhomlib.lib.v12.parallax.sv.ParallaxSupportView;
import lib.lhh.fiv.library.FrescoImageView;

public class MainActivity extends AppCompatActivity {

    private ParallaxSupportView mParallaxSupportView;
    private final static String[] mPic = new String[]{
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t1.jpg",
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t2.jpg",
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t3.jpg",
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t4.jpg",
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t5.jpg",
            "https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/t6.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParallaxSupportView = (ParallaxSupportView) findViewById(R.id.parallaxSv);
        ParallaxProvider provider = new ParallaxProvider();
        mParallaxSupportView.setProvider(provider);
        mParallaxSupportView.setFadeDuration(2000);
        mParallaxSupportView.setAnimInterceptor(new ParallaxSupportView.AnimInterceptor() {
            @Override
            public boolean anim(View view) {
                return false;
            }
        });
//        provider.notifyDataSetChanged();
    }

    class ParallaxProvider extends ParallaxSupportView.ParallaxSupportViewProvider<ParallaxProvider.ViewHolder>{

        class ViewHolder extends ParallaxSupportView.ViewHolder{
            public FrescoImageView mHtv;

            public ViewHolder(View itemView) {
                super(itemView);
                mHtv = (FrescoImageView)itemView;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(int type) {
            FrescoImageView htv = new FrescoImageView(MainActivity.this);
            htv.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            htv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(htv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mHtv.loadView(mPic[position], R.mipmap.ic_launcher);

        }

        @Override
        public int getItemCount() {
            return mPic.length;
        }
    }
}
