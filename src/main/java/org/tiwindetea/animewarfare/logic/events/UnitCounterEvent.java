package org.tiwindetea.animewarfare.logic.events;

import org.lomadriel.lfc.event.Event;
import org.tiwindetea.animewarfare.logic.FactionType;
import org.tiwindetea.animewarfare.logic.units.Unit;
import org.tiwindetea.animewarfare.logic.units.UnitType;

public class UnitCounterEvent implements Event<UnitCounterEventListener> {
	public enum Type {
		ADDED,
		REMOVED
	}

	private final Type type;
	private final FactionType faction;
	private final UnitType unitType;
	private final Unit unit;

	public UnitCounterEvent(Type type, FactionType faction, UnitType unitType, Unit unit) {
		this.type = type;
		this.faction = faction;
		this.unitType = unitType;
		this.unit = unit;
	}

	public Type getType() {
		return this.type;
	}

	public FactionType getFaction() {
		return this.faction;
	}

	public UnitType getUnitType() {
		return this.unitType;
	}

	public Unit getUnit() {
		return this.unit;
	}

	@Override
	public void notify(UnitCounterEventListener listener) {
		listener.handleUnitEvent(this);
	}
}
