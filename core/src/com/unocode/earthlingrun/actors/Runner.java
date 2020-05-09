/*
 * Copyright (c) 2014. William Mora
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unocode.earthlingrun.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.unocode.earthlingrun.box2d.RunnerUserData;
import com.unocode.earthlingrun.enums.Difficulty;
import com.unocode.earthlingrun.enums.GameState;
import com.unocode.earthlingrun.utils.AssetsManager;
import com.unocode.earthlingrun.utils.AudioUtils;
import com.unocode.earthlingrun.utils.Constants;
import com.unocode.earthlingrun.utils.GameManager;

public class Runner extends GameActor {

    private boolean dodging;
    private boolean jumping;
    private boolean doubleJumping;
    private boolean powerStomping;
    private int jumpNum = 0;
    private boolean hit;
    private Animation runningAnimation;
    private TextureRegion jumpingTexture;
    private TextureRegion dodgingTexture;
    private TextureRegion hitTexture;
    private float stateTime;

    private Sound jumpSound;
    private Sound hitSound;

    private int jumpCount, doubleJumpCount, powerStompCount;

    public Runner(Body body) {
        super(body);
        jumpCount = 0;
        doubleJumpCount = 0;
        powerStompCount = 0;

        runningAnimation = com.unocode.earthlingrun.utils.AssetsManager.getAnimation(com.unocode.earthlingrun.utils.Constants.RUNNER_RUNNING_ASSETS_ID);
        stateTime = 0f;
        jumpingTexture = com.unocode.earthlingrun.utils.AssetsManager.getTextureRegion(com.unocode.earthlingrun.utils.Constants.RUNNER_JUMPING_ASSETS_ID);
        dodgingTexture = com.unocode.earthlingrun.utils.AssetsManager.getTextureRegion(com.unocode.earthlingrun.utils.Constants.RUNNER_DODGING_ASSETS_ID);
        hitTexture = AssetsManager.getTextureRegion(Constants.RUNNER_HIT_ASSETS_ID);
        jumpSound = com.unocode.earthlingrun.utils.AudioUtils.getInstance().getJumpSound();
        hitSound = com.unocode.earthlingrun.utils.AudioUtils.getInstance().getHitSound();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float x = screenRectangle.x - (screenRectangle.width * 0.1f) +15;//chance add =15 to offest runner to right a bit
        float y = screenRectangle.y;
        float width = screenRectangle.width * 1.2f;

        if (dodging) {
            batch.draw(dodgingTexture, x, y + screenRectangle.height / 4, width, screenRectangle.height * 3 / 4);
        } else if (hit) {
            // When he's hit we also want to apply rotation if the body has been rotated
            batch.draw(hitTexture, x, y, width * 0.5f, screenRectangle.height * 0.5f, width, screenRectangle.height, 1f,
                    1f, (float) Math.toDegrees(body.getAngle()));
        } else if (jumping) {
            batch.draw(jumpingTexture, x, y, width, screenRectangle.height);
        } else {
            // Running
            if (GameManager.getInstance().getGameState() == GameState.RUNNING) {
                stateTime += Gdx.graphics.getDeltaTime();
            }

            TextureRegion tr = (TextureRegion) runningAnimation.getKeyFrame(stateTime, true);
            batch.draw(tr, x, y, width, screenRectangle.height);
        }
    }

    @Override
    public RunnerUserData getUserData() {
        return (RunnerUserData) userData;
    }

    public void jump() {

        //if (!(jumping || dodging || hit)) {
        if(!(jumpNum > 1 || dodging || hit)) {//chance for doublejump--later check for upgrade
            body.applyLinearImpulse(getUserData().getJumpingLinearImpulse(), body.getWorldCenter(), true);
            jumping = true;
            com.unocode.earthlingrun.utils.AudioUtils.getInstance().playSound(jumpSound);
            jumpCount++;
            if(jumpNum == 1){
                doubleJumpCount++;
                doubleJumping = true;
                System.out.println("doubleJumping");
            }
            jumpNum++;
        }
    }

    public void landed() {
        jumpNum = 0;
        jumping = false;
        doubleJumping = false;
        powerStomping = false;
        System.out.println("landed");
    }

    public void dodge() {
        //if (!(jumping || hit)) {
        if ((!jumping || jumpNum > 1) && !hit) {//check if not jumping or on 2nd jump
            body.setTransform(getUserData().getDodgePosition(), getUserData().getDodgeAngle());
            dodging = true;
            if(jumpNum > 1){
                powerStompCount++;
                powerStomping = true;
                System.out.println("powerStomping");
            }
        }
    }

    public void stopDodge() {
        dodging = false;
        // If the runner is hit don't force him back to the running position
        if (!hit) {
            body.setTransform(getUserData().getRunningPosition(), 0f);
        }
    }

    public boolean isDodging() {
        return dodging;
    }

    public boolean isDoubleJumping(){ return doubleJumping;}

    public boolean isPowerStomping() { return powerStomping;}

    public void hit() {
        body.applyAngularImpulse(getUserData().getHitAngularImpulse(), true);
        hit = true;
        AudioUtils.getInstance().playSound(hitSound);
    }

    public boolean isHit() {
        return hit;
    }

    public void onDifficultyChange(Difficulty newDifficulty) {
        setGravityScale(newDifficulty.getRunnerGravityScale());
        getUserData().setJumpingLinearImpulse(newDifficulty.getRunnerJumpingLinearImpulse());
    }

    public void setGravityScale(float gravityScale) {
        body.setGravityScale(gravityScale);
        body.resetMassData();
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public int getDoubleJumpCount() { return doubleJumpCount;}

    public int getPowerStompCount() { return powerStompCount;}
}
