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

package com.unocode.earthlingrun.actors.menu;

import com.badlogic.gdx.math.Rectangle;
import com.unocode.earthlingrun.enums.GameState;
import com.unocode.earthlingrun.utils.Constants;
import com.unocode.earthlingrun.utils.GameManager;

public class PauseButton extends GameButton {

    public interface PauseButtonListener {
        public void onPause();

        public void onResume();
    }

    private PauseButtonListener listener;

    public PauseButton(Rectangle bounds, PauseButtonListener listener) {
        super(bounds);
        this.listener = listener;
    }

    @Override
    protected String getRegionName() {
        return com.unocode.earthlingrun.utils.GameManager.getInstance().getGameState() == com.unocode.earthlingrun.enums.GameState.PAUSED ? com.unocode.earthlingrun.utils.Constants.PLAY_REGION_NAME : Constants.PAUSE_REGION_NAME;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (com.unocode.earthlingrun.utils.GameManager.getInstance().getGameState() == com.unocode.earthlingrun.enums.GameState.OVER) {
            remove();
        }
    }

    @Override
    public void touched() {
        if (GameManager.getInstance().getGameState() == GameState.PAUSED) {
            listener.onResume();
        } else {
            listener.onPause();
        }
    }

}
