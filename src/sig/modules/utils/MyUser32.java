package sig.modules.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

public interface MyUser32 extends User32 {
    final User32 INSTANCE = (User32) Native.loadLibrary ("user32", User32.class);
    boolean ShowWindow(HWND hWnd, int nCmdShow);
    boolean SetForegroundWindow(HWND hWnd);
    HWND FindWindowA(String lpClassName, String lpWindowName);

}
