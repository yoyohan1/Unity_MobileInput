using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mopsicus.Plugins;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using NiceJson;
#if UNITY_IOS
using System.Runtime.InteropServices;
#endif


namespace yoyohan
{

    /// <summary>
    /// 描述：
    /// 功能：
    /// 作者：yoyohan
    /// 创建时间：2020-12-02 16:22:07
    /// </summary>
    public class KeyHeightMono : MonoBehaviour
    {
        
        public RectTransform main;
        public RectTransform maskRect;

        private Transform canvas;
        private Vector2 prePos;
        private float canvasHeight;

        private MobileInputField mobileInputField;
        private float keyboardHeight;

        private bool isSetMaskRect = false;

#if UNITY_EDITOR
        private RectTransform testKeyboard;
#endif


#if UNITY_IOS
    [DllImport("__Internal")]
    private static extern void excuteOther_iOS(string json);
#endif


        void Start()
        {
            canvas = main.root;
            prePos = main.anchoredPosition;
            canvasHeight = canvas.GetComponent<RectTransform>().rect.height;//这一步不能放在Awake 会造成数值不准确 因为Canvas的Awake调用顺序的原因
            SetMaskRect();
#if UNITY_EDITOR
            //测试软键盘弹出
            testKeyboard = GameTools.GetNewImage(canvas, "TestKeyboard", Vector3.zero, Vector2.zero, Vector2.zero, new Vector2(1, 0), new Vector2(0.5f, 0), Vector3.zero, Vector3.one).GetComponent<RectTransform>();

            Button closeBtn = GameTools.GetNewButton(testKeyboard, "closeBtn", "关闭软键盘", Vector3.zero, new Vector2(100, 50), Vector2.one, Vector2.one, Vector2.one, Vector3.zero, Vector3.one);
            closeBtn.onClick.AddListener(() => MobileInput.OnShowKeyboard(false, 0));

            Text text = GameTools.GetNewText(testKeyboard, "text", "我是测试移动端的虚拟键盘！", Vector3.zero, new Vector2(500, 50), new Vector2(0.5f, 1), new Vector2(0.5f, 1), new Vector2(0.5f, 1), Vector3.zero, Vector3.one);
            text.fontSize = 30;
#endif
        }


        void SetMaskRect()
        {
            JsonObject data = new JsonObject();
            data["msg"] = "SET_MASK";

            if (maskRect != null)
            {
                Rect rect = MobileInputField.GetScreenRectFromRectTransform(maskRect);
                //Debug.Log(rect.x);
                //Debug.Log(rect.y);
                //Debug.Log(rect.width);
                //Debug.Log(rect.height);
                data["x"] = MobileInputField.InvariantCultureString(rect.x / Screen.width);
                data["y"] = MobileInputField.InvariantCultureString(rect.y / Screen.height);
                data["width"] = MobileInputField.InvariantCultureString(rect.width / Screen.width);
                data["height"] = MobileInputField.InvariantCultureString(rect.height / Screen.height);
                //data["screenWidth"] = MobileInputField.InvariantCultureString(Screen.width);
                //data["screenHeight"] = MobileInputField.InvariantCultureString(Screen.height);
                //data["canvasHeight"] = MobileInputField.InvariantCultureString(canvasHeight);
            }
            else
            {
                data["x"] = 0;
                data["y"] = 0;
                data["width"] = 1;
                data["height"] = 1;
            }

            string json = data.ToJsonString();
#if UNITY_EDITOR
            Debug.Log("设置输入框的MASK属性:" + json);
#elif UNITY_ANDROID
            using (AndroidJavaObject javaObject = new AndroidJavaObject("ru.mopsicus.mobileinput.Plugin"))
            {
                javaObject.CallStatic("excuteOther", json);
            }
#elif UNITY_IOS
            excuteOther_iOS(json);
#endif

            isSetMaskRect = true;
        }

        void OnFocus(int id)
        {
            mobileInputField = (MobileInputField)MobileInput.GetReceiver(id);

            if (keyboardHeight > 0)
            {
                ShowUIHeight();
            }
        }

