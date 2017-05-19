/* file: Parameter.java */
/*******************************************************************************
* Copyright 2014-2017 Intel Corporation
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
*******************************************************************************/

/**
 * @ingroup decision_forest_classification_training
 */
/**
 * @brief Contains classes of the decision forest classification algorithm training
 */
package com.intel.daal.algorithms.decision_forest.classification.training;
import com.intel.daal.algorithms.decision_forest.VariableImportanceModeId;
import com.intel.daal.services.DaalContext;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__DECISION_FOREST__CLASSIFICATION__TRAINING__PARAMETER"></a>
 * @brief Base class for parameters of the decision forest classification training algorithm
 */
public class Parameter extends com.intel.daal.algorithms.classifier.Parameter {

    public Parameter(DaalContext context, long cParameter) {
        super(context, cParameter);
    }

    /**
     * Returns number of trees to be created by the decision forest classification training algorithm
     * @return Number of trees
     */
    public long getNTrees() {
        return cGetNTrees(this.cObject);
    }

    /**
     * Sets the number of trees to be created by the decision forest classification training algorithm
     * @param nTrees Number of trees
     */
    public void setNTrees(long nTrees) {
        cSetNTrees(this.cObject, nTrees);
    }

    /**
     * Returns fraction of observations used for a training of one tree, 0 to 1.
     * Default is 1 (sampling with replacement)
     * @return Fraction of observations
     */
    public double getObservationsPerTreeFraction() {
        return cGetObservationsPerTreeFraction(this.cObject);
    }

    /**
     * Sets fraction of observations used for a training of one tree, 0 to 1
     * @param value Fraction of observations
     */
    public void setObservationsPerTreeFraction(double value) {
        cSetObservationsPerTreeFraction(this.cObject, value);
    }

    /**
     * Returns number of features tried as possible splits by the decision forest classification training algorithm
     * If 0 then sqrt(p) is used, where p is the total number of features.
     * @return Number of features
     */
    public long getFeaturesPerNode() {
        return cGetFeaturesPerNode(this.cObject);
    }

    /**
     * Sets the number of features tried as possible splits by decision forest classification training algorithm.
     * If 0 then sqrt(p) is used, where p is the total number of features.
     * @param value Number of features
     */
    public void setFeaturesPerNode(long value) {
        cSetFeaturesPerNode(this.cObject, value);
    }

    /**
     * Returns maximal tree depth. Default is 0 (unlimited)
     * @return Maximal tree depth
     */
    public long getMaxTreeDepth() {
        return cGetMaxTreeDepth(this.cObject);
    }

    /**
     * Sets maximal tree depth. Default is 0 (unlimited)
     * @param value Maximal tree depth
     */
    public void setMaxTreeDepth(long value) {
        cSetMaxTreeDepth(this.cObject, value);
    }

    /**
     * Returns minimal number of samples per node. Default is 1
     * @return Minimal number of samples
     */
    public long getMinObservationsInLeafNode() {
        return cGetMinObservationsInLeafNode(this.cObject);
    }

    /**
     * Sets minimal number of samples per node. Default is 1
     * @param value Minimal number of samples
     */
    public void setMinObservationsInLeafNode(long value) {
        cSetMinObservationsInLeafNode(this.cObject, value);
    }

    /**
     * Returns the seed for the random numbers generator used by the algorithm
     * @return Seed for the seed for the random numbers generator used by the algorithm
     */
    public int getSeed() {
        return cGetSeed(this.cObject);
    }

    /**
     * Sets the seed for the random numbers generator used by the algorithm
     * @param seed   Seed for the random numbers generator used by the algorithm
     */
    public void setSeed(int seed) {
        cSetSeed(this.cObject, seed);
    }

    /**
     * Returns threshold value used as stopping criteria:
     * if the impurity value in the node is smaller than the threshold then the node is not split anymore
     * @return Impurity threshold
     */
    public double getImpurityThreshold() {
        return cGetImpurityThreshold(this.cObject);
    }

    /**
     * Sets threshold value used as stopping criteria
     * @param value Impurity threshold
     */
    public void setImpurityThreshold(double value) {
        cSetImpurityThreshold(this.cObject, value);
    }

    /**
     * Returns variable importance computation mode
     * @return Variable importance computation mode
     */
    public VariableImportanceModeId getVariableImportanceMode() {
        return new VariableImportanceModeId(cGetVariableImportanceMode(this.cObject));
    }

    /**
     * Sets the variable importance computation mode
     * @param value Variable importance computation mode
     */
    public void setVariableImportanceMode(VariableImportanceModeId value) {
        cSetVariableImportanceMode(this.cObject, value.getValue());
    }

    /**
     * Sets the 64 bit integer flag that indicates the results to compute
     * @param resultsToCompute The 64 bit integer flag that indicates the results to compute
     */
    public void setResultsToCompute(long resultsToCompute) {
        cSetResultsToCompute(this.cObject, resultsToCompute);
    }

    /**
     * Gets the 64 bit integer flag that indicates the results to compute
     * @return The 64 bit integer flag that indicates the results to compute
     */
    public long getResultsToCompute() {
        return cGetResultsToCompute(this.cObject);
    }

    private native long cGetNTrees(long parAddr);
    private native void cSetNTrees(long parAddr, long value);

    private native void   cSetObservationsPerTreeFraction(long parAddr, double value);
    private native double cGetObservationsPerTreeFraction(long parAddr);

    private native long cGetFeaturesPerNode(long parAddr);
    private native void cSetFeaturesPerNode(long parAddr, long value);

    private native long cGetMaxTreeDepth(long parAddr);
    private native void cSetMaxTreeDepth(long parAddr, long value);

    private native long cGetMinObservationsInLeafNode(long parAddr);
    private native void cSetMinObservationsInLeafNode(long parAddr, long value);

    private native int cGetSeed(long parAddr);
    private native void cSetSeed(long parAddr, int value);

    private native void   cSetImpurityThreshold(long parAddr, double value);
    private native double cGetImpurityThreshold(long parAddr);

    private native void cSetResultsToCompute(long parAddr, long value);
    private native long cGetResultsToCompute(long parAddr);

    private native int cGetVariableImportanceMode(long parAddr);
    private native void cSetVariableImportanceMode(long parAddr, int value);

}
/** @} */
