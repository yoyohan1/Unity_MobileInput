# UnityMobileInput

### 介绍

1. github地址：https://github.com/mopsicus/UnityMobileInput

2. 功能
   - 去掉移动端InputField自带的丑陋的输入box。
   - 根据输入框高度自动调节主界面绘制View。
   - 使用原生自带的输入框贴到InputField上，可以实现复制、粘贴、密码本、Clear、Done、Search、Return。
   
3. 修复原版的Bug和改进

   + ```c#
     //改进：在MobileInputField上增加该变量
     public string text { get { return Text;} set { Text = value;} }
     ```
     
   + ```c#
     //修复bug：在MobileInputField上修改
     //在此处修改为使用inputfield.text 之前使用inputfield.textcompoent 在密码类型的inputfield上 会出现bug
     this.SetTextNative(this._inputObject.text);
     ```
     
   + ```c#
       //改进：把原生输入框的背景的alpha设置为0 不然会有白底
       _config.BackgroundColor = new Color(_inputObject.colors.normalColor.r, _inputObject.colors.normalColor.g, _inputObject.colors.normalColor.b, 0);
       // _config.BackgroundColor =_inputObject.colors.normalColor;
       ```
       
   + iOS的bug 横屏第一个场景中使用该插件的InputField会不出现原生UI 除非打开闪屏动画或者打开竖屏。（测试了一天时间才发现造成bug的原因）解决方法为 打开闪屏动画 把闪屏动画列表设为空。偶现横屏Home键再返回应用也会不出现输入框。
### 接入

##### Unity

1. 拷贝文件到对应目录

2. 新建GameObject命名为Plugins，并添加Plugins.cs

3. 在需要使用插件的InputField上添加MobileInputField.cs插件，并设置参数：

   + Search、Return、Done事件。可以绑定Done事件为MobileInputField.Hide()

     ​	示例1：使用Next事件绑定下一个MobileInputField的SetFoucs(true)事件 以激活下一个InputField。 

     ​	示例2：使用Done事件绑定自身MobileInputField的Hide()事件 。

   + iOS增加两个参数Clear、Done。Clear为输入框后边的X按钮 Done为输入框上方的按钮。

   + 自定义字体：拷贝TTF字体资源放到StreamingAssets  并替换default为字体的名称即可。

##### 安卓

1. 修改AndroidManifest.xml

   ```java
   <!--增加meta-data-->
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

高级用法：根据输入框高度自动调节View的代码

+ 步骤1.修改Plugin.java

    ```java
    layout = new RelativeLayout(activity);
    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    layout.setBackgroundColor(Color.rgb(0, 0, 0));
    layout.getBackground().setAlpha(1);
    group.addView(layout, params);
    ```

+ 步骤2.修改KeyboardListener.java见[KeyboardListener](https://github.com/yoyohan1/Unity_MobileInput/blob/master/AndroidJar_MobileInput/mobileinput/src/main/java/ru/mopsicus/mobileinput/KeyboardListener.java)

+ 步骤3.修改MobileInput.java见[MobileInput](https://github.com/yoyohan1/Unity_MobileInput/blob/master/AndroidJar_MobileInput/mobileinput/src/main/java/ru/mopsicus/mobileinput/MobileInput.java)

##### iOS：

高级用法：根据输入框高度自动调节View的代码

```objective-c
- (void)textFieldActive:(UITextField *)theTextField {
    NSMutableDictionary *msg = [[NSMutableDictionary alloc] init];
    [msg setValue:ON_FOCUS forKey:@"msg"];
    [self sendData:msg];
    
    //UITextField输入框随键盘弹出界面上移
    CGRect frame = theTextField.frame;
    int offSet = frame.origin.y + 70 - (mainViewController.view.frame.size.height - 216.0); //iphone键盘高度为216.iped键盘高度为352
    [UIView beginAnimations:@"ResizeForKeyboard" context:nil];
    [UIView setAnimationDuration:0.5f];
    //将试图的Y坐标向上移动offset个单位，以使线面腾出开的地方用于软键盘的显示
    NSLog(@"qqqqqqqqqqqqqqqqqqqqqqqqqq:%d",offSet);
    if (offSet > 0) {
        mainViewController.view.frame = CGRectMake(0.0f, -offSet, mainViewController.view.frame.size.width, mainViewController.view.frame.size.height);
        [UIView commitAnimations];
    }
}

- (void)textFieldInActive:(UITextField *)theTextField {
    NSMutableDictionary *msg = [[NSMutableDictionary alloc] init];
    [msg setValue:ON_UNFOCUS forKey:@"msg"];
    [self sendData:msg];
    
    //UITextField输入框随键盘弹出界面上移
    mainViewController.view.frame = CGRectMake(0, 0, mainViewController.view.frame.size.width, mainViewController.view.frame.size.height);
}
```

