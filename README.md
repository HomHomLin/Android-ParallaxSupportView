# Android-ParallaxSupportView

Android-ParallaxSupportView是一种Android平台的动态视觉差控件，支持N个任意View的视觉差效果，支持最低API 9。

Project site： <https://github.com/HomHomLin/Android-ParallaxSupportView>.

最新版本:v1.0.0


![p1](https://raw.githubusercontent.com/HomHomLin/Android-ParallaxSupportView/master/TestPic/demo.gif)

## 支持平台

API 9+

## 导入项目

**API 9+：

**Gradle dependency:**
``` groovy
compile 'homhomlin.lib:psv-library:1.0.0'
```

or

**Maven dependency:**
``` xml
<dependency>
  <groupId>homhomlin.lib</groupId>
  <artifactId>psv-library</artifactId>
  <version>1.0.0</version>
</dependency>
```

**API 12+：

**Gradle dependency:**
``` groovy
compile 'homhomlin.lib.v12:psv-library:1.0.0'
```

or

**Maven dependency:**
``` xml
<dependency>
  <groupId>homhomlin.lib.v12</groupId>
  <artifactId>psv-library</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 依赖

如果你使用的是API 9+的ParallaxSupportView，需要以下依赖：

``` groovy
compile 'com.nineoldandroids:library:2.4.0'
```

API 12+的ParallaxSupportView不需要依赖。

## 用法

1.在需要添加的界面xml中添加组件

``` xml
    <homhomlib.lib.parallax.sv.ParallaxSupportView
        android:id="@+id/parallaxSv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

2.创建ParallaxProvider

与RecyclerView的RecyclerViewAdpater构造方式一致，首先需要一个ParallaxSupportView.ViewHolder，然后再进行Provider的构造，ParallaxSupportView不会重复构建View，以下为最基本的Provider构造。

``` java
class ParallaxProvider extends ParallaxSupportView.ParallaxSupportViewProvider<ParallaxProvider.ViewHolder>{

        class ViewHolder extends ParallaxSupportView.ViewHolder{
            public ImageView mImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView)itemView;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(int type) {
            ImageView iv = new ImageView(MainActivity.this);
            iv.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(htv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mImageView.setBackgroundResource(R.mipmap.ic_launcher);

        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
```

3.在代码中find该组件，并且设置provider。

``` java
    ParallaxSupportView parallaxSupportView = (ParallaxSupportView) findViewById(R.id.parallaxSv);
    parallaxSupportView.setProvider(new ParallaxProvider());
```

## 高级ParallaxSupportView使用

1.ParallaxSupportViewProvider

ParallaxSupportView支持你构建多种不同的View，为了能这么做，你需要重写ParallaxSupportViewProvider中的两个个方法：

* public int getItemTypeCount()；用于返回type类型的数量
* public int getItemType(int position)；用于返回type类型

2.动画拦截

ParallaxSupportView支持你对动画进行拦截，你可以实现自己想要的动画特效而不一定使用ParallaxSupportView自带的默认动画，回调的View为当前即将执行动画的View，方法返回真会导致默认动画不会继续执行：

``` java
  mParallaxSupportView.setAnimInterceptor(new ParallaxSupportView.AnimInterceptor() {
            @Override
            public boolean anim(View view) {
                return false;
            }
        });
```

## 反馈

如果使用上有问题或者需要提交代码，请直接提issue，谢谢。

## Developed By

 * Linhonghong - <linhh90@163.com>

## License
Copyright 2016 LinHongHong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
