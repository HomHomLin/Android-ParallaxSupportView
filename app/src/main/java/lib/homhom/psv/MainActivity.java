package lib.homhom.psv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import homhomlib.lib.parallax.sv.ParallaxSupportView;
import lib.lhh.fiv.library.FrescoImageView;

public class MainActivity extends AppCompatActivity {

    ParallaxSupportView mParallaxSupportView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParallaxSupportView = (ParallaxSupportView) findViewById(R.id.parallaxSv);
        mParallaxSupportView.setProvider(new ParallaxProvider());
    }

    class ParallaxProvider extends ParallaxSupportView.ParallaxSupportViewProvider<ParallaxProvider.ViewHolder>{

        class ViewHolder extends ParallaxSupportView.ViewHolder{
            public ImageView htv;

            public ViewHolder(View itemView) {
                super(itemView);
                htv = (ImageView)itemView;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(int position) {
            ImageView htv = new ImageView(MainActivity.this);
//            FrescoImageView htv = new FrescoImageView(MainActivity.this);
            htv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            htv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            htv.loadView("https://avatars1.githubusercontent.com/u/8758713?v=3&s=460",R.mipmap.ic_launcher);
            return new ViewHolder(htv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.htv.setBackgroundResource(R.mipmap.ic_launcher);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
