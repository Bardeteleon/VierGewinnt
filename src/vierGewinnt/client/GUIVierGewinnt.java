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
import java.awt.SplashScreen;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

import useful.GUI;
import useful.GUI.SplashProgress;

/*
 * Vereinheitlichung x-y-koordinaten und Row-Column-System
 * Dokumentation
 * Fehler Methode für Log.txt
 */
public class GUIVierGewinnt extends JFrame
{
	public static final int RED = 1;
	public static final int YEL = 2;

	private JLabel laTitle;
	private JToolBar tobButtons;
	private JButton bnLobby, bnFullScreen, bnHelp, bnPaneVisibility;
	protected JTable tbPlayingField;
	private JPanel panBuffPlayingField;
	private JTextPane tpInput, tpLog, tpChat;
	private JScrollPane spLog, spChat, spInput;
	private JSplitPane sppLogChat, sppInOut;
	private JDialog diaHelp;

	private Container c;
	private GridBagLayout gbl;
	protected GameTableModel playingFieldModel;
	private GameRenderer playingFieldRenderer;
	protected GUILobby guiLobby;
	protected ClientVierGewinnt client;
	private GlassPaneAnimation glassAni;
	private Explosion expAni;
	private OnePicAnimation winnerAni, gameOverAni;

	public GUIVierGewinnt(String title)
	{
		super(title);
		initComponents();
		addActions();
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon(this, "bilder/clientIcon.png").getImage());

		guiLobby = new GUILobby(this, "Lobby", false);
		guiLobby.setMinimumSize(new Dimension(230, 230));
		guiLobby.setSize(630, 400);
		guiLobby.setLocationRelativeTo(null);
		guiLobby.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		diaHelp = GUI.getHelpDialog(this, "Help.txt");
		diaHelp.pack();
		diaHelp.setSize(700, 600);
		diaHelp.setLocationRelativeTo(this);

		c = getContentPane();
		gbl = new GridBagLayout();
		c.setLayout(gbl);
		c.setBackground(Color.WHITE);

		glassAni = new GlassPaneAnimation();
		setGlassPane(glassAni);
		getGlassPane().setVisible(true);
		expAni = new Explosion(Sprite.loadPics(this, "vierGewinnt/client/bilder/ExplosionOne.png", 17), 100);
		winnerAni = new OnePicAnimation(Sprite.loadPics(this, "vierGewinnt/client/bilder/gewonnen.jpg", 1), glassAni, 20, 100);
		winnerAni.setCentral(true);
		winnerAni.setWaitingSteps(5);
		gameOverAni = new OnePicAnimation(Sprite.loadPics(this, "vierGewinnt/client/bilder/game-over.jpg", 1), glassAni, 20, 100);
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
		playingFieldModel = new GameTableModel(7, 6, 0, 0);
		tbPlayingField.setModel(playingFieldModel);
		panBuffPlayingField = new JPanel();
		panBuffPlayingField.setPreferredSize(new Dimension(10, 10));
		panBuffPlayingField.add(tbPlayingField);
		panBuffPlayingField.setBackground(Color.WHITE);
		playingFieldRenderer = new GameRenderer(panBuffPlayingField);
		tbPlayingField.setDefaultRenderer(Icon.class, playingFieldRenderer);

