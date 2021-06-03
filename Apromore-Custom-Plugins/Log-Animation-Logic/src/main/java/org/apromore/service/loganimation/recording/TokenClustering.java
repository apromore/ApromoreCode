/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.service.loganimation.recording;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;

public class TokenClustering {
    // Each element is a table for one log
    // In each table, 1st dimension is element index, 2nd dimension is cluster index, cell value is the number of tokens in the cluster
    private List<int[][]> tokenClusterTable;
    
    // Map from real element index to zero-based index
    private MutableIntIntMap elementIndexMap = IntIntMaps.mutable.empty();
    
    private final int numberOfLogs;
    
    private final int numberOfClusters;
    
    public TokenClustering(int numberOfLogs, IntSet elementIndexes, int numberOfClusters) {
        if (numberOfLogs <= 0 || elementIndexes == null || elementIndexes.isEmpty() || numberOfClusters <= 1) {
            throw new IllegalArgumentException("Invalid constructor parameters to create TokenClustering");
        }
        this.numberOfLogs = numberOfLogs;
        this.numberOfClusters = numberOfClusters;
        AtomicInteger inc = new AtomicInteger(0);
        elementIndexes.forEach(elementIndex -> elementIndexMap.put(elementIndex, inc.getAndIncrement()));
        tokenClusterTable = new ArrayList<>();
        for (int i = 0; i<numberOfLogs; i++) {
            tokenClusterTable.add(new int[elementIndexes.size()][numberOfClusters]);
        }
    }
    
    public void clear() {
        for (int[][] logTable : tokenClusterTable) {
            for (int elementIndex = 0; elementIndex < elementIndexMap.size(); elementIndex++) {
                for (int clusterIndex = 0; clusterIndex < numberOfClusters; clusterIndex++) {
                    logTable[elementIndex][clusterIndex] = 0;
                }
            }
        }
    }
    
    private boolean isValidLogIndex(int logIndex) {
        return logIndex >= 0 && logIndex < numberOfLogs;
    }
    
    private boolean isValidElementIndex(int elementIndex) {
        return elementIndexMap.containsKey(elementIndex);
    }
    
    private boolean isValidClusterIndex(int clusterIndex) {
        return clusterIndex >= 0 && clusterIndex < numberOfClusters;
    }
    
    public int[] getClusters() {
        return IntStream.range(0, numberOfClusters).toArray();
    }
    
    // The radius on the two sides of the cluster distance
    private double getClusterRadius() {
        return (double)1/(2*(numberOfClusters-1));
    }
    
    private double getClusterDiameter() {
        return (double)1/(numberOfClusters-1);
    }
    
    // Relative distance from the start of the element
    public double getClusterDistance(int clusterIndex) {
        return (double)clusterIndex/(numberOfClusters-1);
    }
    
    private int getClusterByDistance(double distance) {
        double d = distance - getClusterRadius();
        return d <=0 ? 0 : (int)(d/getClusterDiameter()) + 1;
    }
    
    // Increment the size of the cluster that contains the token with tokenDistance
    public void incrementClusterSizeByTokenDistance(int logIndex, int elementIndex, double tokenDistance) {
        if (!isValidLogIndex(logIndex) || !isValidElementIndex(elementIndex)) return ;
        int clusterIndex = getClusterByDistance(tokenDistance);
        if (clusterIndex >= 0) tokenClusterTable.get(logIndex)[elementIndexMap.get(elementIndex)][clusterIndex] += 1;
    }
    
    public int getClusterSize(int logIndex, int elementIndex, int clusterIndex) {
        if (!isValidLogIndex(logIndex) || !isValidElementIndex(elementIndex) || !isValidClusterIndex(clusterIndex)) return 0;
        return tokenClusterTable.get(logIndex)[elementIndexMap.get(elementIndex)][clusterIndex];
    }
}