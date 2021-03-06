package com.jja.ld34.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.jja.ld34.Ld34Game;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        return new GwtApplicationConfiguration(Ld34Game.GAME_WIDTH, Ld34Game.GAME_HEIGHT);
    }

    @Override
    public ApplicationListener getApplicationListener () {
        return new Ld34Game();
    }
}