/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
'use strict';

import {AnimationContext} from './animationContextState';
import {AnimationEvent, AnimationEventType} from './animationEvents';
import TokenAnimation from './tokenAnimation';
import TimelineAnimation from "./timelineAnimation";
import SpeedControl from "./speedControl";
import ProgressAnimation from "./progressAnimation";
import ClockAnimation from "./clockAnimation";
import PlayActionControl from "./playActionControl";

/**
 * LogAnimation is a Front Controller to manage the animation page.
 * It coordinates other components, including token animation, timeline, clock, log info table, progress indicator.
 * Each component is represented by a Controller.
 * The View and Controller of MVC are bundled as Controller. View is accessed via jQuery.
 * The Model of MVC is not explicitly managed as the data is read-only. Data include JSON and XML read from the server.
 * As the business gets more complex, a more complete MVC model would be considered.
 * Caller of LogAnimation must execute in steps: loadProcessModel, initAnimationComponents, and then start().
 *
 * @author Bruce Nguyen
 */
export default class LogAnimation {
    /**
     * LogAnimation is fed a processMapController which contains a process map/model
     * This map/model can have different implementations, e.g. bpmn.io or cytoscape.js
     * @param {String} pluginExecutionId: ID of the running log animation instance, used for communicating with the server.
     * @param {Object} processMapController: the process map/model controller
     * @param {String} tokenAnimationContainerId: id of the container of token animation
     * @param {String} timelineContainerId: id of the container of timeline panel
     * @param {String} speedControlContainerId: id of the container of speed control panel
     * @param {String} progressContainerId: id of the container of the progress panel
     * @param {String} logInfoContainerId: id of the container of the log info panel
     * @param {String} clockContainerId: id of the container of the clock
     * @param {String} buttonsContainerId: id of the container of the control buttons panel
     * @param {String} playClassName: CSS class name of the play state
     * @param {String} pauseClassName: CSS class name of the pause state
     */
    constructor(pluginExecutionId,
                processMapController,
                tokenAnimationContainerId,
                timelineContainerId,
                speedControlContainerId,
                progressContainerId,
                logInfoContainerId,
                clockContainerId,
                buttonsContainerId,
                playClassName,
                pauseClassName) {
        this.pluginExecutionId = pluginExecutionId;
        this.isPlayingBeforeMovingModel = false;
        this.colorPalette = [
            ['#84c7e3', '#76b3cc', '#699fb5', '#5c8b9e', '#4f7788', '#426371', '#344f5a', '#273b44'], //ligh blue
            ['#bb3a50', '#a83448', '#952e40', '#822838', '#702230', '#5d1d28', '#4a1720', '#381118'], //red
            ['#ffe066', '#ffdb4d', '#ffd633', '#ffd11a', '#ffcc00', '#e6b800', '#cca300', '#b38f00'], //orange
            ['#66ff66', '#4dff4d', '#33ff33', '#1aff1a', '#00ff00', '#00e600', '#00cc00', '#00b300'], //green
            ['#bf80ff', '#b366ff', '#a64dff', '#9933ff', '#8c1aff', '#8000ff', '#7300e6', '#6600cc'], //green
            ['#6666ff', '#4d4dff', '#3333ff', '#1a1aff', '#0000ff', '#0000e6', '#0000cc', '#0000b3'], //dark blue
            ['#ff80ff', '#ff66ff', '#ff4dff', '#ff33ff', '#ff1aff', '#ff00ff', '#e600e6', '#cc00cc'] //pink
        ];
        this.processMapController = processMapController;
        this.tokenAnimationContainerId = tokenAnimationContainerId;
        this.timelineContainerId = timelineContainerId;
        this.speedControlContainerId = speedControlContainerId;
        this.progressContainerId = progressContainerId;
        this.logInfoContainerId = logInfoContainerId;
        this.clockContainerId = clockContainerId;
        this.buttonsContainerId = buttonsContainerId;
        this.playClassName = playClassName;
        this.pauseClassName = pauseClassName;
    }

