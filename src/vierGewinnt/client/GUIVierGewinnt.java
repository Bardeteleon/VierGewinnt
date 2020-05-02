package vierGewinnt.client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;

import useful.GUI;
import vierGewinnt.client.animation.Explosion;
import vierGewinnt.client.animation.GlassPaneAnimation;
import vierGewinnt.client.animation.OnePicAnimation;
import vierGewinnt.client.animation.Sprite;
import vierGewinnt.common.Chip;
import vierGewinnt.common.Player;

/*
 * Vereinheitlichung x-y-koordinaten und Row-Column-System
 * Dokumentation
 * Fehler Methode für Log.txt
 */
public class GUIVierGewinnt extends JFrame
{
	private ClientVierGewinnt client;
	
	private GUILobby guiLobby;
	private GameStatusBar statusBar;
	
	private JToolBar tobButtons;
	private JButton bnLobby, bnFullScreen, bnHelp, bnPaneVisibility;
	private JPanel panBuffPlayingField;
	private JTextPane tpInput, tpChat;
	private JScrollPane spChat, spInput;
	private JSplitPane sppInOut;
	private JDialog diaHelp;

	private JTable tbPlayingField;
	private GameTableModel playingFieldModel;
	private GameCellRenderer playingFieldRenderer;
	
	private GlassPaneAnimation glassAni;
	private Explosion expAni;
	private OnePicAnimation winnerAni, gameOverAni;
	
	private Container contenPane;

	public GUIVierGewinnt(String title)
	{
		super(title);
		initComponents();
		addActions();
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon(this, "bilder/clientIcon.png").getImage());

		guiLobby = new GUILobby(this, "Lobby");
		guiLobby.setMinimumSize(new Dimension(230, 230));
		guiLobby.setSize(630, 400);
		guiLobby.setLocationRelativeTo(null);
		guiLobby.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		diaHelp = GUI.getHelpDialog(this, "Help.txt");
		diaHelp.pack();
		diaHelp.setSize(700, 600);
		diaHelp.setLocationRelativeTo(this);

		contenPane = getContentPane();
		contenPane.setLayout(new GridBagLayout());
		contenPane.setBackground(Color.WHITE);

		glassAni = new GlassPaneAnimation();
		setGlassPane(glassAni);
		getGlassPane().setVisible(true);
		expAni = new Explosion(glassAni, Sprite.loadPics(this, "vierGewinnt/client/bilder/ExplosionOne.png", 17), 100);
		winnerAni = new OnePicAnimation(glassAni, Sprite.loadPics(this, "vierGewinnt/client/bilder/gewonnen.jpg", 1), 100, 20);
		winnerAni.setCentral(true);
		winnerAni.setWaitingSteps(5);
		gameOverAni = new OnePicAnimation(glassAni, Sprite.loadPics(this, "vierGewinnt/client/bilder/game-over.jpg", 1), 100, 20);
		gameOverAni.setCentral(true);
		gameOverAni.setWaitingSteps(5);

		tobButtons = new JToolBar();
		tobButtons.setFloatable(false);
		tobButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
		tobButtons.setPreferredSize(new Dimension(10, 30));
		tobButtons.setBackground(Color.WHITE);
		bnLobby = new JButton("Lobby");
		bnLobby.setFocusPainted(false);
		bnLobby.setBackground(Color.WHITE);
		tobButtons.add(bnLobby);
		bnFullScreen = new JButton("Vollbild");
		bnFullScreen.setFocusPainted(false);
		bnFullScreen.setBackground(Color.WHITE);
		tobButtons.add(bnFullScreen);
		bnHelp = new JButton("Hilfe");
		bnHelp.setFocusPainted(false);
		bnHelp.setBackground(Color.WHITE);
		tobButtons.add(bnHelp);

