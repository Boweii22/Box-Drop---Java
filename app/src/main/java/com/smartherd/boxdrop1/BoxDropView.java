package com.smartherd.boxdrop1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


public class BoxDropView extends View {

    private final List<Body> boxes = new ArrayList<>();
    private final Paint paint = new Paint();
    private World world;
    private final float boxSize = 100f; //this is for the size of the boxes
    private final int velocityIterations = 6;
    private final int positionIterations = 2;
    private Body groundBody;

    public BoxDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize Box2D world with gravity
        world = new World(new Vec2(0f, 10f));  // Gravity pulling down
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Step the physics world
        // 60 frames per second
        float timeStep = 1.0f / 60.0f;
        world.step(timeStep, velocityIterations, positionIterations);

        // Draw all boxes
        for (Body box : boxes) {
            Vec2 position = box.getPosition();
            paint.setColor((Integer) box.getUserData());
            canvas.drawRect(
                    position.x * boxSize - boxSize / 2,
                    position.y * boxSize - boxSize / 2,
                    position.x * boxSize + boxSize / 2,
                    position.y * boxSize + boxSize / 2,
                    paint
            );
        }

        // Redraw at 60 FPS
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Add a new box at the touch location
            Body newBox = createBox(event.getX() / boxSize, 0f);  // Add box at the top of the screen
            boxes.add(newBox);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Body createBox(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);  // Box size

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        body.createFixture(fixtureDef);

        // Assign a random color to the box
        body.setUserData(randomColor());

        return body;
    }

    private int randomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createGround(w, h);
    }

    private void createGround(int width, int height) {
        if (groundBody != null) {
            world.destroyBody(groundBody);  // Destroy previous ground, if any
        }

        BodyDef groundBodyDef = new BodyDef();
        // Position the ground at the bottom of the screen
        groundBodyDef.position.set(width / 2f / boxSize, height / boxSize);
        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        // Set the ground to be as wide as the screen
        groundShape.setAsBox(width / boxSize, 0.5f);

        groundBody.createFixture(groundShape, 0.0f);  // Static body, so no density
    }
    public void resetBoxes() {
        for(Body box: boxes){
            world.destroyBody(box);
        }
        boxes.clear();
        invalidate();   //this is to redraw the view...
    }
}


