package it.unibs.db.tcg.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import it.unibs.db.tcg.model.Carta;
import it.unibs.db.tcg.model.Utente;

public class UsersPanel extends JPanel {

	private static final long serialVersionUID = 8151797203093846063L;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static Color backgroundColor;
	private static Color secondaryBackgroundColor;
	private static Color foregroundColor;
	private Font panelFont;
	private JLabel lblTitle;
	private JList<Carta> list;
	private JButton btnBack;

	public UsersPanel() {
		setLayout(null);
		secondaryBackgroundColor = Preferences.getSecondaryBackgroundColor();
		backgroundColor = Preferences.getBackgroundColor();
		setBackground(backgroundColor);
		foregroundColor = Preferences.getForegroundColor();
		setForeground(foregroundColor);
		panelFont = Preferences.getFont();
		setFont(panelFont);

		lblTitle = new JLabel("Utenti trovati");
		lblTitle.setForeground(foregroundColor);
		lblTitle.setFont(panelFont.deriveFont(Font.BOLD));
		lblTitle.setBounds(0, 0, WIDTH, 50);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTitle);

		btnBack = new JButton("Back");
		btnBack.setBounds(650, 450, 90, 50);
		btnBack.setFont(panelFont);
		add(btnBack);
	}

	public void setUserList(List<Utente> utenti) {
		if (utenti.size() == 0)
			showNoUsersFoundPanel();

		list = new JList();
		DefaultListModel dm = new DefaultListModel();
		for (Utente u : utenti) {
			dm.addElement(new ResultRow(u.getAvatar(),u.getNickname() + " " + u.getNomeUtente()));
		}
		list.setCellRenderer(new Renderer());
		list.setModel(dm);
		list.setBounds(50, 50, 700, 350);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBackground(backgroundColor);
		list.setFont(panelFont);
		list.setForeground(foregroundColor);
		list.setFixedCellWidth(list.getWidth());
		list.setFixedCellHeight(50);
		
		JScrollPane scrollPane = new JScrollPane(this,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(list);
		scrollPane.setBounds(50,50,700,400);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		add(scrollPane);
		//add(list);
	}

	private void showNoUsersFoundPanel() {
		JLabel lblNoCards = new JLabel();
		lblNoCards.setBounds(50, 50, 700, 350);
		ImageIcon icon = new ImageIcon("resources/john.gif");
		lblNoCards.setIcon(icon);
		lblNoCards.setFont(panelFont.deriveFont(Font.BOLD));
		lblNoCards.setHorizontalAlignment(JLabel.CENTER);
		JLabel lblNoCardsText = new JLabel("Non c'� niente qui");
		lblNoCardsText.setHorizontalAlignment(JLabel.CENTER);
		lblNoCardsText.setFont(panelFont.deriveFont(Font.BOLD));
		lblNoCardsText.setForeground(foregroundColor);
		lblNoCardsText.setBounds(50, 410, 700, 20);
		add(lblNoCardsText);
		add(lblNoCards);
	}

	public void addUsersListener(ListSelectionListener a) {
		if (list != null)
			list.addListSelectionListener(a);
	}

	public void addBackListener(ActionListener a) {
		btnBack.addActionListener(a);
	}

	public int getListSelectedIndex() {
		return list.getSelectedIndex();
	}
}
