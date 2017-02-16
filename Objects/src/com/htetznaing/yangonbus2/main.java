package com.htetznaing.yangonbus2;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.yangonbus2", "com.htetznaing.yangonbus2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.yangonbus2", "com.htetznaing.yangonbus2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.yangonbus2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _ad2 = null;
public anywheresoftware.b4a.objects.WebViewWrapper _wb = null;
public uk.co.martinpearman.b4a.webviewsettings.WebViewSettings _wv = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _interstitial = null;
public de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper _fb = null;
public de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper _fb1 = null;
public anywheresoftware.b4a.object.XmlLayoutBuilder _res = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b = null;
public b4a.util.BClipboard _copy = null;
public com.AB.ABZipUnzip.ABZipUnzip _zip = null;
public static String _sd = "";
public anywheresoftware.b4a.samples.httputils2.httpjob _d = null;
public static String _url1 = "";
public MLfiles.Fileslib.MLfiles _ml = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public com.htetznaing.yangonbus2.how _how = null;
public com.htetznaing.yangonbus2.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (how.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 41;BA.debugLine="Activity.LoadLayout(\"lay1\")";
mostCurrent._activity.LoadLayout("lay1",mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="fb.Icon = res.GetDrawable(\"ic_add_white_24dp\")";
mostCurrent._fb.setIcon(mostCurrent._res.GetDrawable("ic_add_white_24dp"));
 //BA.debugLineNum = 43;BA.debugLine="fb.HideOffset = 100%y - fb.Top";
mostCurrent._fb.setHideOffset((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-mostCurrent._fb.getTop()));
 //BA.debugLineNum = 44;BA.debugLine="fb.Hide(False)";
mostCurrent._fb.Hide(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 45;BA.debugLine="fb.Show(True)";
mostCurrent._fb.Show(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 47;BA.debugLine="fb1.Icon = res.GetDrawable(\"about\")";
mostCurrent._fb1.setIcon(mostCurrent._res.GetDrawable("about"));
 //BA.debugLineNum = 48;BA.debugLine="fb1.HideOffset = 100%y - fb.Top";
mostCurrent._fb1.setHideOffset((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-mostCurrent._fb.getTop()));
 //BA.debugLineNum = 49;BA.debugLine="fb1.Hide(False)";
mostCurrent._fb1.Hide(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 50;BA.debugLine="fb1.Show(True)";
mostCurrent._fb1.Show(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 52;BA.debugLine="wb.Initialize(\"wb\")";
mostCurrent._wb.Initialize(mostCurrent.activityBA,"wb");
 //BA.debugLineNum = 54;BA.debugLine="If File.Exists(File.DirRootExternal  & \"/.YangonB";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.YangonBus/yangonbus.com/","index.html")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 55;BA.debugLine="ToastMessageShow(\"Using in Online Mode\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Using in Online Mode",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 56;BA.debugLine="wb.loadUrl (\"http://www.yangonbus.com\")";
mostCurrent._wb.LoadUrl("http://www.yangonbus.com");
 //BA.debugLineNum = 57;BA.debugLine="ProgressDialogShow(\"Loading...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Loading...");
 }else {
 //BA.debugLineNum = 59;BA.debugLine="ToastMessageShow(\"Using in Offline Mode\",True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Using in Offline Mode",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 60;BA.debugLine="wb.loadUrl (\"file:///\" & File.DirRootExternal & \"";
mostCurrent._wb.LoadUrl("file:///"+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.YangonBus/yangonbus.com/index.html");
 //BA.debugLineNum = 61;BA.debugLine="ProgressDialogShow(\"Loading...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Loading...");
 };
 //BA.debugLineNum = 63;BA.debugLine="Activity.AddView(wb,0%x,0%y,100%x,100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._wb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 64;BA.debugLine="wv.setDisplayZoomControls(wb, False)";
mostCurrent._wv.setDisplayZoomControls((android.webkit.WebView)(mostCurrent._wb.getObject()),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-4173348573";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/6966540950");
 //BA.debugLineNum = 68;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 69;BA.debugLine="Activity.AddView(Banner,0%x,100%y - 50dip,100%x,5";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 71;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-pu";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/8443274158");
 //BA.debugLineNum = 72;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 73;BA.debugLine="ad2.Initialize(\"ad2\",300000)";
_ad2.Initialize(processBA,"ad2",(long) (300000));
 //BA.debugLineNum = 74;BA.debugLine="ad2.Enabled = True";
_ad2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 77;BA.debugLine="Activity.AddMenuItem(\"Offline Mode\",\"uo\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("Offline Mode"),"uo");
 //BA.debugLineNum = 78;BA.debugLine="Activity.AddMenuItem(\"Online Mode\",\"om\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("Online Mode"),"om");
 //BA.debugLineNum = 79;BA.debugLine="Activity.AddMenuItem(\"Restart\",\"rf\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("Restart"),"rf");
 //BA.debugLineNum = 80;BA.debugLine="Activity.AddMenuItem(\"Check Update\",\"cu\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("Check Update"),"cu");
 //BA.debugLineNum = 81;BA.debugLine="Activity.AddMenuItem(\"More App\",\"ma\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("More App"),"ma");
 //BA.debugLineNum = 82;BA.debugLine="Activity.AddMenuItem(\"Exit App\",\"ea\")";
mostCurrent._activity.AddMenuItem((java.lang.CharSequence)("Exit App"),"ea");
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
String _url = "";
 //BA.debugLineNum = 193;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 194;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 195;BA.debugLine="Log(wb.Url)";
anywheresoftware.b4a.keywords.Common.Log(mostCurrent._wb.getUrl());
 //BA.debugLineNum = 196;BA.debugLine="Dim Url As String";
_url = "";
 //BA.debugLineNum = 197;BA.debugLine="Url = wb.Url";
_url = mostCurrent._wb.getUrl();
 //BA.debugLineNum = 198;BA.debugLine="If Url.EndsWith(\".YangonBus/yangonbus.com/index.";
if (_url.endsWith(".YangonBus/yangonbus.com/index.html") || (_url).equals("http://www.yangonbus.com/")) { 
 //BA.debugLineNum = 199;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 201;BA.debugLine="wb.Back";
mostCurrent._wb.Back();
 //BA.debugLineNum = 202;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 };
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 187;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 184;BA.debugLine="Log(wb.Url)";
anywheresoftware.b4a.keywords.Common.Log(mostCurrent._wb.getUrl());
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
return "";
}
public static String  _ad2_tick() throws Exception{
 //BA.debugLineNum = 171;BA.debugLine="Sub ad2_Tick";
 //BA.debugLineNum = 172;BA.debugLine="If Interstitial.Ready Then Interstitial.Show";
if (mostCurrent._interstitial.getReady()) { 
mostCurrent._interstitial.Show();};
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _b_click() throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Sub b_Click";
 //BA.debugLineNum = 130;BA.debugLine="If File.Exists(File.DirRootExternal  & \"/MyanmarA";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/MyanmarAndroidApps","how.jpg")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 131;BA.debugLine="File.Copy(File.DirAssets,\"how.zip\",File.DirRootExt";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"how.zip",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"how.zip");
 //BA.debugLineNum = 132;BA.debugLine="zip.ABUnzip(sd & \"how.zip\",sd & \"MyanmarAndroidA";
mostCurrent._zip.ABUnzip(mostCurrent._sd+"how.zip",mostCurrent._sd+"MyanmarAndroidApps");
 //BA.debugLineNum = 133;BA.debugLine="File.Delete(File.DirRootExternal , \"how.zip\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"how.zip");
 //BA.debugLineNum = 134;BA.debugLine="StartActivity(How)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._how.getObject()));
 }else {
 //BA.debugLineNum = 136;BA.debugLine="StartActivity(How)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._how.getObject()));
 };
 //BA.debugLineNum = 138;BA.debugLine="End Sub";
return "";
}
public static String  _cu_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _ii = null;
 //BA.debugLineNum = 92;BA.debugLine="Sub cu_Click";
 //BA.debugLineNum = 93;BA.debugLine="Dim ii As Intent";
_ii = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 94;BA.debugLine="ii.Initialize(ii.ACTION_VIEW,\"https://play.google";
_ii.Initialize(_ii.ACTION_VIEW,"https://play.google.com/store/apps/details?id=com.htetznaing.yangonbus");
 //BA.debugLineNum = 95;BA.debugLine="StartActivity(ii)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_ii.getObject()));
 //BA.debugLineNum = 96;BA.debugLine="ii.WrapAsIntentChooser(\"Open With..\")";
_ii.WrapAsIntentChooser("Open With..");
 //BA.debugLineNum = 97;BA.debugLine="End Sub";
return "";
}
public static String  _ea_click() throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub ea_Click";
 //BA.debugLineNum = 111;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _fb_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 140;BA.debugLine="Sub fb_Click";
 //BA.debugLineNum = 141;BA.debugLine="ToastMessageShow(\"App Update နွင့္ App အသစ္ေတြ \"";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("App Update နွင့္ App အသစ္ေတြ "+anywheresoftware.b4a.keywords.Common.CRLF+"Facebook ကေနဖတ္ရူ့သိနိုင္ဖို့"+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps ကို Like ထားလိုက္ပါ။",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 142;BA.debugLine="Try";
try { //BA.debugLineNum = 144;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 146;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 147;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 151;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 152;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://m.facebook.";
_i.Initialize(_i.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 154;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 };
 //BA.debugLineNum = 157;BA.debugLine="End Sub";
return "";
}
public static String  _fb1_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
 //BA.debugLineNum = 159;BA.debugLine="Sub fb1_Click";
 //BA.debugLineNum = 160;BA.debugLine="Dim ShareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 161;BA.debugLine="copy.clrText";
mostCurrent._copy.clrText(mostCurrent.activityBA);
 //BA.debugLineNum = 162;BA.debugLine="copy.setText(\"#YangonBus Free Android App!\" &CRLF";
mostCurrent._copy.setText(mostCurrent.activityBA,"#YangonBus Free Android App!"+anywheresoftware.b4a.keywords.Common.CRLF+"Download Free at : http://www.htetznaing.com/YangonBusApp/"+anywheresoftware.b4a.keywords.Common.CRLF+" #Ht3tzN4ing"+anywheresoftware.b4a.keywords.Common.CRLF+" #MyanmarAndroidApps");
 //BA.debugLineNum = 163;BA.debugLine="ShareIt.Initialize (ShareIt.ACTION_SEND,\"\")";
_shareit.Initialize(_shareit.ACTION_SEND,"");
 //BA.debugLineNum = 164;BA.debugLine="ShareIt.SetType (\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 165;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.TEXT\",cop";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(mostCurrent._copy.getText(mostCurrent.activityBA)));
 //BA.debugLineNum = 166;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.SUBJECT\",";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("Get Free!!"));
 //BA.debugLineNum = 167;BA.debugLine="ShareIt.WrapAsIntentChooser(\"Share App Via...\")";
_shareit.WrapAsIntentChooser("Share App Via...");
 //BA.debugLineNum = 168;BA.debugLine="StartActivity (ShareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim wb As WebView";
mostCurrent._wb = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim wv As WebViewSettings";
mostCurrent._wv = new uk.co.martinpearman.b4a.webviewsettings.WebViewSettings();
 //BA.debugLineNum = 25;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Interstitial As InterstitialAd";
mostCurrent._interstitial = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim fb,fb1 As FloatingActionButton";
mostCurrent._fb = new de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper();
mostCurrent._fb1 = new de.amberhome.objects.floatingactionbutton.FloatingActionButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim res As XmlLayoutBuilder";
mostCurrent._res = new anywheresoftware.b4a.object.XmlLayoutBuilder();
 //BA.debugLineNum = 30;BA.debugLine="Dim b As Button";
mostCurrent._b = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim copy As BClipboard";
mostCurrent._copy = new b4a.util.BClipboard();
 //BA.debugLineNum = 32;BA.debugLine="Dim zip As ABZipUnzip";
mostCurrent._zip = new com.AB.ABZipUnzip.ABZipUnzip();
 //BA.debugLineNum = 34;BA.debugLine="Dim sd As String = File.DirRootExternal & \"/\"";
mostCurrent._sd = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/";
 //BA.debugLineNum = 35;BA.debugLine="Dim d As HttpJob";
mostCurrent._d = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 36;BA.debugLine="Dim url1 As String";
mostCurrent._url1 = "";
 //BA.debugLineNum = 37;BA.debugLine="Dim ml As MLfiles";
mostCurrent._ml = new MLfiles.Fileslib.MLfiles();
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _interstitial_adclosed() throws Exception{
 //BA.debugLineNum = 224;BA.debugLine="Sub Interstitial_AdClosed";
 //BA.debugLineNum = 225;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd();
 //BA.debugLineNum = 226;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper _out = null;
 //BA.debugLineNum = 207;BA.debugLine="Sub JobDone (Job As HttpJob)";
 //BA.debugLineNum = 208;BA.debugLine="If Job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 209;BA.debugLine="Dim out As OutputStream";
_out = new anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper();
 //BA.debugLineNum = 210;BA.debugLine="out = File.OpenOutput(File.DirRootExternal,\"Yang";
_out = anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"YangonBus.zip",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 211;BA.debugLine="File.Copy2(Job.GetInputStream, out)";
anywheresoftware.b4a.keywords.Common.File.Copy2((java.io.InputStream)(_job._getinputstream().getObject()),(java.io.OutputStream)(_out.getObject()));
 //BA.debugLineNum = 212;BA.debugLine="out.Close";
_out.Close();
 //BA.debugLineNum = 213;BA.debugLine="ml.rmrf(File.DirRootExternal & \".YangonBus\")";
mostCurrent._ml.rmrf(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+".YangonBus");
 //BA.debugLineNum = 214;BA.debugLine="zip.ABUnzip(sd & \"YangonBus.zip\",sd & \".YangonBu";
mostCurrent._zip.ABUnzip(mostCurrent._sd+"YangonBus.zip",mostCurrent._sd+".YangonBus");
 //BA.debugLineNum = 215;BA.debugLine="File.Delete(File.DirRootExternal,\"YangonBus.zip\"";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"YangonBus.zip");
 //BA.debugLineNum = 216;BA.debugLine="Msgbox(\"Congratulations!\" & CRLF & \"Now! you can";
anywheresoftware.b4a.keywords.Common.Msgbox("Congratulations!"+anywheresoftware.b4a.keywords.Common.CRLF+"Now! you can using Offline!","Completed",mostCurrent.activityBA);
 //BA.debugLineNum = 217;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 218;BA.debugLine="StartActivity(Me)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,main.getObject());
 }else {
 //BA.debugLineNum = 220;BA.debugLine="Msgbox(\"Error\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Error","",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 222;BA.debugLine="End Sub";
return "";
}
public static String  _ma_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _ii = null;
 //BA.debugLineNum = 85;BA.debugLine="Sub ma_Click";
 //BA.debugLineNum = 86;BA.debugLine="Dim ii As Intent";
_ii = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 87;BA.debugLine="ii.Initialize(ii.ACTION_VIEW,\"https://play.google";
_ii.Initialize(_ii.ACTION_VIEW,"https://play.google.com/store/apps/dev?id=7613861041148492843");
 //BA.debugLineNum = 88;BA.debugLine="StartActivity(ii)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_ii.getObject()));
 //BA.debugLineNum = 89;BA.debugLine="ii.WrapAsIntentChooser(\"Open With..\")";
_ii.WrapAsIntentChooser("Open With..");
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _om_click() throws Exception{
 //BA.debugLineNum = 99;BA.debugLine="Sub om_Click";
 //BA.debugLineNum = 100;BA.debugLine="wb.loadUrl (\"http://www.yangonbus.com\")";
mostCurrent._wb.LoadUrl("http://www.yangonbus.com");
 //BA.debugLineNum = 101;BA.debugLine="ProgressDialogShow(\"Loading...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Loading...");
 //BA.debugLineNum = 102;BA.debugLine="ToastMessageShow(\"Using in Online Mode\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Using in Online Mode",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
how._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim ad2 As Timer";
_ad2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _rf_click() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub rf_CLick";
 //BA.debugLineNum = 106;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 107;BA.debugLine="StartActivity(Me)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,main.getObject());
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _uo_click() throws Exception{
 //BA.debugLineNum = 114;BA.debugLine="Sub uo_Click";
 //BA.debugLineNum = 115;BA.debugLine="If File.Exists(File.DirRootExternal  & \"/.YangonB";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.YangonBus/yangonbus.com/","index.html")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 116;BA.debugLine="wb.loadUrl (\"file:///\" & File.DirRootExternal &";
mostCurrent._wb.LoadUrl("file:///"+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/.YangonBus/yangonbus.com/index.html");
 //BA.debugLineNum = 117;BA.debugLine="ProgressDialogShow(\"Loading...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Loading...");
 //BA.debugLineNum = 118;BA.debugLine="ToastMessageShow(\"Using in Offline Mode\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Using in Offline Mode",anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 121;BA.debugLine="d.Initialize(\"d\",Me)";
mostCurrent._d._initialize(processBA,"d",main.getObject());
 //BA.debugLineNum = 122;BA.debugLine="url1 =\"http://mmapp.ml/YBS/YangonBus.zip\"";
mostCurrent._url1 = "http://mmapp.ml/YBS/YangonBus.zip";
 //BA.debugLineNum = 123;BA.debugLine="d.Download(url1)";
mostCurrent._d._download(mostCurrent._url1);
 //BA.debugLineNum = 124;BA.debugLine="ProgressDialogShow(\"Downloading data...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Downloading data...");
 };
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static boolean  _wb_overrideurl(String _url) throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub wb_OverrideUrl (url As String)  As Boolean";
 //BA.debugLineNum = 176;BA.debugLine="ProgressDialogShow(\"Loading...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"Loading...");
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return false;
}
public static String  _wb_pagefinished(String _url) throws Exception{
 //BA.debugLineNum = 179;BA.debugLine="Sub wb_PageFinished (url As String)";
 //BA.debugLineNum = 180;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
}
