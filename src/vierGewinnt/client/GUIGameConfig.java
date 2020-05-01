package vierGewinnt.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import useful.GUI;
import useful.Stopuhr;

public class GUIGameConfig extends JDialog
{
	private GUIVierGewinnt parent;
	
	private JComboBox cbNames, cbRows, cbColumns;
	private JLabel laNames, laDimension, laRows, laColumns, laBombConfig, laBombCount;
	private JPanel panBuffName, panBuffSettings, panBuffBombConfig;
	private JCheckBox chbBombPlayCount, chbBombChipCount;
	private JButton bnGameRequest;
	private JTextField tfBombs;

	private Container contentPane;
	private GridBagLayout gbl;
	private Font font;
	private Vector<Integer> values;

	public GUIGameConfig(GUIVierGewinnt parent)
	{
		super(parent, "Neues Spiel", true);
		this.parent = parent;

		initComponents();
		addActions();
	}

	public void setNames(Vector<String> names)
	{
		cbNames.removeAllItems();
		for(String s : names)
		{
			cbNames.addItem(s);
		}
		cbNames.repaint();
	}
	
	private void fillValues(int from, int to)
	{
		values = new Vector<Integer>();
		for (int i = from; i <= to; i++)
		{
			values.add(i);
		}
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon(this, "bilder/clientIcon.png").getImage());

		contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		font = new Font("Sans Serif", Font.BOLD, 16);
		
		cbNames = new JComboBox();

		fillValues(4, 20);
		cbRows = new JComboBox(values);
		cbRows.setSelectedIndex(2);

		cbColumns = new JComboBox(values);
		cbColumns.setSelectedIndex(3);
		
		laNames = new JLabel("Mit wem möchten Sie spielen?");
		laNames.setFont(font);
		laNames.setHorizontalAlignment(JLabel.CENTER);
		laNames.setPreferredSize(new Dimension(100, 100));	
		laNames.setOpaque(true);
		laNames.setBackground(Color.WHITE);
		
		laDimension = new JLabel("Wie groß soll ihr Spielfeld sein?");
		laDimension.setFont(font);
		laDimension.setHorizontalAlignment(JLabel.CENTER);
		laDimension.setPreferredSize(new Dimension(10, 10));
		laDimension.setOpaque(true);
		laDimension.setBackground(Color.WHITE);
		
		laRows = new JLabel("Zeilen:");
		laRows.setPreferredSize(new Dimension(10, 10));
		laRows.setOpaque(true);
		laRows.setBackground(Color.WHITE);
		
		laColumns = new JLabel("Spalten:");
		laColumns.setPreferredSize(new Dimension(10, 10));
		laColumns.setOpaque(true);
		laColumns.setBackground(Color.WHITE);
		
		laBombConfig = new JLabel("Bombenkonfiguration");
		laBombConfig.setFont(font);
		laBombConfig.setHorizontalAlignment(JLabel.CENTER);
		laBombConfig.setPreferredSize(new Dimension(10,10));
		laBombConfig.setOpaque(true);
		laBombConfig.setBackground(Color.WHITE);
		
		laBombCount = new JLabel("Anzahl Bomben:");
		laBombCount.setPreferredSize(new Dimension(10,10));
		laBombCount.setOpaque(true);
		laBombCount.setBackground(Color.WHITE);
		
		bnGameRequest = new JButton("Anfrage senden");
		bnGameRequest.setFocusPainted(false);
		bnGameRequest.setBackground(Color.WHITE);
		
		tfBombs = new JTextField("0");
		tfBombs.setPreferredSize(new Dimension(10,10));
		
		chbBombChipCount = new JCheckBox("Bombenchip zählt zum Sieg");
		chbBombChipCount.setPreferredSize(new Dimension(10,10));
		chbBombChipCount.setBackground(Color.WHITE);
		
		chbBombPlayCount = new JCheckBox("Explosion zählt als Zug");
		chbBombPlayCount.setSelected(true);
		chbBombPlayCount.setPreferredSize(new Dimension(10,10));
		chbBombPlayCount.setBackground(Color.WHITE);
		
		gbl = new GridBagLayout();
		panBuffName = new JPanel(gbl);
		panBuffName.setBorder(BorderFactory.createRaisedBevelBorder());
		panBuffName.setBackground(Color.WHITE);
		GUI.addComponent(panBuffName, laNames, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 0, 1, 1, 1, 1.2);
		GUI.addComponent(panBuffName, cbNames, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 1, 1, 1, 1, 1);
		
		panBuffSettings = new JPanel(gbl);
		panBuffSettings.setBorder(BorderFactory.createLoweredBevelBorder());
		panBuffSettings.setBackground(Color.WHITE);
		GUI.addComponent(panBuffSettings, laDimension, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5), 0, 0, 2, 1, 1, 4);
		GUI.addComponent(panBuffSettings, laRows, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 1, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffSettings, cbRows, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 1, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffSettings, laColumns, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 2, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffSettings, cbColumns, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 2, 1, 1, 0.6, 1);

		panBuffBombConfig = new JPanel(gbl);
		panBuffBombConfig.setBorder(BorderFactory.createLoweredBevelBorder());
		panBuffBombConfig.setBackground(Color.WHITE);
		GUI.addComponent(panBuffBombConfig, laBombConfig, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 0, 2, 1, 1, 1);
		GUI.addComponent(panBuffBombConfig, laBombCount, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 1, 1, 1, 1, 1);
		GUI.addComponent(panBuffBombConfig, tfBombs, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 1, 1, 1, 1, 1);
		GUI.addComponent(panBuffBombConfig, chbBombChipCount, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 2, 2, 1, 1, 1);
		GUI.addComponent(panBuffBombConfig, chbBombPlayCount, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 3, 2, 1, 1, 1);
		
		contentPane.setLayout(gbl);
		GUI.addComponent(contentPane, panBuffName, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(30,30,5,30), 0, 0, 1, 1, 1, 0.2);
		GUI.addComponent(contentPane, panBuffSettings, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0,30,5,30), 0, 1, 1, 1, 1, 0.35);
		GUI.addComponent(contentPane, panBuffBombConfig, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0,30,5,30), 0, 2, 1, 1, 1, 0.35);
		GUI.addComponent(contentPane, bnGameRequest, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(0,0,30,0), 0, 3, 1, 1, 1, 0.1);

	}

	private void addActions()
	{
		bnGameRequest.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int bombs;
				try{
					bombs = Integer.parseInt(tfBombs.getText());
				}catch(NumberFormatException ex)
				{
					tfBombs.setText("Format Error!");
					return;
				}
				parent.getClient().gameRequest((String)cbNames.getSelectedItem(), Integer.parseInt(cbRows.getSelectedItem().toString()),Integer.parseInt(cbColumns.getSelectedItem().toString()),chbBombChipCount.isSelected(),chbBombPlayCount.isSelected(),bombs);
				setVisible(false);
			}
		});
	}
	
	public static void main(String[] args)
	{
		Stopuhr s = new Stopuhr();
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		GUIGameConfig f = new GUIGameConfig(null);
		Vector<String> name = new Vector<String>();
		name.add("Hans");
		f.setNames(name);
		f.setSize(380, 440);
		f.pack();
		System.out.println("packed "+s.getTime());
		f.setVisible(true);
	}
}
