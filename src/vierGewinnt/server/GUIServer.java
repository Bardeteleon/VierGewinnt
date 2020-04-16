package vierGewinnt.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import useful.GUI;

public class GUIServer extends JFrame
{
	private JButton bnServer;
	private JTextField tfPort;
	private Container c;
	private ImageIcon icStart, icStop;
	private JPanel pnBufferWest, pnBufferEast, pnBufferNorth, pnBufferSouth;

	private ServerVierGewinnt myServer;

	public GUIServer()
	{
		super("Vier Gewinnt");
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(200, 240));
		setIconImage(GUI.createImageIcon(this, "serverIcon.png").getImage());

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie den Server wirklich schlieﬂen?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == 0)
				{
					if (!bnServer.getActionCommand().equals("Starten"))
						myServer.close();
					System.exit(0);
				}
			}
		});
		c = getContentPane();

		URL urlStart = this.getClass().getResource("Start.png");
		if (urlStart != null)
			icStart = new ImageIcon(new ImageIcon(urlStart).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
		URL urlStop = this.getClass().getResource("Stop.png");
		if (urlStop != null)
			icStop = new ImageIcon(new ImageIcon(urlStop).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));

		bnServer = new JButton();
		bnServer.setActionCommand("Starten");
		bnServer.setFocusPainted(false);
		bnServer.setIcon(icStart);
		bnServer.addActionListener(new ServerListener());

		tfPort = new JTextField("Port");
		tfPort.setColumns(10);

		pnBufferEast = new JPanel();
		pnBufferNorth = new JPanel();
		pnBufferSouth = new JPanel();
		pnBufferWest = new JPanel();

		pnBufferSouth.add(tfPort);

		c.add(BorderLayout.CENTER, bnServer);
		c.add(BorderLayout.SOUTH, pnBufferSouth);
		c.add(BorderLayout.NORTH, pnBufferNorth);
		c.add(BorderLayout.WEST, pnBufferWest);
		c.add(BorderLayout.EAST, pnBufferEast);

	}

	private class ServerListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String cmd = e.getActionCommand();
			if (cmd.equals("Starten"))
			{
				// Server starten
				tfPort.setEnabled(false);
				bnServer.setActionCommand("Beenden");
				bnServer.setIcon(icStop);

				try
				{
					myServer = new ServerVierGewinnt(Integer.parseInt(tfPort.getText()));
				} catch (NumberFormatException ex)
				{
					tfPort.setText("1000");
					myServer = new ServerVierGewinnt(1000);
				}
			}
			if (cmd.equals("Beenden"))
			{
				try
				{
					int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie den Server wirklich ausschalten?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (answer == 0)
					{
						myServer.close();
						tfPort.setEnabled(true);
						bnServer.setActionCommand("Starten");
						bnServer.setIcon(icStart);
					}
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}

			}
		}
	}
}
