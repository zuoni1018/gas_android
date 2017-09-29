简化网络请求操作.  
简化adapter实现过程.  
简化listview上拉加载,下拉刷新更多功能.  
********
参考例子:  
[云社区](https://github.com/oldfeel/YunCommunityAndroid)

### 以导入 module 的方式导入

File --> New --> Import Module...

### 以 git submodule 的方式导入

````
git submodule add git@git.oschina.net:oldfeel/AndroidUtils.git
````

导入项目后在 settings.gradle 中添加

````
include ':AndroidUtils', ":multi-image-selector"
project(':AndroidUtils').projectDir = new File('AndroidUtils/AndroidUtils')
project(':multi-image-selector').projectDir = new File('AndroidUtils/multi-image-selector')
````