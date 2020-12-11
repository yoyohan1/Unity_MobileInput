# UnityMobileInput

### 介绍

1. github地址：https://github.com/mopsicus/UnityMobileInput

2. 功能
   - 去掉移动端InputField自带的丑陋的输入box。
   - 根据键盘高度自动调节输入框在UGUI中的位置（使用KeyHeightMono.cs）。
   - 使用原生自带的输入框贴到InputField上，可以实现复制、粘贴、密码本、Clear、Done、Search、Return。
   
3. 修复原版的Bug和改进

   + ```c#
     //改进：在MobileInputField.cs上增加该变量
     public string text { get { return Text;} set { Text = value;} }
     ```
     
   + ```c#
     //修复bug：在MobileInputField.cs上修改
     //在此处修改为使用inputfield.text 之前使用inputfield.textcompoent 在密码类型的inputfield上 会出现bug
     this.SetTextNative(this._inputObject.text);
     ```
     
   + ```c#
       //改进：在MobileInputField.cs上修改
       //把原生输入框的背景的alpha设置为0 不然会有白底
       _config.BackgroundColor = new Color(_inputObject.colors.normalColor.r, _inputObject.colors.normalColor.g, _inputObject.colors.normalColor.b, 0);
       // _config.BackgroundColor =_inputObject.colors.normalColor;
       ```
       
    + ```c#
       //修复bug：在MobileInputField.cs上修改 
       //ios按home键之后再切回应用Input消失了
       private void OnApplicationFocus (bool hasFocus) {
           if (!_isMobileInputCreated || !this.Visible) {
               return;
           }
           //this.SetVisible (hasFocus);
       }
       ```
    + ```c#
    	//改进：修改MobileInputField.cs和Plugins.cs 增加Plugins的判断自动生成Plugins 无需提前挂载Plugins脚本
        //1.在MobileInputField.cs的Awake中加入
        if (Plugins.instance == null){
            GameObject plugins = new GameObject("Plugins");
            plugins.AddComponent<Plugins>();
       }
       //2.在Plugins.cs的Awake中加入
       if (instance != null){
           DestroyImmediate(gameObject);
           return;
       }
       instance = this;
       ```
   + 修改MobileInput.cs见[MobileInput](https://github.com/yoyohan1/Unity_MobileInput/blob/master/UnityMobileInput/Scripts/MobileInput.cs)  增加onFocus事件 解决了点不同输入框时 输入框需要调整高度到UGUI可见位置
   
   + 修改MobileInputReceiver.cs见[MobileInput](https://github.com/yoyohan1/Unity_MobileInput/blob/master/UnityMobileInput/Scripts/MobileInput.cs) 增加UNITY_EDITOR模式显示模拟键盘
   
   + 增加KeyHeightMono.cs见[KeyHeightMono](https://github.com/yoyohan1/Unity_MobileInput/blob/master/UnityMobileInput/Scripts/KeyHeightMono.cs) 以使用Android和iOS端根据键盘高度自动调节输入框在UGUI中的位置
   
   + 修改KeyboardListener.java见[KeyboardListener](https://github.com/yoyohan1/Unity_MobileInput/blob/master/AndroidJar_MobileInput/mobileinput/src/main/java/ru/mopsicus/mobileinput/KeyboardListener.java) 解决了安卓输入法切换时键盘高度变化
   
   + iOS的bug 横屏第一个场景中使用该插件的InputField会不出现原生UI 除非打开闪屏动画或者打开竖屏。（测试了一天时间才发现造成bug的原因）解决方法为 打开闪屏动画 把闪屏动画列表设为空。偶现横屏Home键再返回应用也会不出现输入框。
### 接入

##### Unity

1. 拷贝文件到对应目录

3. 在需要使用插件的InputField上添加MobileInputField.cs插件，并设置参数：

   + Search、Return、Done事件。可以绑定Done事件为MobileInputField.Hide()

     ​	示例1：使用Next事件绑定下一个MobileInputField的SetFoucs(true)事件 以激活下一个InputField。 

     ​	示例2：使用Done事件绑定自身MobileInputField的Hide()事件 。

   + iOS增加两个参数Clear、Done。Clear为输入框后边的X按钮 Done为输入框上方的按钮。

   + 自定义字体：拷贝TTF字体资源放到StreamingAssets  并替换default为字体的名称即可。
   
4. 在GameObject上挂载KeyHeightMono 实现根据键盘高度自动调节输入框在UGUI中的位置。

##### 安卓

1. 修改AndroidManifest.xml

   ```java
   <!--增加meta-data 在Unity页面中，如果希望能够使页面中的Android 控件具有点击事件，需要增加meta-data，值需设置为true-->
   <activity android:name="com.unity3d.player.UnityPlayerNativeActivity" android:label="@string/app_name"
       		...
               <meta-data android:name="unityplayer.ForwardNativeEventsToDalvik" android:value="true" />
               ...
   </activity>
                   
   <!--设置是否根据输入框高度自动调节View 不加时自动调节生效-->   
   activity ...  android:windowSoftInputMode="adjustNothing">
   			 ...
   </activity>           
   ```


##### iOS：

无

### 效果图

![image](https://github.com/yoyohan1/Unity_MobileInput/blob/master/移动端InputField演示.gif)

