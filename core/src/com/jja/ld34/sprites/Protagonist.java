package com.jja.ld34.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.jja.ld34.Ld34Game;
import com.jja.ld34.screens.PlayScreen;

import java.util.HashMap;

public class Protagonist extends Entity implements ContactListener {

    private static final float BASE_SIZE = 32f;
    private static final float BASE_MOVEMENT_SPEED = 2f;

    public enum State {
        IDLING,
        MOVING
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private Body body;

    private Direction currentDirection;
    private Direction previousDirection;
    private State currentState;
    private State previousState;
    private HashMap<Direction, TextureRegion> idlingTextureRegionMap;
    private HashMap<Direction, Animation> movingAnimationMap;
    private float animationTimer;

    public Protagonist(String uniqueName, World world, Vector2 initialPosition, TextureAtlas.AtlasRegion textureRegion) {
        super(uniqueName, world, initialPosition, BASE_SIZE, Ld34Game.PROTAGONIST_BIT, Ld34Game.ALL_FLAGS, textureRegion);

        this.currentDirection = this.previousDirection = Direction.DOWN;
        this.currentState = this.previousState = State.IDLING;
        this.animationTimer = 0;

        // setup idling texture regions
        this.idlingTextureRegionMap = new HashMap<Direction, TextureRegion>(4);
        this.idlingTextureRegionMap.put(Direction.DOWN, PlayScreen.generateTextureRegion(getTexture(), 0));
        this.idlingTextureRegionMap.put(Direction.LEFT, PlayScreen.generateTextureRegion(getTexture(), 4));
        this.idlingTextureRegionMap.put(Direction.RIGHT, PlayScreen.generateTextureRegion(getTexture(), 7));
        this.idlingTextureRegionMap.put(Direction.UP, PlayScreen.generateTextureRegion(getTexture(), 10));

        // setup animations
        this.movingAnimationMap = new HashMap<Direction, Animation>(4);
        Array<TextureRegion> frames;
        frames = new Array<TextureRegion>();
        for (int i = 0; i <= 2; i++) {
            frames.add(PlayScreen.generateTextureRegion(getTexture(), i));
        }
        this.movingAnimationMap.put(Direction.DOWN, new Animation(getAnimationFramerate(), frames));
        frames = new Array<TextureRegion>();
        for (int i = 3; i <= 5; i++) {
            frames.add(PlayScreen.generateTextureRegion(getTexture(), i));
        }
        this.movingAnimationMap.put(Direction.LEFT, new Animation(getAnimationFramerate(), frames));
        frames = new Array<TextureRegion>();
        for (int i = 6; i <= 8; i++) {
            frames.add(PlayScreen.generateTextureRegion(getTexture(), i));
        }
        this.movingAnimationMap.put(Direction.RIGHT, new Animation(getAnimationFramerate(), frames));
        frames = new Array<TextureRegion>();
        for (int i = 9; i <= 11; i++) {
            frames.add(PlayScreen.generateTextureRegion(getTexture(), i));
        }
        this.movingAnimationMap.put(Direction.UP, new Animation(getAnimationFramerate(), frames));

        setRegion(this.idlingTextureRegionMap.get(this.currentDirection));

        world.setContactListener(this);
    }

    public int getUpKey() {
        return Input.Keys.UP;
    }
    public int getDownKey() {
        return Input.Keys.DOWN;
    }
    public int getLeftKey() {
        return Input.Keys.LEFT;
    }
    public int getRightKey() {
        return Input.Keys.RIGHT;
    }

    public float getSize() {
        return BASE_SIZE;
    }

    public float getMovementSpeed() {
        return BASE_MOVEMENT_SPEED;
    }
    public float getAnimationFramerate() {
        return 1 / 15f;
    }

