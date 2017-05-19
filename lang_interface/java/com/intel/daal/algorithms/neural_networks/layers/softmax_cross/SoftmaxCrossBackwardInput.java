/* file: SoftmaxCrossBackwardInput.java */
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
 * @defgroup softmax_cross_backward Backward Softmax Cross-entropy Layer
 * @brief Contains classes for the backward softmax cross-entropy layer
 * @ingroup softmax_cross
 * @{
 */
package com.intel.daal.algorithms.neural_networks.layers.softmax_cross;

import com.intel.daal.data_management.data.Factory;
import com.intel.daal.data_management.data.Tensor;
import com.intel.daal.services.DaalContext;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__NEURAL_NETWORKS__LAYERS__SOFTMAX_CROSS__SOFTMAXCROSSBACKWARDINPUT"></a>
 * @brief Input object for the backward softmax cross-entropy layer
 */
public final class SoftmaxCrossBackwardInput extends com.intel.daal.algorithms.neural_networks.layers.loss.LossBackwardInput {
    /** @private */
    static {
        System.loadLibrary("JavaAPI");
    }

    public SoftmaxCrossBackwardInput(DaalContext context, long cObject) {
        super(context, cObject);
    }

    /**
     * Sets the input object of the backward softmax cross-entropy layer
     * @param id    Identifier of the input object
     * @param val   Value of the input object
     */
    public void set(SoftmaxCrossLayerDataId id, Tensor val) {
        if (id == SoftmaxCrossLayerDataId.auxProbabilities || id == SoftmaxCrossLayerDataId.auxGroundTruth) {
            cSetInput(cObject, id.getValue(), val.getCObject());
        }
        else {
            throw new IllegalArgumentException("Incorrect SoftmaxCrossLayerDataId");
        }
    }

    /**
     * Returns the input object of the backward softmax cross-entropy layer
     * @param id Identifier of the input object
     * @return   Input object that corresponds to the given identifier
     */
    public Tensor get(SoftmaxCrossLayerDataId id) {
        if (id == SoftmaxCrossLayerDataId.auxProbabilities || id == SoftmaxCrossLayerDataId.auxGroundTruth) {
            return (Tensor)Factory.instance().createObject(getContext(), cGetInput(cObject, id.getValue()));
        }
        else {
            throw new IllegalArgumentException("id unsupported");
        }
    }

    private native void cSetInput(long cObject, int id, long ntAddr);
    private native long cGetInput(long cObject, int id);
}
/** @} */
