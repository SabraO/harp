/* file: NormalDenseBatch.java */
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

/*
 //  Content:
 //     Java example of normal distribution
 ////////////////////////////////////////////////////////////////////////////////
 */

package com.intel.daal.examples.distributions;

import com.intel.daal.algorithms.distributions.*;
import com.intel.daal.algorithms.distributions.normal.*;
import com.intel.daal.data_management.data.NumericTable;
import com.intel.daal.data_management.data.HomogenNumericTable;
import com.intel.daal.examples.utils.Service;
import com.intel.daal.services.DaalContext;

/**
 * <a name="DAAL-EXAMPLE-JAVA-NORMALDENSEBATCH">
 * @example NormalDenseBatch.java
 */
class NormalDenseBatch {
    private static DaalContext context = new DaalContext();

    public static void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
        /* Create input table to fill with random numbers */
        HomogenNumericTable dataTable = new HomogenNumericTable(context, Float.class, 1, 10, NumericTable.AllocationFlag.DoAllocate);

        /* Create the algorithm */
        Batch normal = new Batch(context, Float.class, Method.defaultDense, 0.0, 1.0);

        /* Set the algorithm input */
        normal.input.set(InputId.tableToFill, dataTable);

        /* Perform computations */
        normal.compute();

        /* Print the results */
        Service.printNumericTable("Normal distribution output:", dataTable);

        context.dispose();
    }
}
