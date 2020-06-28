package vierGewinnt.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import useful.GUI;
import vierGewinnt.common.Player;

public class GameStatusBar extends JPanel{

	private JLabel lbWhosPlaying;
	private JLabel lbPlayer1Color;
	private JLabel lbPlayer2Color;
	private JLabel lbWhosTurnIsIt;
	private JLabel lbTurnTimeRemaining;
	private JLabel lbGameTime;
	private JLabel lbBombInfo;
	
	private ImageIcon iconRed;
	private ImageIcon iconYellow;
	
	private JPanel pnLowerBar;
	
	public GameStatusBar()
	{
		lbWhosPlaying = new JLabel();
		lbWhosPlaying.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbWhosTurnIsIt = new JLabel();
		lbWhosTurnIsIt.setHorizontalAlignment(SwingConstants.CENTER);
//		lbWhosTurnIsIt.setOpaque(true);
//		lbWhosTurnIsIt.setBackground(Color.GREEN);
		
		lbTurnTimeRemaining = new JLabel();
		lbTurnTimeRemaining.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbGameTime = new JLabel();
		lbGameTime.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbBombInfo = new JLabel();
		lbBombInfo.setHorizontalAlignment(SwingConstants.CENTER);
//		lbBombInfo.setOpaque(true);
//		lbBombInfo.setBackground(Color.GRAY);
		
		pnLowerBar = new JPanel();
		pnLowerBar.setBackground(Color.WHITE);
		
		lbPlayer1Color = new JLabel();
		lbPlayer1Color.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbPlayer2Color = new JLabel();
		lbPlayer2Color.setHorizontalAlignment(SwingConstants.CENTER);
		
		iconRed = GUI.createImageIcon("images/client/VAR1/o_red.jpg");
		iconYellow = GUI.createImageIcon("images/client/VAR1/o_yel.jpg");
		
		JPanel pnWrapWhosPlaying = new JPanel();
		pnWrapWhosPlaying.setBackground(Color.WHITE);
		pnWrapWhosPlaying.setLayout(new BoxLayout(pnWrapWhosPlaying, BoxLayout.X_AXIS));
		pnWrapWhosPlaying.add(Box.createHorizontalGlue());
		pnWrapWhosPlaying.add(lbPlayer1Color);
		pnWrapWhosPlaying.add(Box.createRigidArea(new Dimension(3, 0)));
		pnWrapWhosPlaying.add(lbWhosPlaying);
		pnWrapWhosPlaying.add(Box.createRigidArea(new Dimension(3, 0)));
		pnWrapWhosPlaying.add(lbPlayer2Color);
		pnWrapWhosPlaying.add(Box.createHorizontalGlue());
		pnWrapWhosPlaying.setAlignmentX(0.5f);	
		
		
		pnLowerBar.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.33;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		pnLowerBar.add(lbWhosTurnIsIt, gbc);
		
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.33;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		pnLowerBar.add(lbBombInfo, gbc);
		
//		gbc = new GridBagConstraints();
//		gbc.anchor = GridBagConstraints.CENTER;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(0, 0, 0, 0);
//		gbc.gridx = 2;
//		gbc.gridy = 0;
//		gbc.gridwidth = 1;
//		gbc.gridheight = 1;
//		gbc.weightx = 0.25;
//		gbc.weighty = 0;
//		gbc.ipadx = 0;
//		gbc.ipady = 0;
//		pnLowerBar.add(lbGameTime, gbc);
		
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.33;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		pnLowerBar.add(lbTurnTimeRemaining, gbc);

		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(pnWrapWhosPlaying);
		add(pnLowerBar);
	}
	
	public void setPlayers(String player1Nick, Player player1Color, String player2Nick)
	{
		int iconSize = 12;
		
		lbWhosPlaying.setText(player1Nick + " vs " + player2Nick);
		if(player1Color == Player.YELLOW)
		{
			lbPlayer1Color.setIcon(new ImageIcon(iconYellow.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
			lbPlayer2Color.setIcon(new ImageIcon(iconRed.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
		}else
		{
			lbPlayer1Color.setIcon(new ImageIcon(iconRed.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
			lbPlayer2Color.setIcon(new ImageIcon(iconYellow.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
		}
	}
	
	public void setYourTurn()
	{
		lbWhosTurnIsIt.setText("Du bist dran!");
	}
	
	public void setOpponentsTurn(String opponentsName)
	{
		lbWhosTurnIsIt.setText(opponentsName + " ist dran.");
	}
	
	public void setGameTime(int milliseconds)
	{
		lbGameTime.setText("Spielzeit: " + (int)(milliseconds/1000/60) + "min");
	}
	
	public void setRemainingTurnTime(int milliseconds)
	{
		lbTurnTimeRemaining.setText("Zugzeit: " + (int)(milliseconds/1000) + "s");
	}
	
	public void setBombInfo(int remainingBombs)
	{
		lbBombInfo.setText("Bomben: " + remainingBombs);
	}
	
	public void clear()
	{
		lbWhosPlaying.setText("");
		lbWhosTurnIsIt.setText("");
		lbTurnTimeRemaining.setText("");
		lbGameTime.setText("");
		lbBombInfo.setText("");
		lbPlayer1Color.setIcon(null);
		lbPlayer2Color.setIcon(null);
	}
	
	public void clearAfterGame()
	{
		lbWhosTurnIsIt.setText(" ");
		lbTurnTimeRemaining.setText(" ");
		lbGameTime.setText(" ");
		lbBombInfo.setText(" ");
	}
}
