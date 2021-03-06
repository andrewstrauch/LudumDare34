package com.jja.ld34.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jja.ld34.Ld34Game;
import com.jja.ld34.scenes.Hud;

//Object that Bernie has to collect to complete the exit to the next level.
public class ExitPart extends CollectibleEntity {

    public static final float SIZE = 32f;

    public ExitPart(World world, Vector2 initialPosition) {
        super(world, initialPosition, SIZE, new Texture("battery/battery.png"));
    }

    @Override
    public void onCollected() {
        //Increment to a count somewhere else keeping track of pieces gathered for that level,
        //and also update the display in the HUD to reflect the change.
        Hud.exitPartsCount ++;
    }

    @Override
    public Body initializeBody(Vector2 initialPosition, short filterCategoryBit, short filterMaskBit) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((initialPosition.x / Ld34Game.PIXELS_PER_METER) + ((SIZE / 2) / Ld34Game.PIXELS_PER_METER), (initialPosition.y / Ld34Game.PIXELS_PER_METER) + ((SIZE / 2) / Ld34Game.PIXELS_PER_METER));
        bodyDef.type = BodyDef.BodyType.StaticBody;
        this.body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((SIZE / 2) / Ld34Game.PIXELS_PER_METER, (SIZE / 2) / Ld34Game.PIXELS_PER_METER);
        fixtureDef.filter.categoryBits = filterCategoryBit;
        fixtureDef.filter.maskBits = filterMaskBit;
        fixtureDef.shape = shape;
        this.body.createFixture(fixtureDef).setUserData(this);

        return this.body;
    }
}
