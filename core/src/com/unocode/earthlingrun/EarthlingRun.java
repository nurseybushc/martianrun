/*
 * Copyright (c) 2015. William Mora
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

package com.unocode.earthlingrun;

import com.badlogic.gdx.Game;
import com.unocode.earthlingrun.screens.GameScreen;
import com.unocode.earthlingrun.utils.AssetsManager;
import com.unocode.earthlingrun.utils.AudioUtils;
import com.unocode.earthlingrun.utils.GameEventListener;
import com.unocode.earthlingrun.utils.GameManager;

public class EarthlingRun extends Game {

    public EarthlingRun(GameEventListener listener) {
        GameManager.getInstance().setGameEventListener(listener);
    }

    @Override
    public void create() {
        com.unocode.earthlingrun.utils.AssetsManager.loadAssets();
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        AudioUtils.dispose();
        AssetsManager.dispose();
    }
}