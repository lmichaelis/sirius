package de.lmichaelis.sirius.filter;

import net.minecraft.text.Text;

public abstract class Notifier extends Filter{
    @Override
    public Text run(Text input) {
        if(isMatch(input)){
            runNotification();
        }
        return input;
    }

//    public abstract void runNotification();
    public void runNotification(){

    }
}
