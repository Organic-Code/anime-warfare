////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare.logic;

import org.tiwindetea.animewarfare.logic.units.Studio;
import org.tiwindetea.animewarfare.logic.units.Unit;
import org.tiwindetea.animewarfare.logic.units.UnitType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GameBoard {
	private final GameMap gameMap;

	private final Map<Integer, Player> players = new HashMap<>(); // todo possibly useless [ players are now in a circular linked list fashion.
	// /!\ They are however using WeakReferences, so they need to be stored somewhere
	private final List<Player> playersInOrder = new ArrayList<>(); // Used to cache. [Same for this one]

	private Player lastFirstPlayer;
	private Player firstPlayer;
	private int firstPlayerID;
	private boolean clockWiseRotationTurn;

	private AdvertisingCampaignRightsPool advertisingCampaignRightsPool = new AdvertisingCampaignRightsPool();

	private final EndGameMonitor endGameMonitor = new EndGameMonitor();

	private final MarketingLadder marketingLadder;

	public GameBoard(Map<Integer, FactionType> players) {
		if (players.size() < 2 && players.size() > 4) {
			throw new IllegalArgumentException("Incorrect number of players, "
					+ players.size()
					+ " player(s) requested, should be between 2 and 4");
		}

		initializePlayers(players);
		this.gameMap = new GameMap(this.players.size());
		this.marketingLadder = new MarketingLadder(this.players.size());

		List<Zone> zones = new ArrayList<>(this.gameMap.getZones());
		Random r = new Random();
		for (Player player : this.players.values()) {
			Zone playerZone = zones.get(r.nextInt(zones.size()));
			zones.remove(playerZone);

			Unit unit = null;
			switch (player.getFaction()) {
				case NO_NAME:
					unit = new Unit(UnitType.RUSSELL_JIN);
					break;
				case THE_BLACK_KNIGHTS:
					unit = new Unit(UnitType.NUNNALLY);
					break;
				case HAIYORE:
					unit = new Unit(UnitType.YOICHI_TAKEHIKO);
					break;
				case F_CLASS_NO_BAKA:
					unit = new Unit(UnitType.YOSHII_AKIHISA);
					break;
			}

			player.getUnitCounter().addUnit(unit.getType(), unit.getID());
			unit.addInZone(playerZone);

			Studio studio = new Studio(playerZone.getID(), player);
			playerZone.setStudio(studio);
			studio.setController(unit);

			for (int i = 0; i < 5; i++) {
				unit = new Unit(unit.getType());
				player.getUnitCounter().addUnit(unit.getType(), unit.getID());
				unit.addInZone(playerZone);
			}
		}
	}

	public int getLastFirstPlayerID() {
		return this.lastFirstPlayer.getID();
	}

	public Player getPlayer(int id) {
		return this.players.get(new Integer(id));
	}

	public Player getPlayer(FactionType factionType) {
		return this.players.values().stream()
		                   .filter(p -> p.hasFaction(factionType))
		                   .findFirst()
		                   .orElse(null);
	}

	public Collection<Player> getPlayers() {
		return Collections.unmodifiableCollection(this.players.values());
	}

	public Player selectRandomPlayer() {

		Player p;
		int chosenPlayer = new Random().nextInt(this.players.size());

		Collection<Player> localPlayers = this.players.values();
		Iterator<Player> playerIterator = localPlayers.iterator();
		do {
			p = playerIterator.next();
			--chosenPlayer;
		} while (chosenPlayer >= 0);

		return p;
	}

	public List<Player> getPlayersInOrder() { // todo : possibly useless (cf line 42, players hashmap)
		return Collections.unmodifiableList(this.playersInOrder);
	}

	public Player getFirstPlayer() {
		return this.firstPlayer;
	}

	public static List<Integer> getPlayersIndex(List<Player> players) {
		return players.stream().map(player -> Integer.valueOf(player.getID())).collect(Collectors.toList());
	}

	public void initializeTurn(Player firstPlayer, boolean clockWiseRotationTurn) {
		this.lastFirstPlayer = getPlayer(this.firstPlayerID);
		this.firstPlayerID = firstPlayer.getID();
		this.clockWiseRotationTurn = clockWiseRotationTurn;

		buildPlayerList();
	}

	private void initializePlayers(Map<Integer, FactionType> players) {

		TreeSet<Player> playersInOrder = new TreeSet<>(Comparator.comparingInt(Player::getID));
		players.entrySet().stream()
				.map(entry -> new Player(entry.getKey(), entry.getValue()))
				.forEach(p -> playersInOrder.add(p));

		Player previous = null;
		Player first = null;

		for (Player current : playersInOrder) {
			this.players.put(new Integer(current.getID()), current);
			if (previous != null) {
				current.setClockwisePreviousPlayer(previous);
				previous.setClockwiseNextPlayer(current);
			} else {
				first = current;
			}
			previous = current;
		}
		previous.setClockwiseNextPlayer(first);
		first.setClockwisePreviousPlayer(previous);
	}

	private void buildPlayerList() {
		this.firstPlayer = this.players.get(new Integer(this.firstPlayerID));
		this.firstPlayer.setClockwiseness(this.clockWiseRotationTurn);
	}

	public GameMap getMap() {
		return this.gameMap;
	}

	public AdvertisingCampaignRightsPool getAdvertisingCampaignRightsPool() {
		return this.advertisingCampaignRightsPool;
	}

	public MarketingLadder getMarketingLadder() {
		return this.marketingLadder;
	}

	public void destroy() {
		this.endGameMonitor.destroy();
	}

}
