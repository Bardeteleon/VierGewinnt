package useful;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class LoadingFrame extends JDialog
{
	JProgressBar bar;
	JLabel lab;
	Process process;
	

	public LoadingFrame(String path, String file)
	{
		ProcessBuilder b = new ProcessBuilder("cmd","/c", "java "+file);
		b.directory(new File(path));
		try
		{
			process = b.start();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	public void closeLoadingFrame()
	{
		BufferedOutputStream b = new BufferedOutputStream(process.getOutputStream());
		try
		{
			b.write(0);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				b.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private LoadingFrame() throws IOException
	{		
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setDoubleBuffered(true);

		lab = new JLabel("VierGewinnt");
		lab.setFont(new Font("Sans Serif", Font.BOLD, 30));
		lab.setHorizontalAlignment(SwingConstants.CENTER);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(lab, BorderLayout.CENTER);
		getContentPane().add(bar, BorderLayout.SOUTH);

		setSize(300, 150);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setVisible(true);
	}

	private int waitForInput()
	{
		BufferedInputStream processReader = new BufferedInputStream(System.in);
		try
		{
			int in = -1;
			do{
				in  = processReader.read();
			}while(in != 0);
			return in;
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				processReader.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return Integer.MIN_VALUE;
	}

	public static void main(String[] args) throws IOException
	{
		LoadingFrame load = new LoadingFrame();
		if(load.waitForInput() == 0)
		{
			load.setVisible(false);
			load.dispose();
		}
	}
}