		tbPlayingField = new JTable();
		tbPlayingField.setPreferredSize(new Dimension(448, 448));
		tbPlayingField.setRowHeight(64);
		tbPlayingField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbPlayingField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		playingFieldModel = new GameTableModel(7, 6, 0, Player.NONE);
		tbPlayingField.setModel(playingFieldModel);
		panBuffPlayingField = new JPanel();
		panBuffPlayingField.setPreferredSize(new Dimension(10, 10));
		panBuffPlayingField.add(tbPlayingField);
		panBuffPlayingField.setBackground(Color.WHITE);
		playingFieldRenderer = new GameCellRenderer(panBuffPlayingField);
		tbPlayingField.setDefaultRenderer(Icon.class, playingFieldRenderer);

		tpChat = new JTextPane()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .13f);
				g2.setComposite(comp);
				g2.setFont(new Font("SansSerif", Font.BOLD, 30));
				Point viewPos = spChat.getViewport().getViewPosition();
				g2.drawString("Chat", spChat.getWidth() / 2 - g2.getFontMetrics().stringWidth("CHAT") / 2, viewPos.y + (spChat.getHeight() / 2 + g2.getFontMetrics().getHeight() / 2));
			}
		};
		tpChat.setEditable(false);
		tpChat.setStyledDocument(GUI.getStyledDoc(this));
		tpInput = new JTextPane()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .13f);
				g2.setComposite(comp);
				g2.setFont(new Font("SansSerif", Font.BOLD, 20));
				Point viewPos = spInput.getViewport().getViewPosition();
				g2.drawString("Chat Eingabe", spInput.getWidth() / 2 - g2.getFontMetrics().stringWidth("Chat Eingabe") / 2, viewPos.y + (spInput.getHeight() / 2 + g2.getFontMetrics().getHeight() / 2 - 5));
			}
		};
		tpInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
		tpInput.setStyledDocument(GUI.getStyledDoc(this));

		spChat = new JScrollPane(tpChat);
		spInput = new JScrollPane(tpInput);

		sppInOut = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spChat, spInput);
		sppInOut.setOneTouchExpandable(true);
		sppInOut.setDividerLocation(0.8);
		sppInOut.setPreferredSize(new Dimension(10, 10)); // needed so that the chat size is set by the layout. Unless it gets too big.

		bnPaneVisibility = new JButton();
		bnPaneVisibility.setPreferredSize(new Dimension(10, 10));
		bnPaneVisibility.setFocusPainted(false);
		bnPaneVisibility.setBackground(Color.WHITE);
		
		statusBar = new GameStatusBar();
		statusBar.clear();

		GUI.addComponent(contenPane, tobButtons, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0, 1, 1, 1, 0);
		GUI.addComponent(contenPane, panBuffPlayingField, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 20, 20, 20), 0, 1, 1, 1, 1, 0.70);
		GUI.addComponent(contenPane, bnPaneVisibility, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 3, 1, 1, 1, 0.001);
		GUI.addComponent(contenPane, sppInOut, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 4, 1, 1, 1, 0.299);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 10;
		contenPane.add(statusBar, gbc);
		
		pack();
	}

	private void addActions()
	{
		contenPane.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				sppInOut.setDividerLocation(0.8);
				getContentPane().validate();
			}
		});

		panBuffPlayingField.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				playingFieldRenderer.setAktSize();
			}
		});

		bnLobby.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				guiLobby.setVisible(true);
			}
		});

		bnFullScreen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("Vollbild"))
				{
					GUI.changeFullscreenOrWindow(GUIVierGewinnt.this);
					bnFullScreen.setText("Fenster");
				} else if (e.getActionCommand().equals("Fenster"))
				{
					GUI.changeFullscreenOrWindow(GUIVierGewinnt.this);
					bnFullScreen.setText("Vollbild");
				}
			}
		});

		bnHelp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (diaHelp.isVisible())
					diaHelp.setVisible(false);
				else
					diaHelp.setVisible(true);
			}
		});

		bnPaneVisibility.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (sppInOut.isVisible())
					sppInOut.setVisible(false);
				else
					sppInOut.setVisible(true);
				contenPane.validate();
				contenPane.dispatchEvent(new ComponentEvent(contenPane, ComponentEvent.COMPONENT_RESIZED));
			}
		});

		tbPlayingField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		tbPlayingField.getActionMap().put("right", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				playingFieldModel.setChooseChipRight();
			}
		});

		tbPlayingField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		tbPlayingField.getActionMap().put("left", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				playingFieldModel.setChooseChipLeft();
			}
		});

		tbPlayingField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		tbPlayingField.getActionMap().put("down", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (playingFieldModel.isChooseChipEnable() && client != null && client.myConnection != null && client.myConnection.getConnected())
				{
					client.playRequest(playingFieldModel.getChoosingChipPos(), playingFieldModel.getChoosingChipType());
				}
			}
		});

		tbPlayingField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		tbPlayingField.getActionMap().put("up", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (playingFieldModel.isChooseChipEnable())
					if (playingFieldModel.getChoosingChipType() == Chip.EXPLOSIVE)
						playingFieldModel.setChooserChipToBomb(false);
					else
						playingFieldModel.setChooserChipToBomb(true);
			}
		});

		tpInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "bnSenden");
		tpInput.getActionMap().put("bnSenden", new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (client != null && client.teammate != null)
				{
					String message = tpInput.getText();
					if (message.matches("\\s*") || message.equals("") || message.substring(0, 1).equals("#"))
						return;
					if (client.myConnection != null && client.myConnection.getConnected())
					{
						client.sendWhisperMessage(message, client.nick, Arrays.asList(client.teammate));
						tpInput.setText("");
					}

				}
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

		tbPlayingField.addFocusListener(new FocusListener()
		{

			@Override
			public void focusLost(FocusEvent arg0)
			{
				tbPlayingField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			}

			@Override
			public void focusGained(FocusEvent arg0)
			{
				tbPlayingField.setBorder(BorderFactory.createLineBorder(Color.RED));
			}
		});

		tbPlayingField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					Point pField = getColumnAndRowOfPlayingFieldAt(e.getPoint());
					if (   client != null 
						&& client.teammate != null 
						&& Chip.EXPLOSIVE == playingFieldModel.getChipTypeAt(pField.x, pField.y + 1) 
						&& playingFieldModel.getPlayerAt(pField.x, pField.y) == client.myPlayer)
					{
						client.explosionRequest(pField.x, pField.y);
					}
				}
			}
		});
	}

	private Point getColumnAndRowOfPlayingFieldAt(Point pixelPos)
	{
		int widthHeight = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		Point fieldPos = new Point();
		fieldPos.x = pixelPos.x / widthHeight;
		fieldPos.y = pixelPos.y / widthHeight - 1; // -1 wegen Auswahl Zeile
		return fieldPos;

	}

	public void appendGameChat(String text, String type)
	{
		try
		{
			int docLength = tpChat.getDocument().getLength();
			tpChat.getDocument().insertString(docLength, text, tpChat.getStyle(type));
			GUI.insertSmileys(tpChat.getStyledDocument(), docLength);
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	public void setChipAt(Player spieler, Chip chipType, int x, int y)
	{
		// Anpassung der y Koordinate wegen unterschiedlichen Systemen
		int maxY = playingFieldModel.getRowCount() - 2;
		y = maxY - y;

		boolean animated = true;
		
		if (animated)
			playingFieldModel.setChipAnimatedAt(spieler, chipType, x, y);
		else
			playingFieldModel.setChipAt(spieler, chipType, x, y);
	}

	public void doSmallExplosion(int x, int y)
	{
		int column = x;
		int row = playingFieldModel.getRowCount() - 2 - y;

		int widthHeight = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		Point pFrame = tbPlayingField.getLocationOnScreen();
		pFrame.x -= contenPane.getLocationOnScreen().x;
		pFrame.y -= contenPane.getLocationOnScreen().y;
		pFrame.x += column * widthHeight;
		pFrame.y += (row + 1) * widthHeight;

		playingFieldModel.setIconAt(GameTableModel.LEER, column, row);
		int wh = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		expAni.setSize(wh * 2, wh * 2);
		expAni.setPosition(pFrame.x - wh * .5, pFrame.y - wh * .5);
		expAni.reset();
		glassAni.addAnimation(expAni);
		glassAni.resume();
		playingFieldModel.delayAnimationHandler(expAni.getDelay() * (expAni.getLoopTo() - expAni.getLoopFrom() - 2));
		Vector<Point> points = playingFieldModel.getChipPositionsOver(column, row);
		for (Point p : points)
		{
			playingFieldModel.setChipAnimatedAt(playingFieldModel.getPlayerAt(p.x, p.y), playingFieldModel.getChipTypeAt(p.x, p.y + 1), column, p.y, row);
			row--;
		}
	}

	public void doWinnerAnimation()
	{
		winnerAni.reset();
		winnerAni.setInitialWaitingDelayMS(1500);
		glassAni.addAnimation(winnerAni);
		glassAni.resume();
	}

	public void doLooserAnimation()
	{
		gameOverAni.reset();
		gameOverAni.setInitialWaitingDelayMS(1500);
		glassAni.addAnimation(gameOverAni);
		glassAni.resume();
	}

	public boolean askForGame(String vonSpieler, String rows, String columns, String _expChipsZahlenFuerSieg, String _explosionZahltAlsZug, String bombs)
	{
		String expMod1 = "", expMod2 = "";
		if (Integer.parseInt(bombs) > 0)
		{
			if (Boolean.parseBoolean(_expChipsZahlenFuerSieg))
				expMod1 = "\n-> Bombenchip zählt zum Sieg";
			else
				expMod1 = "\n-> Bombenchip zählt NICHT zum Sieg";
			if (Boolean.parseBoolean(_explosionZahltAlsZug))
				expMod2 = "\n-> Bombenexplosion zählt als Zug";
			else
				expMod2 = "\n-> Bombenexplosion zählt NICHT als Zug";
		}
		int answer = JOptionPane.showConfirmDialog(this, "Wollen Sie mit " + vonSpieler + " VierGewinnt spielen?\nSpielfeldgröße: " + rows + " Zeilen / " + columns + " Spalten\nAnzahl Bomben: " + bombs + expMod1 + expMod2, "Einladung", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (answer == 1 || answer == -1)
			return false;
		else
			return true;
	}

	public void setChooseChip(boolean enable, Player color)
	{
		playingFieldModel.setChooseChipEnable(enable, color);
	}

	public void showErrorMessage(String error)
	{
		JOptionPane.showMessageDialog(this, error, "Error occured", JOptionPane.ERROR_MESSAGE);
	}

	public void resetPlayingFieldGUI()
	{
		playingFieldModel.clearPlayingField();
		playingFieldModel.stopAnimationHandler();
		playingFieldModel.setChooseChipEnable(false, Player.NONE);
	}

	public void resetChat()
	{
		tpChat.setText("");
	}

	public void resetFrame()
	{
		resetPlayingFieldGUI();
		resetChat();
		getGUILobby().clearOutputPane();
		statusBar.clear();
	}
	
	public void resetAfterGame()
	{
		client.teammate = null;
		statusBar.clearAfterGame();
		getGUILobby().resetAfterGame();
		playingFieldModel.setChooseChipEnable(false, null);
	}

	public JTable getPlayingField() {
		return tbPlayingField;
	}

	public GameTableModel getPlayingFieldModel() {
		return playingFieldModel;
	}

	public void setPlayingFieldModel(GameTableModel playingFieldModel) {
		this.playingFieldModel = playingFieldModel;
		this.tbPlayingField.setModel(playingFieldModel);
	}

	public GameStatusBar getStatusBar() {
		return statusBar;
	}
	
	public GUILobby getGUILobby()
	{
		return guiLobby;
	}
	
	public ClientVierGewinnt getClient()
	{
		return client;
	}
	
	public void setClient(ClientVierGewinnt client)
	{
		this.client = client;
	}
}
