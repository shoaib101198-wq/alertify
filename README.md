# Alertify (Android)

Jab kisi *selected* app se notification aaye, Alertify ek **looping alarm** bajata hai
(jaise alarm clock) jab tak aap **STOP** na dabao — phone lock ho ya silent mode mein ho tab bhi.

## Kaise build karein
1. **Android Studio** (latest) open karo → `Open` → ye `Alertify` folder select karo.
2. Pehli baar Gradle sync chalega (internet chahiye — Gradle 8.7 + dependencies download honge).
3. Phone ko USB se laga ke (USB debugging on) ya emulator pe **Run ▶** dabao.
   - Ya APK ke liye: `Build > Build Bundle(s)/APK(s) > Build APK(s)`.

## Pehli baar app khol ke
1. **"Grant notification access"** button dabao → list mein **Alertify** ko ON karo.
   (Ye permission zaroori hai — iske bagair app notifications detect nahi kar sakti.)
2. Wapas aa kar jin apps ka alarm chahiye unke switch ON kar do (e.g. WhatsApp, Gmail).
3. Bas. Ab un apps ki har notification pe alarm bajega.

## Test karne ka tareeqa
- Apni list se koi app (jaise WhatsApp) ON karo → us app se khud ko message bhejo
  ya koi notification trigger karo → alarm bajna chahiye → STOP dabao.

## Notes / limitations
- **Android 14+**: agar full-screen alarm screen na khule, to phone Settings me
  Alertify ke liye *"Full screen intents / Display over other apps"* allow kar dena.
  Sound phir bhi bajega heads-up notification ke through.
- Battery optimization: kuch phones (Xiaomi, Oppo, Samsung) background services ko maar dete hain.
  Reliability ke liye Settings > Battery me Alertify ko **"Unrestricted / Don't optimize"** kar dena.
- `QUERY_ALL_PACKAGES` permission sirf installed apps ki list dikhane ke liye hai.
  Agar Play Store pe daalna ho to iske liye justification chahiye hoga.

## Customization (agar chaho)
- Apni custom alarm tone: `AlarmPlayer.kt` me `RingtoneManager` ki jagah
  `R.raw.your_tone` (app/src/main/res/raw/ me mp3 daal ke) use kar sakte ho.
- Vibration pattern: `AlarmPlayer.startVibration()` me `pattern` array change karo.

Built for HighSkyGo.
