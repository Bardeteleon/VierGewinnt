package vierGewinnt.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import useful.GUI;

public class GUIConnection extends JDialog
{
	private JTextField tfIP, tfPort, tfNick;
	private JLabel laIP, laPort, laNick, laTitle;
	private JButton bnVerbinden;
	private JPanel buffR, buffL;

	private Container c;
	private GridBagLayout gbl;
	private GUIVierGewinnt parent;

	public GUIConnection(GUIVierGewinnt parent, String title, boolean model)
	{
		super(parent, title, model);
		this.parent = parent;
		initComponents();
		addActions();
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon(this, "bilder/clientIcon.png").getImage());
		
		c = getContentPane();
		gbl = new GridBagLayout();
		c.setLayout(gbl);
		c.setBackground(Color.WHITE);

		tfIP = new JTextField();
		tfIP.setPreferredSize(new Dimension(10, 25));

		tfPort = new JTextField();
		tfPort.setPreferredSize(new Dimension(10, 25));

		tfNick = new JTextField();
		tfNick.setPreferredSize(new Dimension(10, 25));

		laIP = new JLabel("IP:");
		laIP.setPreferredSize(new Dimension(10, 10));
		laIP.setOpaque(true);
		laIP.setBackground(Color.WHITE);
		
		laPort = new JLabel("Port:");
		laPort.setPreferredSize(new Dimension(10, 10));
		laPort.setOpaque(true);
		laPort.setBackground(Color.WHITE);
		
		laNick = new JLabel("Nickname:");
		laNick.setPreferredSize(new Dimension(10, 10));
		laNick.setOpaque(true);
		laNick.setBackground(Color.WHITE);
		
		bnVerbinden = new JButton("Verbinden");
		bnVerbinden.setFocusPainted(false);
		bnVerbinden.setBackground(Color.WHITE);

		buffR = new JPanel();
		buffR.setPreferredSize(new Dimension(10, 10));
		buffR.setBackground(Color.WHITE);
		
		buffL = new JPanel();
		buffL.setPreferredSize(new Dimension(10, 10));
		buffL.setBackground(Color.WHITE);
		
		GUI.addComponent(c, buffL, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0, 1, 3, 0.05, 1);
		GUI.addComponent(c, buffR, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 3, 0, 1, 3, 0.05, 1);
		GUI.addComponent(c, laNick, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 1, 0, 1, 1, 0.45, 1);
		GUI.addComponent(c, tfNick, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 2, 0, 1, 1, 0.45, 1);
		GUI.addComponent(c, laIP, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 1, 1, 1, 1, 0.45, 1);
		GUI.addComponent(c, tfIP, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 2, 1, 1, 1, 0.45, 1);
		GUI.addComponent(c, laPort, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 1, 2, 1, 1, 0.45, 1);
		GUI.addComponent(c, tfPort, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, new Insets(0, 0, 0, 0), 2, 2, 1, 1, 0.45, 1);
		GUI.addComponent(c, bnVerbinden, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5, 10, 5, 10), 1, 3, 2, 1, 0.9, 1);


	}

	private void addActions()
	{
		bnVerbinden.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent ae)
			{
				String cmd = ae.getActionCommand();
				if (cmd.equals("Verbinden"))
				{
					int port = 1000;
					String ip = "localhost";

					if(!tfIP.getText().isEmpty())
					{
						ip = tfIP.getText(); // TODO UTIL IP regex Check
					}
					
					try
					{
						port = Integer.parseInt(tfPort.getText());
					} catch (NumberFormatException ex)
					{
						tfPort.setText(""+port);
					}
					
					String nick = tfNick.getText();
					if (nick.contains(" ") || nick.matches("\\s*"))
					{
						tfNick.setText("Error (Bitte neu eingeben)");
						return;
					}
					
					parent.client = new ClientVierGewinnt(ip, port, nick, parent);
					
					if (parent.client.myConnection != null && parent.client.myConnection.getConnected())
					{
						tfIP.setEnabled(false);
						tfPort.setEnabled(false);
						tfNick.setEnabled(false);
						bnVerbinden.setText("Trennen");
						setVisible(false);
					}else 
					{
						JOptionPane.showMessageDialog(GUIConnection.this, "Fehler beim Verbinden!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (cmd.equals("Trennen"))
				{
					parent.client.close();

					tfIP.setEnabled(true);
					tfPort.setEnabled(true);
					tfNick.setEnabled(true);
					parent.resetFrame();
					parent.guiLobby.tableModel.setData(null);
					bnVerbinden.setText("Verbinden");
				}
			}
		});
	}
	
	public void doGUIDisconnect()
	{
		parent.client.close();

		tfIP.setEnabled(true);
		tfPort.setEnabled(true);
		tfNick.setEnabled(true);
		parent.guiLobby.tableModel.setData(null);
		bnVerbinden.setText("Verbinden");

	}
}
