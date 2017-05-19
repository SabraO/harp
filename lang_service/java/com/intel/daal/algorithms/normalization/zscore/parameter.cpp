/* file: parameter.cpp */
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

/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>

#include "daal.h"
#include "normalization/zscore/JParameter.h"
#include "normalization/zscore/JMethod.h"

#include "common_defines.i"

#define DefaultDense com_intel_daal_algorithms_normalization_zscore_Method_DefaultDense
#define SumDense     com_intel_daal_algorithms_normalization_zscore_Method_SumDense
#define batchValue   com_intel_daal_algorithms_ComputeMode_batchValue

using namespace daal;
using namespace daal::algorithms;
using namespace daal::services;

/*
 * Class:     com_intel_daal_algorithms_normalization_zscore_Parameter
 * Method:    cSetMoments
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_intel_daal_algorithms_normalization_zscore_Parameter_cSetMoments
(JNIEnv *env, jobject thisObj, jlong parAddr, jlong momentsAdr, jint prec, jint method, jint cmode)
{
    using namespace daal::algorithms::normalization::zscore;
    if(method == defaultDense)
    {
        if(cmode == batchValue)
        {
            if(prec == 0) //double
            {
                normalization::zscore::Parameter<double, defaultDense> *parameterAddr = (normalization::zscore::Parameter<double,
                                                                                         defaultDense> *)parAddr;
                // parameterAddr->moments = *((SharedPtr<daal::algorithms::low_order_moments::BatchImpl> *)momentsAdr);
                parameterAddr->moments = staticPointerCast<daal::algorithms::low_order_moments::BatchImpl>(*(SharedPtr<AlgorithmIface> *)momentsAdr);
            }
            else
            {
                normalization::zscore::Parameter<float, defaultDense> *parameterAddr = (normalization::zscore::Parameter<float,
                                                                                        defaultDense> *)parAddr;
                // parameterAddr->moments = *((SharedPtr<daal::algorithms::low_order_moments::BatchImpl> *)momentsAdr);
                parameterAddr->moments = staticPointerCast<daal::algorithms::low_order_moments::BatchImpl>(*(SharedPtr<AlgorithmIface> *)momentsAdr);
            }
        }
    }
}
