Type=Activity
Version=6.5
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: false
#End Region

Sub Process_Globals
End Sub

Sub Globals
	Dim wb As WebView
Dim wv As WebViewSettings
End Sub

Sub Activity_Create(FirstTime As Boolean)
	wb.Initialize("wb")
	Activity.AddView(wb,0%x,0%y,100%x,100%y)
	wb.LoadUrl("file:///" & File.DirRootExternal & "/MyanmarAndroidApps/how.html")
	wv.setDisplayZoomControls(wb , False)
End Sub