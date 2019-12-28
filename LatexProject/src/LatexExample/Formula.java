package LatexExample;

/** Creates a Window and displays a sample formula using LaTex.
 * @author Shaína N. Muñoz
 * @version 1.0
 * @since 1.0
*/

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class Formula extends JFrame{
	
	/** 
	 * Creates a Formula Window.
	 * @throws HeadlessException
	 */
	
	public Formula() throws HeadlessException{

		super();
		setTitle("Formula Window");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 50, 500);
	}


	/** Creates a Formula Object, creates a LaTex text icon and appends it to a
	 *  window then display the formated LaTex string on screen
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Formula app = new Formula();
		String math = "\\frac{V,m} {K_M+S}";
		TeXFormula formula = new TeXFormula(math);
		TeXIcon ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 40);
		BufferedImage b = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		ti.paintIcon(new JLabel(), b.getGraphics(), 0, 0);
		

		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		label.setIcon(ti);
		panel.add(label);
		app.add(panel);
		app.setVisible(true);
		app.pack();


	}

}
