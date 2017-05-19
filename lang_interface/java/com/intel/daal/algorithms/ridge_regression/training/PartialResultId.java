/* file: PartialResultId.java */
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
 * @ingroup ridge_regression_training
 * @{
 */
package com.intel.daal.algorithms.ridge_regression.training;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__RIDGE_REGRESSION__TRAINING__PARTIALRESULTID"></a>
 * @brief Available identifiers of a partial result of ridge regression model-based training
 */
public final class PartialResultId {
    private int _value;

    static {
        System.loadLibrary("JavaAPI");
    }

    /**
     * Constructs the partial result object identifier using the provided value
     * @param value     Value corresponding to the partial result object identifier
     */
    public PartialResultId(int value) {
        _value = value;
    }

    /**
     * Returns the value corresponding to the partial result object identifier
     * @return Value corresponding to the partial result object identifier
     */
    public int getValue() {
        return _value;
    }

    private static final int ModelId = 0;

    /** Partial model trained on the available input data */
    public static final PartialResultId model = new PartialResultId(ModelId);
}
/** @} */
