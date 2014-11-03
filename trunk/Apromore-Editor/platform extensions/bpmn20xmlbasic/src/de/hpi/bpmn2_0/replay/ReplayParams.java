package de.hpi.bpmn2_0.replay;

public class ReplayParams {
    
    private double maxCost;
    private int maxDepth;
    private double minHits;
    private double maxHits;
    private double minFitness;
    private int maxDiffSeries;
    private double ActivitySkippedCost;
    private double EventSkippedCost;
    private double traceChunkSize; //maximum length of trace to be searched one at a time
    private double ActivitySkipPercent;
    private double MaxNodeDistance;
    private int TimelineSlots;
    private int TotalEngineSeconds;

    public int getTotalEngineSeconds() {
        return TotalEngineSeconds;
    }

    public void setTotalEngineSeconds(int TotalEngineSeconds) {
        this.TotalEngineSeconds = TotalEngineSeconds;
    }


    public int getTimelineSlots() {
        return TimelineSlots;
    }

    public void setTimelineSlots(int TimelineSlots) {
        this.TimelineSlots = TimelineSlots;
    }


    public double getMaxNodeDistance() {
        return MaxNodeDistance;
    }

    public void setMaxNodeDistance(double MaxNodeDistance) {
        this.MaxNodeDistance = MaxNodeDistance;
    }


    public double getMaxActivitySkip() {
        return ActivitySkipPercent;
    }

    public void setMaxActivitySkip(double ActivitySkipPercent) {
        this.ActivitySkipPercent = ActivitySkipPercent;
    }


    public double getTraceChunkSize() {
        return traceChunkSize;
    }

    public void setTraceChunkSize(int traceChunkSize) {
        this.traceChunkSize = traceChunkSize;
    }


    public double getEventSkipCost() {
        return EventSkippedCost;
    }

    public void setEventSkipCost(double EventSkippedCost) {
        this.EventSkippedCost = EventSkippedCost;
    }


    public double getActivitySkipCost() {
        return ActivitySkippedCost;
    }

    public void setActivitySkipCost(double ActivitySkippedCost) {
        this.ActivitySkippedCost = ActivitySkippedCost;
    }


    public int getMaxDiffSeries() {
        return maxDiffSeries;
    }

    public void setMaxDiffSeries(int maxDiffSeries) {
        this.maxDiffSeries = maxDiffSeries;
    }


    public double getMinFitness() {
        return minFitness;
    }

    public void setMinFitness(double minFitness) {
        this.minFitness = minFitness;
    }


    public double getMaxMatch() {
        return maxHits;
    }

    public void setMaxMatch(double maxHits) {
        this.maxHits = maxHits;
    }


    public double getMinMatch() {
        return minHits;
    }

    public void setMinMatch(double minHits) {
        this.minHits = minHits;
    }


    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

}