    public float getFriction(boolean inXDir) {
        if (inXDir) {
            return -this.body.getLinearVelocity().x;
        } else {
            return -this.body.getLinearVelocity().y;
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(getUpKey())) {
            this.body.applyLinearImpulse(new Vector2(getFriction(true), getMovementSpeed()), this.body.getWorldCenter(), true);

            this.currentDirection = Direction.UP;
            this.currentState = State.MOVING;
            this.previousDirection = this.currentDirection;
            this.previousState = this.currentState;
        } else if (Gdx.input.isKeyJustPressed(getDownKey())) {
            this.body.applyLinearImpulse(new Vector2(getFriction(true), -getMovementSpeed()), this.body.getWorldCenter(), true);

            this.currentDirection = Direction.DOWN;
            this.currentState = State.MOVING;
            this.previousDirection = this.currentDirection;
            this.previousState = this.currentState;
        } else if (Gdx.input.isKeyJustPressed(getLeftKey())) {
            this.body.applyLinearImpulse(new Vector2(-getMovementSpeed(), getFriction(false)), this.body.getWorldCenter(), true);

            this.currentDirection = Direction.LEFT;
            this.currentState = State.MOVING;
            this.previousDirection = this.currentDirection;
            this.previousState = this.currentState;
        } else if (Gdx.input.isKeyJustPressed(getRightKey())) {
            this.body.applyLinearImpulse(new Vector2(getMovementSpeed(), getFriction(false)), this.body.getWorldCenter(), true);

            this.currentDirection = Direction.RIGHT;
            this.currentState = State.MOVING;
            this.previousDirection = this.currentDirection;
            this.previousState = this.currentState;
        } else if (!Gdx.input.isKeyPressed(getUpKey()) && !Gdx.input.isKeyPressed(getDownKey()) && !Gdx.input.isKeyPressed(getLeftKey()) && !Gdx.input.isKeyPressed(getRightKey())) {
            this.body.applyLinearImpulse(new Vector2(getFriction(true), getFriction(false)), this.body.getWorldCenter(), true);

            this.currentState = State.IDLING;
            this.previousState = this.currentState;
        }
    }

    private TextureRegion getFrame(float delta) {
        if (this.previousState != this.currentState || this.previousDirection != this.currentDirection) {
            this.animationTimer = 0;
        } else {
            this.animationTimer += delta;
        }

        TextureRegion textureRegion;
        switch (this.currentState) {
            case MOVING:
                textureRegion = this.movingAnimationMap.get(this.currentDirection).getKeyFrame(this.animationTimer, true);
                break;
            default:
                textureRegion = this.idlingTextureRegionMap.get(this.currentDirection);
                break;
        }

        return textureRegion;
    }

    @Override
    public void onUpdate(float delta) {
        setPosition(this.body.getPosition().x - getWidth() / 2, this.body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
    }

    @Override
    public Body initializeBody(Vector2 initialPosition, short filterCategoryBit, short filterMaskBit) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((initialPosition.x / Ld34Game.PIXELS_PER_METER) + ((getSize() / 2) / Ld34Game.PIXELS_PER_METER), (initialPosition.y / Ld34Game.PIXELS_PER_METER) + ((getSize() / 2) / Ld34Game.PIXELS_PER_METER));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((getSize() / 2) / Ld34Game.PIXELS_PER_METER, (getSize() / 2) / Ld34Game.PIXELS_PER_METER);
        fixtureDef.filter.categoryBits = filterCategoryBit;
        fixtureDef.filter.maskBits = filterMaskBit;
        fixtureDef.shape = shape;
        this.body.createFixture(fixtureDef).setUserData(this.uniqueName);

        return body;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() == "protagonist" || fixtureB.getUserData() == "protagonist") {
            Fixture protagonistFixture = (fixtureA.getUserData() == "protagonist") ? fixtureA : fixtureB;
            Fixture objectFixture = (protagonistFixture == fixtureA) ? fixtureB : fixtureA;
            if (objectFixture.getUserData() instanceof InteractiveEntity) {
                ((InteractiveEntity) objectFixture.getUserData()).onCollision();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public Vector2 getPosition() {
        return this.body.getPosition();
    }
}
