/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.portal.loganimation.frame;

public class AnimationSetting {
    private int fps = 24; //frames per second
    private int frameGap = 41; //milliseconds
    private long startTimestamp;
    private int chunkSize;
    
    public int getFPS() {
        return this.fps;
    }
    
    public void setFPS(int fps) {
        this.fps = fps;
    }
    
    public int getFrameGap() {
        return this.frameGap;
    }
    
    public void setFrameGap(int frameGap) {
        this.frameGap = frameGap;
    }
    
    public long getStartTimestamp() {
        return this.startTimestamp;
    }
    
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
    
    public int getChunkSize() {
        return this.chunkSize;
    }
    
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
