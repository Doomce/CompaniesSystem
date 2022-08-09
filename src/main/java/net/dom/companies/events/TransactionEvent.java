package net.dom.companies.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TransactionEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final String SenderIBAN;
    private final String ReceiverIBAN;
    private double Amount;
    private String Description;
    private boolean isCancelled;

    private static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public TransactionEvent(String Sender, String Receiver2, double amount, String Desc) {
        this.SenderIBAN = Sender;
        this.ReceiverIBAN = Receiver2;
        this.Amount = amount;
        this.Description = Desc;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getSenderIBAN() {
        return this.SenderIBAN;
    }

    public String getReceiverIBAN() {
        return this.ReceiverIBAN;
    }

    public double getAmount() {
        return this.Amount;
    }

    public String getDescription() {
        return this.Description;
    }
}