    /**
     * Caller of LogAnimation must make sure that it has successfuly loaded a process model before initializing it.
     * A log animation cannot work without a process model.
     * @param {String} setupDataJSON: log animation setup data
     */
    initialize(setupDataJSON) {
        if (!this.processMapController) {
            console.error('Stop. The process model has not been loaded yet!');
            return;
        }
        let setupData = JSON.parse(setupDataJSON);
        this.logSummaries = setupData.logs;
        let {recordingFrameRate, logStartFrameIndexes, logEndFrameIndexes, elementIndexIDMap, caseCountsByFrames} = setupData;
        let startMs = new Date(setupData.timeline.startDateLabel).getTime(); // Start date in milliseconds
        let endMs = new Date(setupData.timeline.endDateLabel).getTime(); // End date in milliseconds
        let totalEngineS = setupData.timeline.totalEngineSeconds;
        let slotEngineUnit = setupData.timeline.slotEngineUnit;
        let timelineSlots = setupData.timeline.timelineSlots;

        this.animationContext = new AnimationContext(this.pluginExecutionId, startMs, endMs, timelineSlots,
            totalEngineS, slotEngineUnit, recordingFrameRate);

        this.processMapController.initialize(elementIndexIDMap);
        this.tokenAnimation = new TokenAnimation(this, this.tokenAnimationContainerId, this.processMapController, this.colorPalette);
        this.timeline = new TimelineAnimation(this, this.timelineContainerId, caseCountsByFrames);
        this.speedControl = new SpeedControl(this, this.speedControlContainerId);
        if (this.progressContainerId) {
            this.progress = new ProgressAnimation(this, logStartFrameIndexes, logEndFrameIndexes, this.progressContainerId, this.logInfoContainerId);
        }
        this.clock = new ClockAnimation(this, this.clockContainerId);
        this.buttonControls = new PlayActionControl(this, this.buttonsContainerId, this.playClassName, this.pauseClassName);

        // Register events
        this.processMapController.registerListener(this);
        this.tokenAnimation.registerListener(this);
        this.timeline.registerListener(this);
        this._setKeyboardEvents();

        this.clock.setClockTime(this.animationContext.getLogStartTime());
        this.tokenAnimation.startEngine();
        this.pause();
    }

    /**
     * @returns {TokenAnimation}
     */
    getTokenAnimation() {
        return this.tokenAnimation;
    }

    /**
     * In case of using a different type of TokenAnimation
     * @param {TokenAnimation} newTokenAnimation
     */
    setTokenAnimation(newTokenAnimation) {
        this.tokenAnimation = newTokenAnimation;
    }

    _setKeyboardEvents() {
        let me = this;
        window.onkeydown = function (event) {
            if (event.keyCode === 32 || event.key === " ") {
                event.preventDefault();
                me.playPause();
            }
        };
    }

    /**
     * @returns {AnimationContext}
     */
    getAnimationContext() {
        return this.animationContext;
    }

    getNumberOfLogs() {
        return this.logSummaries.length;
    }

    getLogSummaries() {
        return this.logSummaries;
    }

    getLogColor(logNo) {
        return this.colorPalette[logNo][0];
    }

    // Seconds
    getLogicalTimeFromFrameIndex(frameIndex) {
        return frameIndex/this.animationContext.getRecordingFrameRate();
    }

    getLogTimeFromFrameIndex(frameIndex) {
        return this.getLogTimeFromFromLogicalTime(this.getLogicalTimeFromFrameIndex(frameIndex));
    }

    // Milliseconds
    getLogTimeFromFromLogicalTime(logicalTime) {
        return this.animationContext.getLogStartTime() + logicalTime*this.animationContext.getTimelineRatio()*1000;
    }

    getCurrentLogicalTime() {
        return this.getLogicalTimeFromFrameIndex(this.tokenAnimation.getCurrentFrameIndex());
    }

    /**
     * SVG animation controls speed on a fixed path via time duration and determines the current position via
     * the current engine time. However, TokenAnimation (canvas based) controls speed via frame rates.
     * The time system in TokenAnimation and SVG animations are different because of different frame rates
     * (we don't know what happens inside the SVG animation engine). However, we can use the current logical time
     * as the shared information to synchronize them.
     * @param {Number} frameRate: frames per second
     */
    setSpeedLevel(frameRate) {
        let newSpeedLevel = frameRate / this.animationContext.getRecordingFrameRate();
        console.log('AnimationController - changeSpeed: speedLevel = ' + newSpeedLevel);
        this.tokenAnimation.setPlayingFrameRate(frameRate);
    }

    // Move forward 1 slot
    fastForward() {
        console.log('AnimationController - fastForward');
        if (this.isAtEnd()) return;
        let newLogicalTime = this.getCurrentLogicalTime() + this.animationContext.getLogicalSlotTime();
        this.goto(newLogicalTime);
    }

    // Move backward 1 slot
    fastBackward() {
        console.log('AnimationController - fastBackward');
        if (this.isAtStart()) return;
        let newLogicalTime = this.getCurrentLogicalTime() - this.animationContext.getLogicalSlotTime();
        this.goto(newLogicalTime);
    }

