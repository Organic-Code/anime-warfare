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

package org.tiwindetea.animewarfare.logic.units;

import org.tiwindetea.animewarfare.logic.FactionType;
import org.tiwindetea.animewarfare.logic.LogicEventDispatcher;
import org.tiwindetea.animewarfare.logic.Zone;
import org.tiwindetea.animewarfare.logic.buffs.UnitBuffedCharacteristics;
import org.tiwindetea.animewarfare.logic.units.events.UnitMovedEvent;

import java.util.Objects;

public class Unit {
	private static int unitCounter;

	private final int ID;
	private final UnitType type;
	private final FactionType faction;

	private final UnitBuffedCharacteristics unitBuffedCharacteristics = new UnitBuffedCharacteristics();
	private Zone zone;

	public Unit(UnitType type) {
		this.ID = unitCounter++;

		this.faction = type.getDefaultFaction();
		this.type = type;
	}

	public int getID() {
		return this.ID;
	}

	/**
	 * Returns the previous zone.
	 */
	public Zone move(Zone zone) {
		Zone previousZone = this.zone;
		previousZone.removeUnit(this);

		this.zone = zone;
		this.zone.addUnit(this);

		LogicEventDispatcher.getInstance().fire(new UnitMovedEvent(this, previousZone, this.zone));

		return previousZone;
	}

	public void removeFromMap() {
		Zone previousZone = this.zone;
		previousZone.removeUnit(this);

		LogicEventDispatcher.getInstance().fire(new UnitMovedEvent(this, previousZone, null));
	}

	public void addInZone(Zone zone) {
		this.zone = zone;
		this.zone.addUnit(this);

		LogicEventDispatcher.getInstance().fire(new UnitMovedEvent(this, null, this.zone));
	}

	public Zone getZone() {
		return this.zone;
	}

	/**
	 * Returns the number of attacks points for this unit.
	 *
	 * @return the number of attacks points
	 */
	public float getAttackPoints() {
		return this.unitBuffedCharacteristics.getAttackPoints()
				+ this.type.getUnitBasicCharacteristics()
				           .getBaseAttackPoints();
	}

	public UnitBuffedCharacteristics getUnitBuffedCharacteristics() {
		return this.unitBuffedCharacteristics;
	}

	public FactionType getFaction() {
		return this.faction;
	}

	public UnitType getType() {
		return this.type;
	}

	public boolean isLevel(UnitLevel level) {
		return this.type.isLevel(level);
	}

	public boolean hasFaction(FactionType faction) {
		return this.faction == faction;
	}

	/**
	 * mascots < common characters < heroes
	 *
	 * @param u1 unit 1
	 * @param u2 unit 2
	 *
	 * @return -1 if u2 is better, 0 if they have same strength and 1 if u1 is better.
	 */
	public static int bestUnitComparator(Unit u1, Unit u2) {
		return bestUnitComparatorByType(u1.getType(), u2.getType());
	}

	/**
	 * mascots < common characters < heroes
	 *
	 * @param u1 unit type 1
	 * @param u2 unit type 2
	 *
	 * @return -1 if u2 is better, 0 if they have same strength and 1 if u1 is better.
	 */
	public static int bestUnitComparatorByType(UnitType u1, UnitType u2) {
		if (u1.getUnitLevel() == UnitLevel.HERO) {
			if (u2.getUnitLevel() == UnitLevel.HERO) {
				return 0;
			} else {
				return 1;
			}
		} else if (u1.getUnitLevel() == UnitLevel.MASCOT) {
			if (u2.getUnitLevel() == UnitLevel.MASCOT) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Unit unit = (Unit) o;
		return this.ID == unit.ID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.ID);
	}
}
