package core.client;

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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.BadLocationException;

import useful.GUI;
import core.common.Chat;
import core.common.MessageGenerator;

public class GUILobby extends JDialog
{
	private GUIVierGewinnt parent;

	private GUIConnection guiConnection;
	private GUIGameConfig guiGameConfig;
	
	private JLabel laTitle;
	private JToolBar tobButtons;
	private JButton bnConnect, bnSend, bnPlay;
	private JTextPane tpOutput;
	private JTextPane tpInput;
	private JSplitPane sppInOut;
	private JScrollPane spOutput, spInput, spConnections;
	
	private JTable tbConnections;
	private ConnectionTableModel tableModel;

	private Container contentPane;
	private GridBagLayout gbl;

	public GUILobby(GUIVierGewinnt parent, String title)
	{
		super(parent, title, false);
		this.parent = parent;
		setMinimumSize(new Dimension(250, 210));

		initComponents();
		addActions();
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon("images/client/clientIcon.png").getImage());

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
		contentPane = getContentPane();
		contentPane.setLayout(gbl);
		contentPane.setBackground(Color.WHITE);

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
		tpOutput.setStyledDocument(GUI.getStyledDoc());
		spOutput = new JScrollPane(tpOutput);

		tpInput = new JTextPane();
		tpInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
		tpInput.setStyledDocument(GUI.getStyledDoc());
		spInput = new JScrollPane(tpInput);

		sppInOut = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spOutput, spInput);
		sppInOut.setPreferredSize(new Dimension(10, 10));

		tableModel = new ConnectionTableModel();
		tbConnections = new JTable(tableModel);
		tbConnections.getColumnModel().getColumn(1).setPreferredWidth(20);
		tbConnections.getTableHeader().setToolTipText("Nickname mit Whisperstatus");
		tbConnections.getTableHeader().setReorderingAllowed(false);
		tbConnections.setCellSelectionEnabled(false);
		tbConnections.setFocusable(false);
		spConnections = new JScrollPane(tbConnections);
		spConnections.setPreferredSize(new Dimension(10, 10));

		bnSend = new JButton("Senden");
		bnSend.setFocusPainted(false);
		bnSend.setBackground(Color.WHITE);

		GUI.addComponent(contentPane, tobButtons, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0, 2, 1, 1, 0.05);
		GUI.addComponent(contentPane, laTitle, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 1, 2, 1, 1, 0.05);
		GUI.addComponent(contentPane, sppInOut, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5), 0, 2, 1, 2, 0.85, 0.9);
		GUI.addComponent(contentPane, spConnections, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5), 1, 2, 1, 1, 0.15, 0.8);
		GUI.addComponent(contentPane, bnSend, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(25, 15, 25, 15), 1, 3, 1, 1, 0.15, 0.1);
	}

	private void addActions()
	{
		contentPane.addComponentListener(new ComponentAdapter()
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
				if (parent.getClient() != null && parent.getClient().myConnection != null && parent.getClient().myConnection.getConnected())
				{
					if (tableModel.getWhisperNames().size() == 0)
					{
						parent.getClient().sendChatMessage(message, parent.getClient().nick);
						tpInput.setText("");
					} else
					{
						parent.getClient().sendWhisperMessage(message, parent.getClient().nick, tableModel.getWhisperNames());
						tpInput.setText("");
					}
				}
			}
		});

		bnPlay.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(event.getActionCommand().contentEquals("Spielen"))
				{	
					if (parent.getClient() != null && tableModel.getNames().size() > 0)
					{
						if (parent.getClient().teammate != null)
						{
							JOptionPane.showMessageDialog(GUILobby.this, "Sie sind bereits in einem Spiel!", "", JOptionPane.INFORMATION_MESSAGE);
						} else
						{
							guiGameConfig.setNames(tableModel.getNames());
							guiGameConfig.setVisible(true);
						}
					} else
						JOptionPane.showMessageDialog(GUILobby.this, "Kein Spieler online.", "", JOptionPane.INFORMATION_MESSAGE);
				}else if(event.getActionCommand().contentEquals("Spiel verlassen"))
				{
					int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie das Spiel wirklich verlassen?\nSie k�nnen sich nicht wieder verbinden.", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (answer == 0)
					{
						parent.getClient().sendMessage(MessageGenerator.sendGameEnd(MessageGenerator.GAMEEND_QUITTING));
						parent.getClient().turnTimer.stop();
						parent.resetAfterGame();
					}
				}
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
		
		tbConnections.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if(parent != null)
					if(tableModel.getWhisperNames().size() > 0)
					{
						clearOutputPane();
						Vector<String> participants = tableModel.getWhisperNames();
						participants.add(parent.getClient().nick);
	
						if(!parent.getClient().myChatHandler.isChat(participants))
							parent.getClient().myChatHandler.addNewChat(participants);
						
						Chat chat = parent.getClient().myChatHandler.getChat(participants);
						parent.getClient().myChatHandler.setChatsInvisible();
						chat.setVisible(true);
						setLobbyChat(chat.getFormattedMessageAll(), GUI.GREENSTYLE);
	
					}else
					{
						clearOutputPane();
						Chat chat = parent.getClient().myChatHandler.getChat(parent.getClient().BROADCAST);
						parent.getClient().myChatHandler.setChatsInvisible();
						chat.setVisible(true);
						setLobbyChat(chat.getFormattedMessageAll(), GUI.BLACKSTYLE);
					}
			}
		});;
	}
	
	public void clearOutputPane()
	{
		try {
			tpOutput.getDocument().remove(0, tpOutput.getDocument().getLength());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	public void setLobbyChat(String text, String style)
	{
		try {
			tpOutput.getDocument().insertString(0, text, tpOutput.getStyle(style));
			GUI.insertSmileys(tpOutput.getStyledDocument(), 0);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}
	
	public void appendLobbyChat(String text, String type)
	{
		try
		{
			int docLength = tpOutput.getDocument().getLength();
			tpOutput.getDocument().insertString(docLength, text, tpOutput.getStyle(type));
			GUI.insertSmileys(tpOutput.getStyledDocument(), docLength);
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setUserTable(List<String> connections, String... filters)
	{
		tableModel.setData(connections, filters);
	}
	
	public void setInGame()
	{
		bnPlay.setText("Spiel verlassen");
	}
	
	public void resetAfterGame()
	{
		bnPlay.setText("Spielen");
	}
	
	public GUIConnection getGUIConnection()
	{
		return guiConnection;
	}
	
	public GUIGameConfig getGUIGameConfig()
	{
		return guiGameConfig;
	}
	
	public static void main(String[] args) {
		GUILobby guiLobby = new GUILobby(null, "Lobby");
		guiLobby.setMinimumSize(new Dimension(230, 230));
		guiLobby.setSize(630, 400);
		guiLobby.setLocationRelativeTo(null);
		guiLobby.setUserTable(Arrays.asList("Hans", "Peter", "Mueller"));
		guiLobby.setVisible(true);
	}
}
