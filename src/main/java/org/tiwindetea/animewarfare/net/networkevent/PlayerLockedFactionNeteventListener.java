package org.tiwindetea.animewarfare.net.networkevent;

import java.util.EventListener;

/**
 * @author Lucas Lazare
 * @since 0.1.0
 */
public interface PlayerLockedFactionNeteventListener extends EventListener {

    void handleFactionLock(PlayerLockedFactionNetevent playerLockedFactionNetevent);
}
