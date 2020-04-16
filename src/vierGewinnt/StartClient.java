package vierGewinnt;

import java.awt.Dimension;
import java.awt.SplashScreen;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import useful.GUI.SplashProgress;
import vierGewinnt.client.GUIVierGewinnt;

public class StartClient {

	public static void main(String[] args) {
		new SplashProgress(SplashScreen.getSplashScreen());
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					GUIVierGewinnt wnd = new GUIVierGewinnt("VierGewinnt");
					wnd.setSize(740, 700);
					wnd.setMinimumSize(new Dimension(365, 570));
					wnd.setLocationRelativeTo(null);
					wnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					wnd.setVisible(true);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
