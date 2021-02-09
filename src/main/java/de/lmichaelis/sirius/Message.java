package de.lmichaelis.sirius;

import net.minecraft.text.Text;

public class Message {
    //FIXME refactor originalText into just a link to 1 central place to store all original message text,
    // so redundant copies of it are not made (originalText should not be changed when used)
    public Text originalText; //TODO what Text type should this be?
    public Text filteredText; //TODO what Text type should this be?
    public long unixTimestamp; // signed int would overflow in 2038
    public boolean isHidden; // should be set to true if filteredText is empty, or if a filter blocks this message from being displayed

    public Message(Text initialText, int unixTimestamp){
        new Message(initialText, initialText, unixTimestamp, false);
    }

    private Message(Text originalText, Text filteredText, long unixTimestamp, boolean isHidden){
        this.originalText = originalText;
        this.filteredText = filteredText;
        this.unixTimestamp = unixTimestamp;
        this.isHidden = isHidden;
    }

    public Message copy(){
        return new Message(originalText, filteredText, unixTimestamp, isHidden);
    }
}
