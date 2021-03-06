package it.unibs.db.tcg.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.List;
import java.sql.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ConnectorService {

	//private Connector connector = new Connector("jdbc:mysql://192.168.1.124:3306/tcg_db", "tcg", "pippo123456");
	 private Connector connector = new Connector("jdbc:mysql://localhost:3306/TCG_DB", "tcg", "pippo123456");
	// private Connector connector = new Connector("jdbc:mysql://localhost:4040/TCG_DB", "root", "");

	public boolean isReachable() {
		return connector.isReachable();
	}

	public Utente getUser(String nickname) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_ATTRIBUTES);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		String _nickname = null;
		String _name = null;
		String _mail = null;
		ImageIcon _avatar = null;
		Date _dataRegistrazione = null;
		try {
			while (set.next()) {
				_nickname = set.getString("Nickname");
				_name = set.getString("Nome_Utente");
				_mail = set.getString("Mail");
				Blob b = set.getBlob("Avatar");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				_avatar = new ImageIcon(i);
				_dataRegistrazione = set.getDate("Data_Registrazione");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}

		Utente user = new Utente();
		user.setNickname(_nickname);
		user.setNomeUtente(_name);
		user.setMail(_mail);
		user.setAvatar(_avatar);
		user.setDataRegistrazione(_dataRegistrazione);
		return user;
	}

	public List<String> getPublicUserCollections(String nickname) {
		connector.openConnection();
		ArrayList<String> collections = new ArrayList<String>();
		connector.submitParametrizedQuery(QueryBuilder.GET_PUBLIC_USER_COLLECTIONS);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				collections.add(set.getString("Nome_Collezione"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return collections;

	}

	public List<String> getUserCollections(String nickname) {
		connector.openConnection();
		ArrayList<String> collections = new ArrayList<String>();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_COLLECTIONS);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				collections.add(set.getString("Nome_Collezione"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return collections;

	}

	public boolean isUserExistant(String nickname) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_ATTRIBUTES);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		try (set) {
			if (set.next() == false)
				return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return true;
	}

	public boolean hasUserCollection(String nickname, String collection) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collection);
		ResultSet set = connector.executeQuery();
		try (set) {
			if (set.next() == false)
				return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return true;
	}

	// da cambiare tipo Valore in TABLE Carta nel DB
	public double getUserTotalCardsValue(String nickname) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_TOTAL_CARDS_VALUE);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		double value = 0;
		try (set) {
			while (set.next()) {
				value += set.getInt(1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return value;
	}
	
	
	public boolean isCollectionComplete(String nickname, String collectionName) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.IS_COLLECTION_COMPLETE);
		for(int i = 1; i <= 6; i++) {
			connector.setStringParameter(i, nickname);
			i++;
			connector.setStringParameter(i, collectionName);
		}
		ResultSet set = connector.executeQuery();
		int num = 0;
		try {
			while (set.next()) {
				num = set.getInt(1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return num != 0;
	}

	public List<Carta> getCardsFromCollection(String nickname, String collectionName) {
		connector.openConnection();
		List<Carta> cardsList = new ArrayList<>();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_CARDS);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		String _abbreviation = null;
		int _number = 0;
		String _nomeCarta = null;
		ImageIcon _immagine = null;
		try {
			while (set.next()) {
				_abbreviation = set.getString("Abbr_Espansione");
				_number = set.getInt("Numero");
				_nomeCarta = set.getString("Nome_Carta");
				Blob b = set.getBlob("Immagine");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				_immagine = new ImageIcon(i);
				Carta c = new Carta(_number, _abbreviation);
				c.setImmagine(_immagine);
				c.setNome(_nomeCarta);
				cardsList.add(c);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return cardsList;
	}

	public String getCardType(int number, String abbr_espansione) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_CARD);
		connector.setIntParameter(1, number);
		connector.setStringParameter(2, abbr_espansione);
		ResultSet set = connector.executeQuery();
		String type = "";
		try {
			while (set.next()) {
				int sel_carta = set.getInt("SEL_Carta");
				if (sel_carta == 0)
					type = Strings.CARTA_POKEMON_BASE;
				else if (sel_carta == 1)
					type = Strings.CARTA_POKEMON_SPECIALE;
				else if (sel_carta == 2)
					type = Strings.CARTA_STRUMENTO;
				else if (sel_carta == 3)
					type = Strings.CARTA_ENERGIA;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return type;
	}

	public Carta getCardFromNumberAndAbbrEspansione(int number, String abbr_espansione) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_CARD);
		connector.setIntParameter(1, number);
		connector.setStringParameter(2, abbr_espansione);
		ResultSet set = connector.executeQuery();
		Carta c = null;
		CartaPokemon c1;
		String _descr_pkmn;
		String _tipo_energia;
		int _ps;
		int _costo_ritirata;
		String _resistenza;
		String _debolezza;
		String _abilita;
		String _attr_spec;
		String _regola;

		try {
			while (set.next()) {
				int _number = set.getInt("Numero");
				String _nomeCarta = set.getString("Nome_Carta");
				Blob b = set.getBlob("Immagine");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon _immagine = new ImageIcon(i);
				String _rarity = set.getString("Rarita");
				String _illustratore = set.getString("Illustratore");
				int _valore = set.getInt("Valore");
				String _abbreviation = set.getString("Abbr_Espansione");

				c = new Carta(_number, _abbreviation);
				c.setNumero(_number);
				c.setImmagine(_immagine);
				c.setNome(_nomeCarta);
				c.setAbbrEspansione(_abbreviation);
				c.setValore(_valore);
				c.setRarita(_rarity);
				c.setIllustratore(_illustratore);

				int sel_carta = set.getInt("SEL_Carta");
				switch (sel_carta) {
					case 0:
						_descr_pkmn = set.getString("Descrizione_PKMN");
						_tipo_energia = set.getString("Tipo_Energia");
						_ps = set.getInt("PS");
						_costo_ritirata = set.getInt("Costo_Ritirata");
						_resistenza = set.getString("Resistenza");
						_debolezza = set.getString("Debolezza");
						_abilita = set.getString("Abilita");
						_attr_spec = set.getString("Attributo_Speciale");
						_regola = set.getString("Regola");

						c1 = createCartaPokemon(c);
						c1.setDescrizione(_descr_pkmn);
						c1.setTipoEnergia(_tipo_energia);
						c1.setPS(_ps);
						c1.setCostoRitirata(_costo_ritirata);
						c1.setResistenza(_resistenza);
						c1.setDebolezza(_debolezza);
						connector.submitParametrizedQuery(QueryBuilder.GET_MOSSE);
						connector.setIntParameter(1, c1.getNumero());
						connector.setStringParameter(2, c1.getAbbrEspansione());
						ResultSet setNomiMosse = connector.executeQuery();
						List<String> nomiMosse = new ArrayList<>();
						while (setNomiMosse.next()) {
							nomiMosse.add(setNomiMosse.getString("Nome_Mossa"));
						}
						List<Mossa> mosse = new ArrayList<>();
						for (int k = 0; k < nomiMosse.size(); k++) {
							connector.submitParametrizedQuery(QueryBuilder.GET_MOSSA_BY_NAME);
							connector.setStringParameter(1, nomiMosse.get(k));
							ResultSet setMosse = connector.executeQuery();
							while (setMosse.next()) {
								Mossa m = new Mossa(setMosse.getString("Nome_Mossa"));
								int energia_richiesta = setMosse.getInt("Energia_Richiesta");
								int danno = setMosse.getInt("Danno");
								String descr = setMosse.getString("Descrizione");
								m.setEnergiaRichiesta(energia_richiesta);
								m.setDanno(danno);
								m.setDescrizione(descr);
								mosse.add(m);
							}
						}

						for (Mossa m : mosse)
							c1.addMossa(m);

						CartaPokemonBase c2 = createCartaPokemonBase(c1);
						c2.setAbilita(_abilita);
						return c2;

					case 1:
						_descr_pkmn = set.getString("Descrizione_PKMN");
						_tipo_energia = set.getString("Tipo_Energia");
						_ps = set.getInt("PS");
						_costo_ritirata = set.getInt("Costo_Ritirata");
						_resistenza = set.getString("Resistenza");
						_debolezza = set.getString("Debolezza");
						_abilita = set.getString("Abilita");
						_attr_spec = set.getString("Attributo_Speciale");
						_regola = set.getString("Regola");

						c1 = createCartaPokemon(c);
						c1.setDescrizione(_descr_pkmn);
						c1.setTipoEnergia(_tipo_energia);
						c1.setPS(_ps);
						c1.setCostoRitirata(_costo_ritirata);
						c1.setResistenza(_resistenza);
						c1.setDebolezza(_debolezza);

						connector.submitParametrizedQuery(QueryBuilder.GET_MOSSE);
						connector.setIntParameter(1, c1.getNumero());
						connector.setStringParameter(2, c1.getAbbrEspansione());
						ResultSet setNomiMosse2 = connector.executeQuery();
						List<String> nomiMosse2 = new ArrayList<>();
						while (setNomiMosse2.next()) {
							nomiMosse2.add(setNomiMosse2.getString("Nome_Mossa"));
						}
						List<Mossa> mosse2 = new ArrayList<>();
						for (int k = 0; k < nomiMosse2.size(); k++) {
							connector.submitParametrizedQuery(QueryBuilder.GET_MOSSA_BY_NAME);
							connector.setStringParameter(1, nomiMosse2.get(k));
							ResultSet setMosse = connector.executeQuery();
							while (setMosse.next()) {
								Mossa m = new Mossa(setMosse.getString("Nome_Mossa"));
								int energia_richiesta = setMosse.getInt("Energia_Richiesta");
								int danno = setMosse.getInt("Danno");
								String descr = setMosse.getString("Descrizione");
								m.setEnergiaRichiesta(energia_richiesta);
								m.setDanno(danno);
								m.setDescrizione(descr);
								mosse2.add(m);
							}
						}

						for (Mossa m : mosse2)
							c1.addMossa(m);
						CartaPokemonSpeciale c3 = createCartaPokemonSpeciale(c1);
						c3.setAttributoSpeciale(_attr_spec);
						c3.setRegola(_regola);

						return c3;
					case 2:
						CartaStrumento c4 = createCartaStrumento(c);
						String _descr_strumento = set.getString("Descrizione_Strum");
						String _effetto_strumento = set.getString("Effetto_Strum");
						c4.setDescrizione(_descr_strumento);
						c4.setEffetto(_effetto_strumento);
						return c4;

					case 3:
						CartaEnergia c5 = createCartaEnergia(c);
						String _tipo_carta_energia = set.getString("Tipo_Carta_Energia");
						c5.setTipo(_tipo_carta_energia);
						return c5;
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return c;
	}

	public List<Utente> getSearchResult(String name) {
		connector.openConnection();
		List<Utente> usersList = new ArrayList<>();
		connector.submitParametrizedQuery(QueryBuilder.GET_USERS_BY_NAME);
		connector.setStringParameter(1, name + "%");
		ResultSet set = connector.executeQuery();
		String _nickname = null;
		String _name = null;
		String _mail = null;
		ImageIcon _avatar = null;
		Date _dataRegistrazione = null;
		try {
			while (set.next()) {
				_nickname = set.getString("Nickname");
				_name = set.getString("Nome_Utente");
				_mail = set.getString("Mail");
				Blob b = set.getBlob("Avatar");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				_avatar = new ImageIcon(i);
				_dataRegistrazione = set.getDate("Data_Registrazione");
				Utente user = new Utente();
				user.setNickname(_nickname);
				user.setNomeUtente(_name);
				user.setMail(_mail);
				user.setAvatar(_avatar);
				user.setDataRegistrazione(_dataRegistrazione);
				usersList.add(user);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}

		return usersList;

	}

	public List<Carta> getSearchResult(CardSearchObject s) {
		connector.openConnection();
		createAndSubmitCardSearchQuery(s);

		List<Carta> cardsList = new ArrayList<>();
		ResultSet set = connector.executeQuery();
		String _abbreviation = null;
		int _number = 0;
		String _nomeCarta = null;
		ImageIcon _immagine = null;
		try {
			while (set.next()) {
				_abbreviation = set.getString("Abbr_Espansione");
				_number = set.getInt("Numero");
				_nomeCarta = set.getString("Nome_Carta");
				Blob b = set.getBlob("Immagine");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				_immagine = new ImageIcon(i);
				Carta c = new Carta(_number, _abbreviation);
				c.setImmagine(_immagine);
				c.setNome(_nomeCarta);
				cardsList.add(c);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return cardsList;

	}

	private void createAndSubmitCardSearchQuery(CardSearchObject s) {
		Map<Integer, String> stringParametersMap = new HashMap<>();
		Map<Integer, Integer> intParametersMap = new HashMap<>();
		int position = 1;
		String query = "";
		if (s.hasCardName()) {
			query += QueryBuilder.GET_CARDS_BY_NAME + " INTERSECT ";
			stringParametersMap.put(position, s.getCardName() + "%");
			position++;
		}

		if (s.hasExp()) {
			query += QueryBuilder.GET_CARDS_BY_EXP + " INTERSECT ";
			stringParametersMap.put(position, s.getExp());
			position++;
		}

		if (s.hasCardIllustrator()) {
			query += QueryBuilder.GET_CARDS_BY_ILLUSTRATOR + " INTERSECT ";
			stringParametersMap.put(position, s.getCardIllustrator() + "%");
			position++;
		}

		if (s.hasCardType()) {
			query += "(";
			boolean dopoPrimoCiclo = false;
			for (String type : s.getCardType()) {
				if (dopoPrimoCiclo)
					query += " UNION ";
				switch (type) {
					case "Pokemon":
						query += "((" + QueryBuilder.GET_CARDS_TYPE + " UNION " + QueryBuilder.GET_CARDS_TYPE
								+ ") INTERSECT ";
						intParametersMap.put(position, 0);
						position++;
						intParametersMap.put(position, 1);
						position++;
						query += QueryBuilder.GET_CARDS_BY_PS + ")";
						intParametersMap.put(position, s.getLowerPSValue());
						position++;
						intParametersMap.put(position, s.getUpperPSValue());
						position++;
						break;
					case "Strumento":
						query += QueryBuilder.GET_CARDS_TYPE;
						intParametersMap.put(position, 2);
						position++;
						break;
					case "Energia":
						query += QueryBuilder.GET_CARDS_TYPE;
						intParametersMap.put(position, 3);
						position++;
						break;
				}
				dopoPrimoCiclo = true;
			}
			query += ") INTERSECT ";
		}

		if (s.hasEnergyType()) {
			query += "(" + s.getEnergyType().stream().map(t -> QueryBuilder.GET_CARDS_BY_ENERGY_TYPE)
					.collect(Collectors.joining(" UNION ")) + ") INTERSECT ";
			for (String type : s.getEnergyType()) {
				stringParametersMap.put(position, type);
				position++;
			}
		}
		if (s.hasRarityType()) {
			query += "(" + s.getRarityType().stream().map(t -> QueryBuilder.GET_CARDS_BY_RARITY)
					.collect(Collectors.joining(" UNION ")) + ") INTERSECT ";
			for (String rarity : s.getRarityType()) {
				stringParametersMap.put(position, rarity);
				position++;
			}
		}

		query += QueryBuilder.GET_CARDS_BY_ECONOMIC_VALUE;
		intParametersMap.put(position, s.getLowerValueBarValue());
		position++;
		intParametersMap.put(position, s.getUpperValueBarValue());
		position++;

		connector.submitParametrizedQuery(query);
		for (Entry<Integer, String> entry : stringParametersMap.entrySet())
			connector.setStringParameter(entry.getKey(), entry.getValue());
		for (Entry<Integer, Integer> entry : intParametersMap.entrySet())
			connector.setIntParameter(entry.getKey(), entry.getValue());
	}

	public void updateUserName(String nickname, String newName) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.UPDATE_USERNAME);
		connector.setStringParameter(1, newName);
		connector.setStringParameter(2, nickname);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public void updateMail(String nickname, String newMail) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.UPDATE_MAIL);
		connector.setStringParameter(1, newMail);
		connector.setStringParameter(2, nickname);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public void updateAvatar(String nickname, ImageIcon avatar) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.UPDATE_AVATAR);
		connector.setImageParameter(1, avatar);
		connector.setStringParameter(2, nickname);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public void updateCollectionName(String newCollectionName, String nickname, String oldCollectionName) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.UPDATE_COLLECTION_NAME_COLLEZIONE_TABLE);
		connector.setStringParameter(1, newCollectionName);
		connector.setStringParameter(2, nickname);
		connector.setStringParameter(3, oldCollectionName);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public Map<ImageIcon, String> getCountOfCardsPerExpansion(String nickname) {
		Map<ImageIcon, String> result = new HashMap<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COUNT_OF_CARDS_PER_EXPANSION);
		connector.setStringParameter(1, nickname);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				String abbr_esp = set.getString("Abbr_Espansione");
				String name_esp = set.getString("Nome_Espansione");
				int num = set.getInt(4);
				Blob b = set.getBlob("Icona");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon _immagine = new ImageIcon(i);
				result.put(_immagine, abbr_esp + " | " + name_esp + " : " + num + " carte");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> getUsersByCard(int num, String abbr_exp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USERS_BY_CARD);
		connector.setIntParameter(1, num);
		connector.setStringParameter(2, abbr_exp);
		ResultSet set = connector.executeQuery();
		List<String> result = new ArrayList<>();
		try {
			while (set.next()) {
				String nickname = set.getString("Nickname");
				result.add(nickname);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void updateCollectionVisibility(boolean visible, String nickname, String collectionName) {
		int visibility = visible ? 1 : 0;
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.UPDATE_COLLECTION_VISIBILITY);
		connector.setIntParameter(1, visibility);
		connector.setStringParameter(2, nickname);
		connector.setStringParameter(3, collectionName);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public int getCollectionTotalNumberCards(String nickname, String collectionName) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_TOTAL_NUMBER_CARD_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		int number = 0;
		try (set) {
			if (set.next() == false) {
				number = set.getInt(1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		connector.closeStatement();
		connector.closeConnection();
		return number;
	}
	
	public Map<ImageIcon, String> getCollectionCountOfCardsPerExpansion(String nickname, String collectionName) {
		Map<ImageIcon, String> result = new HashMap<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_COUNT_OF_CARDS_PER_EXPANSION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				String abbr_esp = set.getString("Abbr_Espansione");
				String name_esp = set.getString("Nome_Espansione");
				int num = set.getInt(4);
				Blob b = set.getBlob("Icona");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon _immagine = new ImageIcon(i);
				result.put(_immagine, abbr_esp + " | " + name_esp + " : " + num + " carte");
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String, Integer> getCollectionCountOfCardsPerRarity(String nickname, String collectionName) {
		Map<String, Integer> result = new HashMap<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_COUNT_OF_CARDS_PER_RARITY);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result.put(set.getString(1), set.getInt(2));
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getMaxCardValueInCollection(String nickname, String collectionName) {
		String result = "";
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_MAX_CARD_VALUE_IN_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getString(1) + " (Espansione: " + set.getString(2) + ") Valore: " + set.getInt(3);
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getMinCardValueInCollection(String nickname, String collectionName) {
		String result = "";
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_MIN_CARD_VALUE_IN_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getString(1) + " (Espansione: " + set.getString(2) + ") Valore: " + set.getInt(3);
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getAvgCardValueInCollection(String nickname, String collectionName) {
		int result = 0;
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_AVG_CARD_VALUE_IN_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getInt(1);
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getTotalCollectionValue(String nickname, String collectionName) {
		int result = 0;
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_TOTAL_VALUE);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getInt(1);
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getTotalCollectionCardsNumber(String nickname, String collectionName) {
		int result = 0;
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_CARD_NUMBER);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getInt(1);
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getCollectionStartDate(String nickname, String collectionName) {
		String result = "";
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_COLLECTION_START_DATE);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				result = set.getDate(1).toString();
			}
			set.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isThereCardInCollection(String nickname, String collectionName, int num_card, String abbr_esp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.IS_THERE_CARD_IN_COLLECTION);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		connector.setIntParameter(3, num_card);
		connector.setStringParameter(4, abbr_esp);
		ResultSet set = connector.executeQuery();
		try (set) {
			if (set.next() == false)
				return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return true;
	}

	public List<Carta> getCardsByName(String name) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_CARDS_BY_NAME);
		connector.setStringParameter(1, name);
		ResultSet set = connector.executeQuery();
		List<Carta> result = new ArrayList<>();
		if (set != null) {
			try (set) {
				if (set.next()) {
					String nome_carta = set.getString("Nome_Carta");
					String abbr_Espansione = set.getString("Abbr_Espansione");
					int numero = set.getInt("Numero");
					Blob b = set.getBlob("Immagine");
					byte[] imageByte = b.getBytes(1, (int) b.length());
					InputStream is = new ByteArrayInputStream(imageByte);
					BufferedImage imag = ImageIO.read(is);
					Image i = imag;
					ImageIcon _immagine = new ImageIcon(i);
					Carta c = new Carta(numero, abbr_Espansione);
					c.setImmagine(_immagine);
					c.setNome(nome_carta);
					result.add(c);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void insertCardInCollection(String nickname, String collectionName, int num_card, String abbr_esp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.INSERT_CARD_IN_COMPOSTA);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, collectionName);
		connector.setIntParameter(3, num_card);
		connector.setStringParameter(4, abbr_esp);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public void removeCardFromCollection(String collectionName, int num_card, String abbr_esp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.DELETE_CARD_FROM_COLLECTION);
		connector.setStringParameter(1, collectionName);
		connector.setIntParameter(2, num_card);
		connector.setStringParameter(3, abbr_esp);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public ImageIcon getRandomCard() {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_RANDOM_CARD);
		ImageIcon _immagine = null;
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				Blob b = set.getBlob("Immagine");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				_immagine = new ImageIcon(i);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null)
					set.close();
				connector.closeStatement();
				connector.closeConnection();
			}
			catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return _immagine;
	}

	public void createCollection(String nickname, String nameCollection, int visible) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.CREATE_COLLECTION_COLLECTION_TABLE);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, nameCollection);
		connector.setIntParameter(3, visible);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		connector.setCurrentDateParameter(4, timestamp);
		connector.execute();
		connector.closeConnection();
		connector.closeConnection();
	}

	public void deleteCollection(String nickname, String nameCollection) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.DELETE_COLLECTION_COLLECTION_TABLE);
		connector.setStringParameter(1, nickname);
		connector.setStringParameter(2, nameCollection);
		connector.execute();
		connector.closeConnection();
		connector.closeConnection();
	}

	public void createUser(Utente user) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.CREATE_USER);
		connector.setStringParameter(1, user.getNickname());
		if (user.getNomeUtente().length() > 0) {
			connector.setStringParameter(2, user.getNomeUtente());
		}
		else {
			connector.setStringParameter(2, "");
		}
		if (user.getMail().length() > 0) {
			connector.setStringParameter(3, user.getMail());
		}
		else {
			connector.setStringParameter(3, "");
		}
		connector.setImageParameter(4, user.getAvatar());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Date date = new Date(timestamp.getTime());
		user.setDataRegistrazione(date);
		connector.setCurrentDateParameter(5, timestamp);
		connector.execute();
		connector.closeStatement();
		connector.closeConnection();
	}

	public String getNameNextStageByNumAndAbbrExp(int num, String abbr_esp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_NAME_NEXT_STAGE_BY_NUM_AND_ABBR_EXP);
		connector.setIntParameter(1, num);
		connector.setStringParameter(2, abbr_esp);
		ResultSet set = connector.executeQuery();
		String result = null;
		if (set != null) {
			try {
				while (set.next()) {
					result = set.getString("Stage_successivo");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getNamePreStageByNumAndAbbrExp(int num, String abbr_esp) {
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_NAME_PRE_STAGE_BY_NUM_AND_ABBR_EXP);
		connector.setIntParameter(1, num);
		connector.setStringParameter(2, abbr_esp);
		ResultSet set = connector.executeQuery();
		String result = null;
		if (set != null) {
			try {
				while (set.next()) {
					result = set.getString("Stage_precedente");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public List<Utente> getRankingCardValue() {
		List<Utente> result = new ArrayList<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_RANKING_BY_TOTAL_CARDS_VALUE);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				String nickname = set.getString("Nickname");
				double valoreTotale = set.getDouble("ValoreTotale");
				Blob b = set.getBlob("Avatar");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon avatar = new ImageIcon(i);
				Utente u = new Utente();
				u.setNickname(nickname);
				u.setAvatar(avatar);
				u.setTotalCardsValue(valoreTotale);
				result.add(u);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public List<Utente> getRankingTotalCardNumber() {
		List<Utente> result = new ArrayList<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_RANKING_BY_TOTAL_CARDS_NUMBER);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				String nickname = set.getString("Nickname");
				int totale = set.getInt("TotaleCarte");
				Blob b = set.getBlob("Avatar");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon avatar = new ImageIcon(i);
				Utente u = new Utente();
				u.setNickname(nickname);
				u.setAvatar(avatar);
				u.setTotalCard(totale);
				result.add(u);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public List<Utente> getRankingMaxCardValue() {
		List<Utente> result = new ArrayList<>();
		connector.openConnection();
		connector.submitParametrizedQuery(QueryBuilder.GET_USER_RANKING_BY_MAX_CARD_VALUE);
		ResultSet set = connector.executeQuery();
		try {
			while (set.next()) {
				String nickname = set.getString("Nickname");
				double valoreMax = set.getDouble("ValoreMax");
				Blob b = set.getBlob("Avatar");
				byte[] imageByte = b.getBytes(1, (int) b.length());
				InputStream is = new ByteArrayInputStream(imageByte);
				BufferedImage imag = ImageIO.read(is);
				Image i = imag;
				ImageIcon avatar = new ImageIcon(i);
				Utente u = new Utente();
				u.setNickname(nickname);
				u.setAvatar(avatar);
				u.setMaxCardValue(valoreMax);
				result.add(u);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private CartaEnergia createCartaEnergia(Carta c) {
		CartaEnergia result = new CartaEnergia(c.getNumero(), c.getAbbrEspansione());
		result.setImmagine(c.getImmagine());
		result.setNome(c.getNome());
		result.setValore(c.getValore());
		result.setRarita(c.getRarita());
		result.setIllustratore(c.getIllustratore());
		return result;
	}

	private CartaStrumento createCartaStrumento(Carta c) {
		CartaStrumento result = new CartaStrumento(c.getNumero(), c.getAbbrEspansione());
		result.setImmagine(c.getImmagine());
		result.setNome(c.getNome());
		result.setValore(c.getValore());
		result.setRarita(c.getRarita());
		result.setIllustratore(c.getIllustratore());
		return result;
	}

	private CartaPokemon createCartaPokemon(Carta c) {
		CartaPokemon result = new CartaPokemon(c.getNumero(), c.getAbbrEspansione());
		result.setImmagine(c.getImmagine());
		result.setNome(c.getNome());
		result.setValore(c.getValore());
		result.setRarita(c.getRarita());
		result.setIllustratore(c.getIllustratore());
		return result;
	}

	private CartaPokemonBase createCartaPokemonBase(CartaPokemon c) {
		CartaPokemonBase result = new CartaPokemonBase(c.getNumero(), c.getAbbrEspansione());
		result.setImmagine(c.getImmagine());
		result.setNome(c.getNome());
		result.setValore(c.getValore());
		result.setRarita(c.getRarita());
		result.setIllustratore(c.getIllustratore());
		result.setDescrizione(c.getDescrizione());
		result.setTipoEnergia(c.getTipoEnergia());
		result.setPS(c.getPS());
		result.setCostoRitirata(c.getCostoRitirata());
		result.setResistenza(c.getResistenza());
		result.setDebolezza(c.getDebolezza());
		for (Mossa m : c.getMosse())
			result.addMossa(m);
		return result;
	}

	private CartaPokemonSpeciale createCartaPokemonSpeciale(CartaPokemon c) {
		CartaPokemonSpeciale result = new CartaPokemonSpeciale(c.getNumero(), c.getAbbrEspansione());
		result.setImmagine(c.getImmagine());
		result.setNome(c.getNome());
		result.setValore(c.getValore());
		result.setRarita(c.getRarita());
		result.setIllustratore(c.getIllustratore());
		result.setDescrizione(c.getDescrizione());
		result.setTipoEnergia(c.getTipoEnergia());
		result.setPS(c.getPS());
		result.setCostoRitirata(c.getCostoRitirata());
		result.setResistenza(c.getResistenza());
		result.setDebolezza(c.getDebolezza());
		for (Mossa m : c.getMosse())
			result.addMossa(m);
		return result;
	}

}
