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

package org.tiwindetea.animewarfare.net.networkevent;

import org.lomadriel.lfc.event.Event;
import org.tiwindetea.animewarfare.logic.events.CostModifiedEvent;
import org.tiwindetea.animewarfare.logic.units.UnitType;
import org.tiwindetea.animewarfare.net.GameClientInfo;
import org.tiwindetea.animewarfare.net.networkrequests.server.NetCostModified;

public class CostModifiedNetEvent implements Event<CostModifiedNetEventListener> {
	private final GameClientInfo gameClientInfo;
	private final UnitType unitType;
	private final int cost;
	private final CostModifiedEvent.Type type;

	public CostModifiedNetEvent(NetCostModified costModified) {
		this.gameClientInfo = costModified.getGcf();
		this.unitType = costModified.getUnitType();
		this.cost = costModified.getCost();
		this.type = costModified.getType();
	}

	@Override
	public void notify(CostModifiedNetEventListener listener) {
		switch (this.type) {
			case UNIT:
				listener.handleUnitCostModifierEvent(this);
				break;
			case BATTLE:
				listener.handleBattleCostModifierEvent(this);
				break;
			case UNIQUE_ACTION:
				listener.handleUniqueActionCostModifierEvent(this);
				break;
		}
	}

	public GameClientInfo getGameClientInfo() {
		return this.gameClientInfo;
	}

	public UnitType getUnitType() {
		return this.unitType;
	}

	public int getCost() {
		return this.cost;
	}
}
