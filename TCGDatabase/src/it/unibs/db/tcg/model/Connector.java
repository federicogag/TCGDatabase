package it.unibs.db.tcg.model;

import java.sql.*;

public class Connector {

	private String url;
	private String user;
	private String password;
	private Connection con;
	private Statement stmt;

	public Connector(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void openConnection() {
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query) {
		ResultSet res = null;
		try {
			Statement stmt = con.createStatement();
			res = stmt.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void execute(String query) {
		try {
			stmt = con.createStatement();
			stmt.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeStatement() {
		if (stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
