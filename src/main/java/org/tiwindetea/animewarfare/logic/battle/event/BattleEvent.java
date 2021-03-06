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

package org.tiwindetea.animewarfare.logic.battle.event;

import org.lomadriel.lfc.event.Event;
import org.tiwindetea.animewarfare.logic.battle.BattleContext;

public class BattleEvent implements Event<BattleEventListener> {
	public enum Type {
		PRE_BATTLE {
			@Override
			void handle(BattleEvent event, BattleEventListener battleEventListener) {
				battleEventListener.handlePreBattle(event);
			}
		},
		DURING_BATTLE {
			@Override
			void handle(BattleEvent event, BattleEventListener battleEventListener) {
				battleEventListener.handleDuringBattle(event);
			}
		},
		POST_BATTLE {
			@Override
			void handle(BattleEvent event, BattleEventListener battleEventListener) {
				battleEventListener.handlePostBattle(event);
			}
		},
		BATTLE_FINISHED {
			@Override
			void handle(BattleEvent event, BattleEventListener battleEventListener) {
				battleEventListener.handleBattleFinished(event);
			}
		};

		abstract void handle(BattleEvent event, BattleEventListener battleEventListener);
	}

	private final Type type;

	private final BattleContext battleContext;

	public BattleEvent(Type type, BattleContext battleContext) {
		this.type = type;
		this.battleContext = battleContext;
	}

	@Override
	public void notify(BattleEventListener listener) {
		this.type.handle(this, listener);
	}

	public BattleContext getBattleContext() {
		return this.battleContext;
	}
}
