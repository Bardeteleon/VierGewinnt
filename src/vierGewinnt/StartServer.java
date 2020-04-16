package vierGewinnt;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import vierGewinnt.server.GUIServer;

public class StartServer {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}

				GUIServer f = new GUIServer();
				f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				f.setVisible(true);
			}
		});

	}

}
