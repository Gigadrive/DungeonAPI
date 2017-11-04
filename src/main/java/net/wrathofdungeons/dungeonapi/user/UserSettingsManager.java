package net.wrathofdungeons.dungeonapi.user;

public class UserSettingsManager {
    private boolean friendRequests;
    private boolean privateMessages;
    private boolean partyRequests;
    private boolean guildRequests;
    private boolean duelRequests;
    private boolean tradeRequests;

    public void setFriendRequests(boolean friendRequests) {
        this.friendRequests = friendRequests;
    }

    public boolean allowsFriendRequests() {
        return friendRequests;
    }

    public void setPrivateMessages(boolean privateMessages) {
        this.privateMessages = privateMessages;
    }

    public boolean allowsPrivateMessages() {
        return privateMessages;
    }

    public void setPartyRequests(boolean partyRequests) {
        this.partyRequests = partyRequests;
    }

    public boolean allowsPartyRequests() {
        return partyRequests;
    }

    public void setGuildRequests(boolean guildRequests) {
        this.guildRequests = guildRequests;
    }

    public boolean allowsGuildRequests() {
        return guildRequests;
    }

    public void setDuelRequests(boolean duelRequests) {
        this.duelRequests = duelRequests;
    }

    public boolean allowsDuelRequests() {
        return duelRequests;
    }

    public void setTradeRequests(boolean tradeRequests) {
        this.tradeRequests = tradeRequests;
    }

    public boolean allowsTradeRequests() {
        return tradeRequests;
    }
}
