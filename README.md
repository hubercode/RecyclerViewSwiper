# RecyclerViewSwiper
Library for adding buttons on swipe gestures for android recycler views.

<img src="https://github.com/michaelhuber88/RecyclerViewSwiper/blob/master/screenshot_1.png" width="300"> <img src="https://github.com/michaelhuber88/RecyclerViewSwiper/blob/master/screenshot_2.png" width="300">

## Add the library to your project:

[![](https://jitpack.io/v/michaelhuber88/RecyclerViewSwiper.svg)](https://jitpack.io/#michaelhuber88/RecyclerViewSwiper)

### gradle:
Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add the dependency to your app build.gradle
```
dependencies {
  implementation 'com.github.michaelhuber88:RecyclerViewSwiper:1.0'
}  
```  
### maven:
Add the JitPack repository to your build file
```
<repositories>
  <repository>
	  <id>jitpack.io</id>
	  <url>https://jitpack.io</url>
	</repository>
</repositories>
```
Add the dependency:
```
<dependency>
  <groupId>com.github.michaelhuber88</groupId>
  <artifactId>RecyclerViewSwiper</artifactId>
  <version>Tag</version>
</dependency>
```  

## How to use it:
```java
RecyclerViewSwiper swiper = new RecyclerViewSwiper(this, this.recyclerView) {
    @Override
    public void initSwipeButtonRight(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {

        // DELETE

        swipeButtons.add(new SwipeButton(MainActivity.this, "DELETE", Color.RED, new SwipeButtonClickListener() {
            @Override
            public void onClick(int position) {
                items.remove(position);
                adapter.notifyDataSetChanged();
            }
        }));

        // COPY

        swipeButtons.add(new SwipeButton(MainActivity.this, "COPY", Color.BLACK, new SwipeButtonClickListener() {
            @Override
            public void onClick(int position) {
                items.add(items.get(position));
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, items.get(position) + " added", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void initSwipeButtonLeft(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {

        // INFO

        swipeButtons.add(new SwipeButton(MainActivity.this, "INFO", Color.BLUE, new SwipeButtonClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, items.get(position), Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        }));
    }
};
swiper.setButtonWidth(200);
```
