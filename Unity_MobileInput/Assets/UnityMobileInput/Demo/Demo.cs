using Mopsicus.Plugins;
using UnityEngine;
using UnityEngine.UI;

namespace yoyohan.UnityMobileInputDemo
{

    public class Demo : MonoBehaviour
    {

        public MobileInputField InputText;

        void Start()
        {
            MobileInput.OnShowKeyboard += OnShowKeyboard;

            GameObject DeleteAccountPanel = GameObject.Find("Canvas").transform.Find("DeleteAccountPanel").gameObject;
            GameObject DeleteAccountPanel2 = GameObject.Find("Canvas").transform.Find("DeleteAccountPanel2").gameObject;
            GameObject.Find("OpenCloseButton").GetComponent<Button>().onClick.AddListener(() =>
            {
                DeleteAccountPanel.SetActive(!DeleteAccountPanel.activeInHierarchy);
            });
            GameObject.Find("ShowHideButton").GetComponent<Button>().onClick.AddListener(() =>
            {
                DeleteAccountPanel2.SetActive(!DeleteAccountPanel2.activeInHierarchy);
                DeleteAccountPanel.SetActive(!DeleteAccountPanel2.activeInHierarchy);
            });
        }

        public void OnReturn()
        {
            Debug.Log("OnReturn action");
        }

        public void OnEdit(string text)
        {
            Debug.LogFormat("OnEdit action. Text: {0}", text);
        }

        public void OnEndEdit(string text)
        {
            Debug.LogFormat("OnEndEdit action. Text: {0}", text);
        }

        public void SetTextData()
        {
            Debug.Log("SetTextData");
            InputText.Text = "Text by script";
        }

        void OnShowKeyboard(bool isShow, int height)
        {
            Debug.LogFormat("Keyboad action, show = {0}, height = {1}", isShow, height);
        }

        public void TestUU()
        {
            Debug.Log(GameObject.Find("GameObject").transform.localPosition);
        }

    }
}