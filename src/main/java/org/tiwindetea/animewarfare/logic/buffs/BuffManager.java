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

package org.tiwindetea.animewarfare.logic.buffs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuffManager {
	private final List<Buff> nonActionBuffs = new ArrayList<>();
	private final List<Buff> actionBuffs = new ArrayList<>();

	public void updateNonActionBuffTurn() {
		updateTurn(this.nonActionBuffs);
	}

	public void updateActionBuffTurn() {
		updateTurn(this.actionBuffs);
	}

	public boolean addActionBuff(Buff actionBuff) {
		return this.actionBuffs.add(actionBuff);
	}

	public boolean addNonActionBuffBuff(Buff buff) {
		return this.nonActionBuffs.add(buff);
	}

	private static void updateTurn(List<Buff> buffList) {
		List<Buff> buffToRemove = buffList.stream().filter(b -> {
			--b.remainingTurns;
			return b.remainingTurns == 0;
		}).collect(Collectors.toList());

		buffToRemove.forEach(buff -> buff.destroy());

		buffList.removeAll(buffToRemove);
	}
}
