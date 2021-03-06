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

package org.tiwindetea.animewarfare.net.networkrequests.server;

import org.tiwindetea.animewarfare.logic.events.MarketingLadderUpdatedEvent;

/**
 * @author Lucas Lazare
 * @since 0.1.0
 */
public class NetMarketingLadderUpdated implements NetReceivable {

    private final int newPosition;
    private final int endPosition;
    private final int cost;

    /**
     * Default constructor, required by Kryo.net
     */
    public NetMarketingLadderUpdated() {
        this.newPosition = this.endPosition = this.cost = -1;
    }

    public NetMarketingLadderUpdated(MarketingLadderUpdatedEvent event) {
        this.newPosition = event.getNewPosition();
        this.endPosition = event.getEndPosition();
        this.cost = event.getCost();
    }

    public int getNewPosition() {
        return this.newPosition;
    }

    public int getEndPosition() {
        return this.endPosition;
    }

    public int getCost() {
        return this.cost;
    }
}
