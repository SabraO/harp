/* file: Pooling3dIndices.java */
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
 * @defgroup pooling3d Three-dimensional Pooling Layer
 * @brief Contains classes for the three-dimensional (3D) pooling layer
 * @ingroup layers
 * @{
 */
package com.intel.daal.algorithms.neural_networks.layers.pooling3d;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__NEURAL_NETWORKS__LAYERS__POOLING3D__POOLING3DINDICES"></a>
 * \brief Data structure representing the dimension for convolution kernels
 */
public final class Pooling3dIndices {
    private long[] size;     /*!< Array of dimensions for convolution kernels */

    /**
    * Constructs Indices with parameters
    * @param first  The first dimension for convolution kernels
    * @param second  The second dimension for convolution kernels
    * @param third  The third dimension for convolution kernels
    */
    public Pooling3dIndices(long first, long second, long third) {
        size = new long[3];
        size[0] = first;
        size[1] = second;
        size[2] = third;
    }

    /**
     *  Sets the array of dimensions for convolution kernels
    * @param first  The first dimension for convolution kernels
    * @param second  The second dimension for convolution kernels
    * @param third  The third dimension for convolution kernels
    */
    public void setSize(long first, long second, long third) {
        size[0] = first;
        size[1] = second;
        size[2] = third;
    }

    /**
    *  Gets the array of dimensions for convolution kernels
    * @return Array of dimensions for convolution kernels
    */
    public long[] getSize() {
        return size;
    }
}
/** @} */