		tpLog = new JTextPane()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .13f);
				g2.setComposite(comp);
				g2.setFont(new Font("SansSerif", Font.BOLD, 30));
				Point viewPos = spLog.getViewport().getViewPosition();
				g2.drawString("LOG", spLog.getWidth() / 2 - g2.getFontMetrics().stringWidth("LOG") / 2, viewPos.y + (spLog.getHeight() / 2 + g2.getFontMetrics().getHeight() / 2));
			}
		};
		tpLog.setEditable(false);
		tpLog.setStyledDocument(GUI.getStyledDoc(this));
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

		spLog = new JScrollPane(tpLog);
		spChat = new JScrollPane(tpChat);
		spInput = new JScrollPane(tpInput);

		sppInOut = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spChat, spInput);
		sppInOut.setOneTouchExpandable(true);
		sppLogChat = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLog, sppInOut);
		sppLogChat.setOneTouchExpandable(true);
		sppLogChat.setPreferredSize(new Dimension(10, 10));

		bnPaneVisibility = new JButton();
		bnPaneVisibility.setPreferredSize(new Dimension(10, 10));
		bnPaneVisibility.setFocusPainted(false);
		bnPaneVisibility.setBackground(Color.WHITE);

		GUI.addComponent(c, tobButtons, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0, 1, 1, 1, 0);
		GUI.addComponent(c, panBuffPlayingField, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 20, 20, 20), 0, 1, 1, 1, 1, 0.70);
		GUI.addComponent(c, bnPaneVisibility, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 2, 1, 1, 1, 0.001);
		GUI.addComponent(c, sppLogChat, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 3, 1, 1, 1, 0.299);
		pack();
	}

	private void addActions()
	{
		c.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent arg0)
			{
				sppInOut.setDividerLocation(0.8);
				sppLogChat.setDividerLocation(0.5);
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
				if (sppLogChat.isVisible())
					sppLogChat.setVisible(false);
				else
					sppLogChat.setVisible(true);
				c.validate();
				c.dispatchEvent(new ComponentEvent(c, ComponentEvent.COMPONENT_RESIZED));
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
					client.playRequest(playingFieldModel.getChoosingChipPos(), playingFieldModel.isBombChoosingChip());
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
					if (playingFieldModel.isBombChoosingChip())
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
						Vector<String> mitsp = new Vector<String>();
						mitsp.add(client.teammate);
						client.sendWhisperMessage(message, mitsp);
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
					if (client != null && client.teammate != null && playingFieldModel.isBombPosition(pField.x, pField.y + 1) && playingFieldModel.getSpielerColorAt(pField.x, pField.y) == client.color)
					{
						client.explosionRequest(pField.x, pField.y);
					}
				}
			}
		});
	}

	private Point getColumnAndRowOfPlayingFieldAt(Point p)
	{
		int widthHeight = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		Point pNew = new Point();
		pNew.x = p.x / widthHeight;
		pNew.y = p.y / widthHeight - 1; // -1 wegen Auswahl Zeile
		return pNew;

	}

	public void insertLogMessage(String m)
	{
		try
		{
			tpLog.getDocument().insertString(tpLog.getDocument().getLength(), m + "\n", tpLog.getStyle(GUI.BLACKSTYLE));
			GUI.insertSmileys(tpLog.getStyledDocument(), 0, m.length());
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	public void insertChatMessage(String m, String type)
	{
		try
		{
			guiLobby.tpOutput.getDocument().insertString(guiLobby.tpOutput.getDocument().getLength(), m + "\n", guiLobby.tpOutput.getStyle(type));
			GUI.insertSmileys(guiLobby.tpOutput.getStyledDocument(), 0, m.length());
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	public void insertGameChatMessage(String m, String type)
	{
		try
		{
			tpChat.getDocument().insertString(tpChat.getDocument().getLength(), m + "\n", tpChat.getStyle(type));
			GUI.insertSmileys(tpChat.getStyledDocument(), 0, m.length());
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}

	public void setChipAt(int spieler, int chipType, int x, int y, boolean animated)
	{
		// Anpassung der y Koordinate wegen unterschiedlichen Systemen
		int maxY = playingFieldModel.getRowCount() - 2;
		y = maxY - y;

		if (animated)
			playingFieldModel.setChipAnimatedAt(spieler, GameTableModel.chipTypeToBomb(chipType), x, y);
		else
			playingFieldModel.setChipAt(spieler, GameTableModel.chipTypeToBomb(chipType), x, y);
	}

	public void doSmallExplosion(int x, int y)
	{
		int column = x;
		int row = playingFieldModel.getRowCount() - 2 - y;

		int widthHeight = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		Point pFrame = tbPlayingField.getLocationOnScreen();
		pFrame.x -= c.getLocationOnScreen().x;
		pFrame.y -= c.getLocationOnScreen().y;
		pFrame.x += column * widthHeight;
		pFrame.y += (row + 1) * widthHeight;

		playingFieldModel.setIconAt(playingFieldModel.LEER, column, row);
		int wh = tbPlayingField.getWidth() / playingFieldModel.getColumnCount();
		expAni.setSize(wh * 2, wh * 2);
		expAni.setPosition(pFrame.x - wh * .5, pFrame.y - wh * .5);
		expAni.reset();
		glassAni.addAnimation(expAni);
		glassAni.start();
		playingFieldModel.delayAnimationHandler(expAni.delay * (expAni.loop_to - expAni.loop_from - 2));
		Vector<Point> points = playingFieldModel.getChipPositionsOver(column, row);
		for (Point p : points)
		{
			playingFieldModel.setChipAnimatedAt(playingFieldModel.getSpielerColorAt(p.x, p.y), playingFieldModel.isBombPosition(p.x, p.y + 1), column, p.y, row);
			row--;
		}
	}

	public void doWinnerAnimation()
	{
		winnerAni.reset();
		glassAni.addAnimation(winnerAni);
		glassAni.start();
	}

	public void doLooserAnimation()
	{
		gameOverAni.reset();
		glassAni.addAnimation(gameOverAni);
		glassAni.start();
	}

	public void setUserTable(Vector<String> connections, String... filters)
	{
		guiLobby.tableModel.setData(connections, filters);
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

	public void setChooseChip(boolean enable, int color)
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
		playingFieldModel.setChooseChipEnable(false, 0);
	}

	public void resetLogChat()
	{
		tpLog.setText("");
		tpChat.setText("");
	}

	public void resetFrame()
	{
		resetPlayingFieldGUI();
		resetLogChat();
		guiLobby.tpOutput.setText("");
	}

	public static void main(String[] args)
	{
		new SplashProgress(SplashScreen.getSplashScreen());
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e)
					{
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
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
