import javax.swing.JFrame;

public class FBGameFrame extends JFrame{

	FBGameFrame()
	{
		FBGamePanel Panel = new FBGamePanel();
		this.add(Panel);
		this.setTitle("Flappy Bird Rip-Off");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);


	}
}

