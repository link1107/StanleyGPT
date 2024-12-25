package com.igorlink.stanleygpt.client.eventhandlers;

import com.igorlink.stanleygpt.client.eventhandlers.handlers.*;

/**
 * The manager of event handlers.
 */
public class EventHandlerManager {

    /**
     * Creates a new event handler manager and initializes all event handlers.
     */
    public EventHandlerManager() {
        new PlayerDeathEventHandler();
        new PlayerKilledEntityEventHandler();
        new DropFromBlockEventHandler();
        new PlayerCraftedEventHandler();
        new PlayerMovedToAnotherWorldEventHandler();
        new PlayerTakeItemFromFurnaceEventHandler();
        new PlayerTamedEntityEventHandler();
        new TamedEntityKilledLivingEntityEventHandler();
        new ExplosionHappenedEventHandler();
        new PlayerDamagedEventHandler();
        new PlayerEatEventHandler();
        new PlayerFeedEventHandler();
        new PlayerHookedItemWithFishingBobberEventHandler();
        new PlayerBreedEntityEventHandler();
        new TamedEntityKilledEventHandler();
    }

}
