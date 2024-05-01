import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.channels.Pipe;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import java.awt.Graphics;
import javax.swing.JPanel;



public class FBGamePanel extends JPanel implements ActionListener
{

	static final int SCREEN_WIDTH = 1500;
	static final int SCREEN_HEIGHT = 800;
	static int DELAY = 30;
	int score = 0;
	int highScore = 0;
	int scorespeed = 0;
	int restart = 0;
	boolean running = false;
	Timer timer;
	String jumpSound;
	Color skyBlue = new Color(135,206,235);
	Color ground = new Color(255, 204, 102);
	Color grass1 = new Color(0, 153, 0);
	Color grass2 = new Color(51, 204, 51);
	int birdY = 300;
	int Velocity = 0;
	ArrayList<pipe> Pipes = new ArrayList<pipe>();
	int pipeHeight=200;
	boolean bot;
	int pipeSpeed = 5;
	int pipeCounter = 1000000;

	long lastFPSCheck = 0;
	int currentFPS = 0;
	int frames = 0;
	boolean menuScreen = true;
	boolean gameOverScreen = false;



	//JumpSoundEffect jse = new JumpSoundEffect();

	FBGamePanel()
	{
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(skyBlue);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());

		startGame();
	}

	public void startGame()
	{
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);

	}

	public void draw(Graphics g)
	{

		if(running)
		{
			//ground
			g.setColor(ground);
			g.fillRect(0, 675, 1500, 125);
			g.setColor(grass1);
			g.fillRect(0, 650, 1500, 25);



			if(!menuScreen)
			{
				pipeCounter += pipeSpeed;
				if(pipeCounter >= 375)
				{
					pipe Pipe = new pipe(1500,(int)(Math.random()*200+pipeHeight));
					Pipes.add(Pipe);
					pipeCounter = 0;
				}

				for(int i =0; i < Pipes.size(); i++)
				{
					if(!gameOverScreen)
						Pipes.get(i).x-=pipeSpeed;
					//g.setColor(new Color(0,0,0));
					//g.drawRect(Pipes.get(i).x, Pipes.get(i).y, 75, 150);
					g.setColor(new Color(0,255,0));
					g.fillRect(Pipes.get(i).x, 0, 75, Pipes.get(i).y);
					g.fillRect(Pipes.get(i).x, Pipes.get(i).y+pipeHeight, 75, 650-(Pipes.get(i).y+pipeHeight));
				}

				g.setColor(Color.white);

				if(bot)
				{
					g.setColor(Color.RED);
					g.setFont(new Font ("",Font.ITALIC, 15));
				}
				g.drawString(pipeSpeed+" m/s", 10, 30);

				//fps
				frames++;
				if(System.nanoTime() > lastFPSCheck + 1000000000)
				{
					lastFPSCheck = System.nanoTime();
					currentFPS = frames;
					frames = 0;
				}
				g.drawString(currentFPS + " FPS", 10, 60);
				//bird
				g.setColor(new Color(255,0,0));
				g.fillRect(350, birdY, 50, 50);
			}
			else
			{
				//bird
				g.setColor(new Color(255,0,0));
				g.fillRect(350, birdY, 50, 50);

				if (birdY >= 355)
					Velocity = 20;
score = 0;
				Pipes.removeAll(Pipes);
				g.setColor(Color.black);
				g.setFont(new Font ("", Font.BOLD, 75));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("Press Space to Start", (SCREEN_WIDTH - metrics.stringWidth("Press Space to Start"))/2,SCREEN_HEIGHT/2-100);

			}





			if(gameOverScreen)
			{
				g.setColor(Color.black);
				g.setFont(new Font ("", Font.BOLD, 75));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2-100);
				g.setFont(new Font ("", Font.BOLD, 50));
				metrics = getFontMetrics(g.getFont());
				g.drawString("Score: "+score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score))/2,380);
				g.drawString("High Score: "+highScore, (SCREEN_WIDTH - metrics.stringWidth("High Score: " + highScore))/2,450);
				g.setFont(new Font ("", Font.BOLD, 30));
				g.setColor(Color.gray);
				metrics = getFontMetrics(g.getFont());
				g.drawString("Press Enter to restart", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to restart"))/2,550);



			}
			else
			{
				g.setColor(Color.white);
				g.setFont(new Font ("", Font.BOLD, 40));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("Score: "+score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score))/2,g.getFont().getSize());
				g.setFont(new Font ("", Font.BOLD, 20));
				metrics = getFontMetrics(g.getFont());
				g.drawString("High Score: "+highScore, (SCREEN_WIDTH - metrics.stringWidth("High Score: " + highScore))-80,g.getFont().getSize());
				if(score > highScore)
					highScore = score;
			}
		}

	}



	public void checkCollisions()
	{

		if (bot)
		{
			godMode();
		}

		for(int i=0; i < Pipes.size(); i++)
		{
			if(Pipes.get(i).x < 400 && Pipes.get(i).x+75 > 350)
			{
				if(birdY < Pipes.get(i).y || birdY+50 > Pipes.get(i).y+pipeHeight)
				{
					pipeSpeed = 0;
					gameOverScreen = true;
				}
			}
			else if (Pipes.get(i).x+75 <= 350 && Pipes.get(i).scored == false)
			{
				score++;
				scorespeed++;
				Pipes.get(i).scored = true;
			}
		}

	}



	public void actionPerformed(ActionEvent e) {

		if (running)
		{
			if (birdY >= 600) 
			{
				birdY = 600;
				gameOverScreen = true;
			}
			else
			{
				birdY -= Velocity;
				Velocity -= 2;
			}
			checkCollisions();

			if(!gameOverScreen)
				pipeSpeed = 5 + score/10;

		}
		repaint();
	}

	public void godMode()
	{

		int cpx = 1500;
		int n = 0;
		for(int i=0; i < Pipes.size(); i++)
			if(Pipes.get(i).x >= 275 && Pipes.get(i).x < cpx)
			{
				cpx = Pipes.get(i).x;
				n = i;
			}

		if (birdY+50-Velocity >= Pipes.get(n).y+200) 
			Velocity = 20;

		else if (birdY+50-Velocity >= 600)
			Velocity = 20;
	}

	public class MyKeyAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode()) 
			{
			case KeyEvent.VK_SPACE:
				if(menuScreen)
					menuScreen = false;
				if(!gameOverScreen && birdY > 30)
					Velocity = 20;
				break;
			case KeyEvent.VK_Q:
				if(!menuScreen && !gameOverScreen)
					bot = !bot;
				break;
			case KeyEvent.VK_ENTER:
				running = true;
				menuScreen = true;
				birdY = 300;
				gameOverScreen = false;
				break;
			}
		}
	}



}

class pipe
{
	int x;
	int y;
	boolean scored = false;

	pipe(int pipeX, int pipeY)
	{
		x = pipeX;
		y = pipeY;
	}


}


