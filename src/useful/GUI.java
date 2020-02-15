package useful;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class GUI
{
	public static final String GREENSTYLE = "greenstyle";
	public static final String REDSTYLE = "redstyle";
	public static final String BLACKSTYLE = "blackstyle";
	public static final String GRINSMILEY = ":)";
	public static final String SADSMILEY = ":(";
	public static final String TOUNGESMILEY = ":P";
	public static final String LOUGHSMILEY = ":D";

	public static void addComponent(Container cont, Component c, int fillMode, int anchor, Insets insets, int x, int y, int width, int height, double weightx, double weighty)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = anchor;
		gbc.fill = fillMode;
		gbc.insets = insets;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		cont.add(c, gbc);
	}

	public static ImageIcon createImageIcon(Object parent, String path)
	{
		URL imgURL = parent.getClass().getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL);
		} else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static JDialog getHelpDialog(Object parent, String file)
	{
		JTextArea area = new JTextArea(loadTextFile(parent, file));

		area.setFont(new Font("Sans Serif", Font.PLAIN, 14));
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);

		JScrollPane pane = new JScrollPane(area);

		JDialog dia;
		if (parent instanceof Frame)
			dia = new JDialog((Frame) parent);
		else
			dia = new JDialog();
		Container c = dia.getContentPane();
		c.setLayout(new BorderLayout());
		dia.setModal(true);
		dia.setTitle("Hilfe");
		dia.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		c.add(BorderLayout.CENTER, pane);

		return dia;
	}

	public static String loadTextFile(Object parent, String file)
	{
		InputStream stream = parent.getClass().getResourceAsStream(file);
		char[] data = null;
		if (stream != null)
		{
			InputStreamReader reader = new InputStreamReader(stream);
			try
			{
				int size = stream.available();
				data = new char[size];
				reader.read(data, 0, size);
			} catch (IOException e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					stream.close();
					reader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return new String(data);
	}

	public static void changeFullscreenOrWindow(JFrame wnd)
	{
		if (!wnd.isUndecorated())
		{
			wnd.setPreferredSize(new Dimension(wnd.getWidth(), wnd.getHeight()));
		}
		if (wnd.isUndecorated())
		{
			wnd.dispose();
			wnd.setUndecorated(false);
			wnd.setSize(wnd.getPreferredSize().width, wnd.getPreferredSize().height);
			wnd.setLocationRelativeTo(null);
			wnd.setVisible(true);
		} else
		{
			wnd.dispose();
			wnd.setUndecorated(true);
			wnd.setBounds(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
			wnd.repaint();
			wnd.setVisible(true);
		}
	}

	public static DefaultStyledDocument getStyledDoc(Object parent)
	{
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(def, "SansSerif");
		StyleConstants.setFontSize(def, 14);

		DefaultStyledDocument doc = new DefaultStyledDocument();
		Style s = doc.addStyle(REDSTYLE, def);
		StyleConstants.setForeground(s, new Color(184, 5, 0));
		StyleConstants.setFontSize(s, 20);
		StyleConstants.setBold(s, true);

		s = doc.addStyle(GREENSTYLE, def);
		StyleConstants.setForeground(s, new Color(63, 253, 0));

		s = doc.addStyle(BLACKSTYLE, def);
		StyleConstants.setForeground(s, Color.BLACK);

		ImageIcon sm;

		sm = new ImageIcon(GUI.createImageIcon(parent, "bilder/Smileys/Smiley1.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
		s = doc.addStyle(LOUGHSMILEY, def);
		StyleConstants.setIcon(s, sm);

		sm = new ImageIcon(GUI.createImageIcon(parent, "bilder/Smileys/Smiley2.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
		s = doc.addStyle(SADSMILEY, def);
		StyleConstants.setIcon(s, sm);

		sm = new ImageIcon(GUI.createImageIcon(parent, "bilder/Smileys/Smiley3.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
		s = doc.addStyle(GRINSMILEY, def);
		StyleConstants.setIcon(s, sm);

		sm = new ImageIcon(GUI.createImageIcon(parent, "bilder/Smileys/Smiley4.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
		s = doc.addStyle(TOUNGESMILEY, def);
		StyleConstants.setIcon(s, sm);

		return doc;
	}

	public static void insertSmileys(StyledDocument sdoc, int offset, int length)
	{
		for (int i = offset; i < length - 1; i++)
		{
			String element;
			try
			{
				element = sdoc.getText(i, 2);

				if (element.equals(LOUGHSMILEY))
				{
					sdoc.remove(i, 2);
					sdoc.insertString(i, " ", sdoc.getStyle(LOUGHSMILEY));
				} else if (element.equals(SADSMILEY))
				{
					sdoc.remove(i, 2);
					sdoc.insertString(i, " ", sdoc.getStyle(SADSMILEY));
				} else if (element.equals(GRINSMILEY))
				{
					sdoc.remove(i, 2);
					sdoc.insertString(i, " ", sdoc.getStyle(GRINSMILEY));
				} else if (element.equals(TOUNGESMILEY))
				{
					sdoc.remove(i, 2);
					sdoc.insertString(i, " ", sdoc.getStyle(TOUNGESMILEY));
				}

			} catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static class SplashProgress extends Thread
	{
		SplashScreen splash;
		Graphics2D g;
		Point pos;
		Dimension dim;
		int aktPos = 1;
		int maxPos;
		int height = 15;
		int length = 40;
		int speed = 15;
		int speedInterval = 50;

		public SplashProgress(SplashScreen splash)
		{
			if (splash != null)
			{
				this.splash = splash;
				g = splash.createGraphics();
				pos = new Point();
				dim = splash.getSize();
				maxPos = (int) (dim.width * 0.9) - 1;
				pos.x = (int) (dim.width * 0.05);
				pos.y = (int) (dim.height * 0.8);
				start();
			}
		}

		public void run()
		{
			while (splash.isVisible())
			{
				aktPos += speed;
				if (aktPos >= maxPos - length)
				{
					speed = -Math.abs(speed);
					aktPos = maxPos - length;
				}
				if (aktPos <= 1)
				{
					speed = Math.abs(speed);
					aktPos = 1;
				}
				g.setColor(Color.BLACK);
				g.drawRect(pos.x, pos.y, (int) (dim.width * 0.9), height);
				g.setColor(Color.WHITE);
				g.fillRect(pos.x + 1, pos.y + 1, (int) (dim.width * 0.9) - 1, height - 1);
				g.setColor(Color.ORANGE);
				g.fillRect(pos.x + aktPos, pos.y + 1, length, height - 1);
				splash.update();
				try
				{
					Thread.sleep(speedInterval);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		public void setLocation(int x, int y)
		{
			pos.x = x;
			pos.y = y;
		}

		public void setLocationRelative(double width, double height)
		{

		}
	}
}