        void OnShowKeyboard(bool isShow, int height)
        {
            Debug.Log("OnShowKeyboard开始响应----------" + isShow + " height:" + height);

            if (isShow)
            {
                keyboardHeight = height < Screen.height * 0.33f ? (int)(Screen.height * 0.33f) : height;
                ShowUIHeight();
            }
            else
            {
                keyboardHeight = 0;
                HideUIHeight();
            }
        }

        void ShowUIHeight()
        {
            if (mobileInputField == null)
            {
                Debug.Log("没获取到被Focus的mobileInputField----------");
                return;
            }
            RectTransform input = mobileInputField.GetComponent<RectTransform>();


            Vector2 temp = main.anchoredPosition;

            main.anchoredPosition = prePos;
            float uiKeyHeight = keyboardHeight / (float)Screen.height * canvasHeight;

            //获取B的世界坐标
            Vector3 wp = input.transform.TransformPoint(Vector3.zero);
            //将wp坐标转换到Canvas的局部坐标下
            Vector3 lp = canvas.transform.InverseTransformPoint(wp);
            float componentLowerY = lp.y - input.rect.height * input.pivot.y * main.localScale.y + canvasHeight / 2;

            float clamp = componentLowerY - (uiKeyHeight + 50);//给键盘预留50的高度

            main.anchoredPosition = temp;

            if (clamp < 0)
            {
                LeanTween.value(main.anchoredPosition.y, prePos.y - clamp, 0.3f).setOnUpdate(v => main.anchoredPosition = new Vector2(prePos.x, v));
            }
            else
            {
                //下落最多回到最初位置 效果比较好
                LeanTween.value(main.anchoredPosition.y, prePos.y, 0.3f).setOnUpdate(v => main.anchoredPosition = new Vector2(prePos.x, v));
            }

#if UNITY_EDITOR
            testKeyboard.sizeDelta = new Vector2(0, uiKeyHeight);
            //Debug.Log(uiKeyHeight + " " + keyboardHeight + " " + canvasHeight + " " + Screen.height + " " + canvas.GetComponent<RectTransform>().sizeDelta.y + " " + canvas.GetComponent<RectTransform>().localScale.y);
#endif

        }

        void HideUIHeight()
        {
            LeanTween.value(main.anchoredPosition.y, prePos.y, 0.3f).setOnUpdate(v => main.anchoredPosition = new Vector2(prePos.x, v));

#if UNITY_EDITOR
            LeanTween.value(testKeyboard.sizeDelta.y, 0, 0.3f).setOnUpdate(v => testKeyboard.sizeDelta = new Vector2(testKeyboard.sizeDelta.x, v));
#endif
        }

        IEnumerator IESetMaskRectNextFrame()
        {
            yield return null;
            SetMaskRect();
        }

        void OnEnable()
        {
            if (isSetMaskRect)
            {
                //在禁用之后重新启用Screen.Width会获得奇怪的数据
                StartCoroutine(IESetMaskRectNextFrame());
            }

            MobileInput.OnShowKeyboard += OnShowKeyboard;
            MobileInput.OnFocus += OnFocus;
        }

        void OnDisable()
        {
            MobileInput.OnShowKeyboard -= OnShowKeyboard;
            MobileInput.OnFocus -= OnFocus;
        }

#if UNITY_EDITOR
        private void LateUpdate()
        {
            //使用MouseButtonUp和LateUpdate 而不使用MouseButtonDown和Update会造成bug 困扰
            //是为了防止点击main上的按钮  而MouseButtonDown先调用下移main再调用Button的响应事件 按钮下移造成点不到按钮 不会触发按钮的点击事件
            if (Input.GetMouseButtonUp(0) && keyboardHeight != 0)
            {
                if (RectTransformUtility.RectangleContainsScreenPoint(testKeyboard, Input.mousePosition) == false)
                {
                    if (EventSystem.current != null && EventSystem.current.currentSelectedGameObject != null && EventSystem.current.currentSelectedGameObject.GetComponent<MobileInputField>() != null)
                    {
                        return;
                    }

                    MobileInput.OnShowKeyboard(false, 0);
                }
            }
        }
#endif


    }
}