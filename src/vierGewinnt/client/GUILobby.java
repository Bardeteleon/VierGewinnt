package vierGewinnt.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;

import useful.GUI;

public class GUILobby extends JDialog
{
	private JLabel laTitle;
	private JToolBar tobButtons;
	private JButton bnConnect, bnSend, bnPlay;
	protected JTextPane tpOutput;
	private JTextPane tpInput;
	private JSplitPane sppInOut;
	private JTable tbConnections;
	private JScrollPane spOutput, spInput, spConnections;

	private Container c;
	private GridBagLayout gbl;
	protected ConnectionTableModel tableModel;
	protected GUIConnection guiConnection;
	protected GUIGameConfig guiGameConfig;
	private GUIVierGewinnt parent;

	public GUILobby(GUIVierGewinnt parent, String title, boolean model)
	{
		super(parent, title, model);
		this.parent = parent;
		setMinimumSize(new Dimension(250, 210));

		initComponents();
		addActions();
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon(this, "bilder/clientIcon.png").getImage());

		guiConnection = new GUIConnection(parent, "Verbinden", true);
		guiConnection.setResizable(false);
		guiConnection.setSize(270, 190);
		guiConnection.setLocationRelativeTo(null);
		guiConnection.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		guiGameConfig = new GUIGameConfig(parent);
		guiGameConfig.setSize(380, 430);
		guiGameConfig.setLocationRelativeTo(null);
		guiGameConfig.setResizable(false);
		guiGameConfig.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		gbl = new GridBagLayout();
		c = getContentPane();
		c.setLayout(gbl);
		c.setBackground(Color.WHITE);

		laTitle = new JLabel("Lobby", SwingConstants.CENTER);
		laTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		laTitle.setPreferredSize(new Dimension(10, 20));
		laTitle.setOpaque(true);
		laTitle.setBackground(Color.WHITE);

		tobButtons = new JToolBar();
		tobButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
		tobButtons.setRollover(true);
		tobButtons.setFloatable(false);
		tobButtons.setPreferredSize(new Dimension(10, 20));
		tobButtons.setBackground(Color.WHITE);

		bnConnect = new JButton("Verbinden");
		bnConnect.setFocusPainted(false);
		bnConnect.setBackground(Color.WHITE);
		tobButtons.add(bnConnect);

		bnPlay = new JButton("Spielen");
		bnPlay.setFocusPainted(false);
		bnPlay.setBackground(Color.WHITE);
		tobButtons.add(bnPlay);

		tpOutput = new JTextPane();
		tpOutput.setEditable(false);
		tpOutput.setStyledDocument(GUI.getStyledDoc(this));
		spOutput = new JScrollPane(tpOutput);

		tpInput = new JTextPane();
		tpInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
		tpInput.setStyledDocument(GUI.getStyledDoc(this));
		spInput = new JScrollPane(tpInput);

		sppInOut = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spOutput, spInput);
		sppInOut.setPreferredSize(new Dimension(10, 10));

		tableModel = new ConnectionTableModel();
		tbConnections = new JTable(tableModel);
		tbConnections.getColumnModel().getColumn(1).setPreferredWidth(20);
		tbConnections.getTableHeader().setToolTipText("Nickname mit Whisperstatus");
		tbConnections.getTableHeader().setReorderingAllowed(false);
		spConnections = new JScrollPane(tbConnections);
		spConnections.setPreferredSize(new Dimension(10, 10));

		bnSend = new JButton("Senden");
		bnSend.setFocusPainted(false);
		bnSend.setBackground(Color.WHITE);

		GUI.addComponent(c, tobButtons, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0, 2, 1, 1, 0.05);
		GUI.addComponent(c, laTitle, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 1, 2, 1, 1, 0.05);
		GUI.addComponent(c, sppInOut, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5), 0, 2, 1, 2, 0.85, 0.9);
		GUI.addComponent(c, spConnections, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5), 1, 2, 1, 1, 0.15, 0.8);
		GUI.addComponent(c, bnSend, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(25, 15, 25, 15), 1, 3, 1, 1, 0.15, 0.1);
	}

	private void addActions()
	{
		c.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				sppInOut.setDividerLocation(spConnections.getHeight());
			}
		});

		bnConnect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				guiConnection.setVisible(true);
			}
		});

		bnSend.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String message = tpInput.getText();
				if (message.matches("\\s*") || message.equals(""))
					return;
				if (parent.client != null && parent.client.myConnection != null && parent.client.myConnection.getConnected())
				{
					if (tableModel.getWhisperNames().size() == 0)
					{
						parent.client.sendChatMessage(message);
						tpInput.setText("");
					} else
					{
						parent.client.sendWhisperMessage(message, tableModel.getWhisperNames());
						tpInput.setText("");
					}
				}
			}
		});

		bnPlay.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (parent.client != null && tableModel.getNames(parent.client.nick).size() > 0)
				{
					if (parent.client.teammate != null)
					{
						JOptionPane.showMessageDialog(GUILobby.this, "Sie sind bereits in einem Spiel!", "", JOptionPane.INFORMATION_MESSAGE);
					} else
					{
						guiGameConfig.setNames(tableModel.getNames(parent.client.nick));
						guiGameConfig.setVisible(true);
					}
				} else
					JOptionPane.showMessageDialog(GUILobby.this, "Kein Spieler online.", "", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		tpInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "bnSenden");
		tpInput.getActionMap().put("bnSenden", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				bnSend.doClick();
			}
		});
		tpInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "taEingabeZeilenumbruch");
		tpInput.getActionMap().put("taEingabeZeilenumbruch", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					tpInput.getStyledDocument().insertString(tpInput.getCaretPosition(), "\n", tpInput.getStyle(GUI.BLACKSTYLE));
				} catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
