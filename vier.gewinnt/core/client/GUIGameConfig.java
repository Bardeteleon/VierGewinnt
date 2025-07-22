package core.client;

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

public class GUIGameConfig extends JDialog
{
	private GUIVierGewinnt parent;
	
	private JComboBox cbNames, cbRows, cbColumns, cbTurnTime, cbBombs;
	private JLabel laNames, laGeneralSettings, laRows, laColumns, laTurnTime, laBombConfig, laBombCount, laBombPlayCount, laBombChipCount;
	private JPanel panBuffName, panBuffSettings, panBuffBombConfig;
	private JCheckBox chbBombPlayCount, chbBombChipCount;
	private JButton bnGameRequest;

	private Container contentPane;
	private GridBagLayout gbl;
	private Font font;

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
	
	private Vector<Integer> getValues(int from, int to)
	{
		Vector<Integer> values = new Vector<Integer>();
		for (int i = from; i <= to; i++)
		{
			values.add(i);
		}
		return values;
	}

	private void initComponents()
	{
		setIconImage(GUI.createImageIcon("images/client/clientIcon.png").getImage());

		contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		font = new Font("Sans Serif", Font.BOLD, 16);
		
		cbNames = new JComboBox<String>();

		cbRows = new JComboBox<Integer>(getValues(4, 20));
		cbRows.setSelectedIndex(2);

		cbColumns = new JComboBox<Integer>(getValues(4, 20));
		cbColumns.setSelectedIndex(3);
		
		cbTurnTime = new JComboBox<Integer>(new Integer[] {3, 5, 8, 10, 15, 20, 30});
		cbTurnTime.setSelectedIndex(5);
		
		cbBombs = new JComboBox<Integer>(getValues(0, 10));
		cbBombs.setSelectedIndex(0);
		
		laNames = new JLabel("Mit wem moechten Sie spielen?");
		laNames.setFont(font);
		laNames.setHorizontalAlignment(JLabel.CENTER);
		laNames.setPreferredSize(new Dimension(10, 20));	
		laNames.setOpaque(true);
		laNames.setBackground(Color.WHITE);
		
		laGeneralSettings = new JLabel("Allgemein");
		laGeneralSettings.setFont(font);
		laGeneralSettings.setHorizontalAlignment(JLabel.CENTER);
		laGeneralSettings.setPreferredSize(new Dimension(10, 20));
		laGeneralSettings.setOpaque(true);
		laGeneralSettings.setBackground(Color.WHITE);
		
		laRows = new JLabel("Zeilen:");
		laRows.setPreferredSize(new Dimension(10, 10));
		laRows.setOpaque(true);
		laRows.setBackground(Color.WHITE);
		
		laColumns = new JLabel("Spalten:");
		laColumns.setPreferredSize(new Dimension(10, 10));
		laColumns.setOpaque(true);
		laColumns.setBackground(Color.WHITE);
		
		laTurnTime = new JLabel("Max. Zugzeit (in Sek.):");
		laTurnTime.setPreferredSize(new Dimension(10, 10));
		laTurnTime.setOpaque(true);
		laTurnTime.setBackground(Color.WHITE);
		
		laBombConfig = new JLabel("Bombenkonfiguration");
		laBombConfig.setFont(font);
		laBombConfig.setHorizontalAlignment(JLabel.CENTER);
		laBombConfig.setPreferredSize(new Dimension(10, 20));
		laBombConfig.setOpaque(true);
		laBombConfig.setBackground(Color.WHITE);
		
		laBombCount = new JLabel("Anzahl Bomben:");
		laBombCount.setPreferredSize(new Dimension(10,10));
		laBombCount.setOpaque(true);
		laBombCount.setBackground(Color.WHITE);
		
		laBombChipCount = new JLabel("Bombenchip zaehlt zum Sieg:");
		laBombChipCount.setPreferredSize(new Dimension(10,10));
		laBombChipCount.setOpaque(true);
		laBombChipCount.setBackground(Color.WHITE);
				
		laBombPlayCount = new JLabel("Explosion zaehlt als Zug:");
		laBombPlayCount.setPreferredSize(new Dimension(10,10));
		laBombPlayCount.setOpaque(true);
		laBombPlayCount.setBackground(Color.WHITE);
		
		bnGameRequest = new JButton("Anfrage senden");
		bnGameRequest.setFocusPainted(false);
		bnGameRequest.setBackground(Color.WHITE);
		
		chbBombChipCount = new JCheckBox();
//		chbBombChipCount.setPreferredSize(new Dimension(10,10));
		chbBombChipCount.setBackground(Color.WHITE);
		
		chbBombPlayCount = new JCheckBox();
		chbBombPlayCount.setSelected(true);
//		chbBombPlayCount.setPreferredSize(new Dimension(10,10));
		chbBombPlayCount.setBackground(Color.WHITE);
		
		gbl = new GridBagLayout();
		panBuffName = new JPanel(gbl);
		panBuffName.setBorder(BorderFactory.createRaisedBevelBorder());
		panBuffName.setBackground(Color.WHITE);
		GUI.addComponent(panBuffName, laNames, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 0, 1, 1, 1, 1);
		GUI.addComponent(panBuffName, cbNames, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 1, 1, 1, 1, 1);
		
		panBuffSettings = new JPanel(gbl);
		panBuffSettings.setBorder(BorderFactory.createLoweredBevelBorder());
		panBuffSettings.setBackground(Color.WHITE);
		GUI.addComponent(panBuffSettings, laGeneralSettings, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 0, 0, 2, 1, 1,   1);
		GUI.addComponent(panBuffSettings, laRows,			 GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 0, 1, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffSettings, cbRows, 			 GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 1, 1, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffSettings, laColumns,		 GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 0, 2, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffSettings, cbColumns, 		 GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 1, 2, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffSettings, laTurnTime, 		 GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 0, 3, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffSettings, cbTurnTime, 		 GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 	 1, 3, 1, 1, 0.4, 1);

		panBuffBombConfig = new JPanel(gbl);
		panBuffBombConfig.setBorder(BorderFactory.createLoweredBevelBorder());
		panBuffBombConfig.setBackground(Color.WHITE);
		GUI.addComponent(panBuffBombConfig, laBombConfig, 		GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 0, 2, 1, 1, 1);
		GUI.addComponent(panBuffBombConfig, laBombCount, 		GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 1, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffBombConfig, cbBombs, 			GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 1, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffBombConfig, laBombChipCount, 	GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 2, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffBombConfig, chbBombChipCount, 	GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 2, 1, 1, 0.4, 1);
		GUI.addComponent(panBuffBombConfig, laBombPlayCount, 	GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,5,5,5), 0, 3, 1, 1, 0.6, 1);
		GUI.addComponent(panBuffBombConfig, chbBombPlayCount, 	GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(5,5,5,5), 1, 3, 1, 1, 0.4, 1);
		
		contentPane.setLayout(gbl);
		GUI.addComponent(contentPane, panBuffName, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(30,30,5,30), 0, 0, 1, 1, 1, 0.2);
		GUI.addComponent(contentPane, panBuffSettings, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0,30,5,30), 0, 1, 1, 1, 1, 0.35);
		GUI.addComponent(contentPane, panBuffBombConfig, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0,30,5,30), 0, 2, 1, 1, 1, 0.35);
		GUI.addComponent(contentPane, bnGameRequest, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(5,60,10,60), 0, 3, 1, 1, 1, 0.1);

	}

	private void addActions()
	{
		bnGameRequest.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				parent.getClient().gameRequest(
						(String)cbNames.getSelectedItem(), 
						(int)cbRows.getSelectedItem(),
						(int)cbColumns.getSelectedItem(),
						(int)cbTurnTime.getSelectedItem(),
						chbBombChipCount.isSelected(),
						chbBombPlayCount.isSelected(),
						(int)cbBombs.getSelectedItem());
				setVisible(false);
			}
		});
	}
	
	public static void main(String[] args)
	{
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
		f.setSize(380, 490);
		f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		f.pack();
		f.setVisible(true);
	}
}