    gotoStart() {
        console.log('AnimationController - gotoStart');
        if (this.isAtStart()) return;
        this.goto(0);
        this.timeline.updateCursor(0);
        this.pause();
    }

    gotoEnd() {
        console.log('AnimationController - gotoEnd');
        if (this.isAtEnd()) return;
        this.goto(this.animationContext.getLogicalTimelineMax());
        this.timeline.updateCursor(this.animationContext.getTotalNumberOfFrames()-1);
        this.pause();
    }

    isAtStart() {
        return this.tokenAnimation.isAtStartFrame();
    }

    isAtEnd() {
        return this.tokenAnimation.isAtEndFrame();
    }

    isPlaying() {
        return !this.tokenAnimation.isPausing();
    }

    /**
     *
     * @param {Number} logicalTime: the time when speed level = 1.
     */
    goto(logicalTime) {
        let newLogicalTime = logicalTime;
        if (newLogicalTime < 0) {
            newLogicalTime = 0;
        }
        if (newLogicalTime > this.animationContext.getLogicalTimelineMax()) {
            newLogicalTime = this.animationContext.getLogicalTimelineMax();
        }
        this.tokenAnimation.doGoto(newLogicalTime);
        //this.clock.setClockTime(this.getLogTimeFromFromLogicalTime(newLogicalTime));
    }

    pause() {
        console.log('AnimationController: pause');
        this.tokenAnimation.doPause();
        this.buttonControls.setPlayPauseButton(true);
    }

    unPause() {
        console.log('AnimationController: unPause');
        this.tokenAnimation.doUnpause();
        this.buttonControls.setPlayPauseButton(false);
    }

    /**
     * Toggle between play and pause.
     */
    playPause() {
        if (this.isAtEnd()) return;
        console.log('AnimationController: toggle play/pause');
        if (this.isPlaying()) {
            this.pause();
        } else {
            this.unPause();
        }
    }

    freezeControls() {
        this.timeline.freezeControls();
        this.buttonControls.freezeControls();
        this.speedControl.freezeControls();
    }

    unFreezeControls() {
        this.timeline.unFreezeControls();
        this.buttonControls.unFreezeControls();
        this.speedControl.unFreezeControls();
    }

    /**
     * The main token animation drives the changes on the main UI via event communication
     * @param {AnimationEventType} event
     * @param {Object} eventData
     */
    handleEvent(event, eventData) {
        if (!(event instanceof AnimationEvent)) return;

        if (event.getEventType() === AnimationEventType.FRAMES_NOT_AVAILABLE) {
            this.freezeControls();
        }
        else if (event.getEventType() === AnimationEventType.FRAMES_AVAILABLE) {
            let frameIndex = event.getEventData().frameIndex;
            this.timeline.updateCursor(frameIndex);
            if (this.progress) this.progress.updateProgress(frameIndex);
            this.clock.setClockTime(this.getLogTimeFromFrameIndex(frameIndex));
            this.unFreezeControls();
        }
        else if (event.getEventType() === AnimationEventType.END_OF_ANIMATION) {
            this.pause();
            this.clock.setClockTime(this.animationContext.getLogEndTime()); // fix small discrepancy in timing
        }
        else if (event.getEventType() === AnimationEventType.MODEL_CANVAS_MOVING) {
            let modelBox = event.getEventData().viewbox;
            let modelMatrix = event.getEventData().transformMatrix;
            this.tokenAnimation.setPosition(modelBox.x, modelBox.y, modelBox.width, modelBox.height, modelMatrix);
            if (this.isPlaying()) {
                this.pause();
                this.isPlayingBeforeMovingModel = true;
            }
        }
        else if (event.getEventType() === AnimationEventType.MODEL_CANVAS_MOVED) {
            let modelBox = event.getEventData().viewbox;
            let modelMatrix = event.getEventData().transformMatrix;
            this.tokenAnimation.setPosition(modelBox.x, modelBox.y, modelBox.width, modelBox.height, modelMatrix);
            if (this.isPlayingBeforeMovingModel) {
                this.unPause();
                this.isPlayingBeforeMovingModel = false;
            }
        }
        else if (event.getEventType() === AnimationEventType.TIMELINE_CURSOR_MOVING) {
            let cursorLogicalTime = event.getEventData().logicalTime;
            this.clock.setClockTime(this.getLogTimeFromFromLogicalTime(cursorLogicalTime));
        }
    }

}