' 无窗口运行 bat（参数1：bat 完整路径）
Set sh = CreateObject("WScript.Shell")
batPath = WScript.Arguments(0)
If batPath = "" Then
    WScript.Quit 1
End If
sh.Run "cmd /c """ & batPath & """", 0, False
