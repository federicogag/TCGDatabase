package it.unibs.db.tcg.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = -125624121548476171L;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static Color backgroundColor;
	private static Color secondaryBackgroundColor;
	private static Color foregroundColor;
	private static Font panelFont;

	private List<ActionListener> listenerList = new ArrayList<>();

	private JTextField loginTextField;
	private JButton btnLogin;
	private JButton btnRegister;

	public LoginPanel() {
		setLayout(null);
		secondaryBackgroundColor = Preferences.getSecondaryBackgroundColor();
		backgroundColor = Preferences.getBackgroundColor();
		setBackground(backgroundColor);
		foregroundColor = Preferences.getForegroundColor();
		setForeground(foregroundColor);
		panelFont = Preferences.getFont();
		setFont(panelFont);

		setFocusable(true);
		addKeyListener(this);

		loginTextField = new JTextField();
		loginTextField.setBounds(WIDTH / 2 - 100, HEIGHT / 2 - 25, 200, 50);
		add(loginTextField);
		loginTextField.addKeyListener(this);
		loginTextField.setFont(panelFont);
		loginTextField.setColumns(10);

		JLabel lblLogin = new JLabel("Nickname");
		lblLogin.setForeground(foregroundColor);
		lblLogin.setBounds(WIDTH / 2 - loginTextField.getWidth(), HEIGHT / 2 - loginTextField.getHeight() / 2, 100, 50);
		lblLogin.setFont(panelFont.deriveFont(Font.BOLD));
		add(lblLogin);

		btnLogin = new JButton("Login");
		btnLogin.setFont(panelFont);
		btnLogin.setBounds(WIDTH / 2 - 100, HEIGHT / 2 + 100, 200, 50);
		add(btnLogin);
		
		btnRegister = new JButton("Nuovo utente");
		btnRegister.setFont(panelFont);
		btnRegister.setBounds(300, 500, 200, 50);
		add(btnRegister);
	

	}
	
	public String getLoginField() {
		return loginTextField.getText();
	}

	@SuppressWarnings("static-access")
	public void showErrorPopup() {
		JOptionPane error = new JOptionPane();
		error.setBounds(getBounds());
		error.showMessageDialog(this, "Inserisci un nickname valido!", "Warning", JOptionPane.ERROR_MESSAGE);
	}

	public void addHomeListener(ActionListener a) {
		btnLogin.addActionListener(a);
	}
	
	public void addRegistrationListener(ActionListener a) {
		btnRegister.addActionListener(a);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
			btnLogin.doClick();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
